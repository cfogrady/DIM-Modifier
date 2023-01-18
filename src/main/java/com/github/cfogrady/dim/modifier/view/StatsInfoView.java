package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.*;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.controls.StringIntComboBox;
import com.github.cfogrady.dim.modifier.data.DimContentFactory;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.DimDataFactory;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.github.cfogrady.dim.modifier.LoadedScene.*;

@Slf4j
@AllArgsConstructor
public class StatsInfoView implements InfoView {

    private final DimData dimData;
    private final SpriteImageTranslator spriteImageTranslator;
    private final Stage stage;
    private final Runnable sceneRefresher;
    private BackgroundImage background;

    public StatsInfoView(DimData dimData, SpriteImageTranslator spriteSlotParser, Stage stage, Runnable stateChanger) {
        this.dimData = dimData;
        this.spriteImageTranslator = spriteSlotParser;
        this.stage = stage;
        this.sceneRefresher = stateChanger;
        BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
        Image image = spriteSlotParser.loadImageFromSprite(dimData.getBackGroundSprite());
        this.background = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
    }

    @Override
    public Node setupView(SelectionState selectionState) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        HBox hbox = new HBox();
        vbox.getChildren().add(hbox);
        hbox.getChildren().add(setupSpriteArea(selectionState));
        hbox.getChildren().add(setupStatArea(selectionState));
        return vbox;
    }

    @Override
    public double getPrefWidth() {
        return 870;
    }

    public int getSpriteCountForSelection(SelectionState selectionState) {
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            return 1;
        } else if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
            return 8;
        } else {
            int level = getSelectionLevel(selectionState);
            int spriteCount = DimDataFactory.getSpriteCountForLevel(level);
            return spriteCount;
        }
    }

    private int getSelectionLevel(SelectionState selectionState) {
        return dimData.getMonsterSlotList().get(selectionState.getSlot()).getStatBlock().getStage();
    }

    private Node setupSpriteArea(SelectionState selectionState) {
        SpriteData.Sprite sprite;
        if(selectionState.getSelectionType() == CurrentSelectionType.SLOT) {
            MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(selectionState.getSlot());
            if(selectionState.getSpriteIndex() >= monsterSlot.getSprites().size()) {
                monsterSlot.getSprites().add(SpriteData.Sprite.builder().width(1).height(1).pixelData(DimContentFactory.createDummySprite(1, 1)).build());
            }
            sprite = monsterSlot.getSprites().get(selectionState.getSpriteIndex());
        } else if (selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            sprite = dimData.getLogoSprite();
        } else {
            sprite = dimData.getEggSprites().get(selectionState.getSpriteIndex());
        }
        log.info("Sprite Displayed size {}x{}. Being displayed at {}x{}: ", sprite.getWidth(), sprite.getHeight(), sprite.getWidth()*2, sprite.getHeight()*2);
        Image image = spriteImageTranslator.loadImageFromSprite(sprite);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(sprite.getWidth() * 2.0);
        imageView.setFitHeight(sprite.getHeight() * 2.0);

        VBox vbox = new VBox(getChangeBackgroundButton(selectionState), setupSpriteBackground(selectionState, imageView));
        HBox hBox = new HBox(setupPrevSpriteButton(selectionState), setupReplaceSpriteButton(selectionState), setupNextSpriteButton(selectionState));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER); //children take up as much space as they can, so we need to either align here, or control width of the parent.
        vbox.getChildren().add(hBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        return vbox;
    }

    private Node setupSpriteBackground(SelectionState selectionState, ImageView spriteImageView) {
        StackPane backgroundPane = new StackPane(spriteImageView);
        backgroundPane.setAlignment(Pos.BOTTOM_CENTER);
        backgroundPane.setBackground(getBackground(selectionState)); //160x320
        backgroundPane.setMinSize(160.0, 320.0);
        backgroundPane.setMaxSize(160.0, 320.0);
        backgroundPane.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                replaceSpriteWithFile(file, selectionState);
            }
        });
        backgroundPane.setOnDragOver(e -> {
            if (e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.ANY);
                log.info("Drag Over Image");
                e.consume();
            } else if(e.getDragboard().hasFiles()) {
                if (e.getDragboard().getFiles().size() > 1) {
                    log.info("Can only load 1 file at a time");
                } else {
                    e.acceptTransferModes(TransferMode.ANY);
                    e.consume();
                }
            }
        });
        return backgroundPane;
    }

    private void replaceSpriteWithFile(File file, SelectionState selectionState) {
        if(file != null) {
            try {
                SpriteData.Sprite newSprite = spriteImageTranslator.loadSprite(file);
                setSprite(selectionState, newSprite);
                sceneRefresher.run();
            } catch (IOException ioe) {
                log.error("Couldn't load image file!", ioe);
            }
        }
    }

    private Node setupStatArea(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        if(selectionState.getSelectionType() != CurrentSelectionType.SLOT) {
            return gridPane;
        }
        int slot = selectionState.getSlot();
        MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(slot);
        gridPane.add(setupStageLabel(selectionState, monsterSlot), 0, 0);
        gridPane.add(setupLockedLabel(selectionState, monsterSlot), 1, 0);
        gridPane.add(setupAttributeLabel(selectionState, monsterSlot), 0, 1);
        gridPane.add(setupDispositionLabel(selectionState, monsterSlot), 1, 1);
        gridPane.add(setupSmallAttackLabel(monsterSlot), 0, 2);
        gridPane.add(setupBigAttackLabel(monsterSlot), 1, 2);
        gridPane.add(setupDPStarsLabel(monsterSlot), 0, 3);
        gridPane.add(setupDPLabel(monsterSlot), 1, 3);
        gridPane.add(setupHpLabel(monsterSlot), 0, 4);
        gridPane.add(setupApLabel(monsterSlot), 1, 4);
        return gridPane;
    }

    private Node setupStageLabel(SelectionState selectionState, MonsterSlot monsterSlot) {
        Label label = new Label("Stage: ");
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setValue(monsterSlot.getStatBlock().getStage() + 1);
        if(selectionState.isSafetyModeOn()) {
            if(selectionState.isOnBabySlot()) {
                comboBox.setItems(FXCollections.observableArrayList(monsterSlot.getStatBlock().getStage() + 1));
                comboBox.setDisable(true);
            } else {
                comboBox.setItems(FXCollections.observableArrayList(3, 4, 5, 6));
            }
        } else {
            comboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6));
        }
        comboBox.setOnAction(event -> {
            int value = comboBox.getValue() - 1;
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().stage(value).build());
            this.sceneRefresher.run();
        });
        comboBox.setPrefWidth(20);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupLockedLabel(SelectionState selectionState, MonsterSlot monsterSlot) {
        Label label = new Label("Requires Unlock:");
        ComboBox<Boolean> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(false, true));
        comboBox.setValue(monsterSlot.getStatBlock().isUnlockRequired());
        if(selectionState.isSafetyModeOn() && selectionState.isOnBabySlot()) {
            comboBox.setDisable(true);
        }
        comboBox.setOnAction(event -> {
            boolean value = comboBox.getValue();
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().unlockRequired(value).build());
        });
        comboBox.setPrefWidth(80);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupAttributeLabel(SelectionState selectionState, MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("Attribute: " + monsterSlot.getStatBlock().getAttribute());
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Attribute:");
        StringIntComboBox comboBox = new StringIntComboBox(monsterSlot.getStatBlock().getAttribute(), getAttributes(), value -> {
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().attribute(value).build());
        });
        if(selectionState.isSafetyModeOn() && selectionState.isOnBabySlot()) {
            comboBox.setDisable(true);
        }
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private ObservableList<StringIntComboBox.StringIntPair> getAttributes() {
        StringIntComboBox.StringIntPair virus = new StringIntComboBox.StringIntPair("Virus", 1);
        StringIntComboBox.StringIntPair data = new StringIntComboBox.StringIntPair("Data", 2);
        StringIntComboBox.StringIntPair vaccine = new StringIntComboBox.StringIntPair("Vaccine", 3);
        StringIntComboBox.StringIntPair free = new StringIntComboBox.StringIntPair("Free", 4);
        return FXCollections.observableArrayList(virus, data, vaccine, free);

    }

    private Node setupDispositionLabel(SelectionState selectionState, MonsterSlot monsterSlot) {
        Label label = new Label("Disposition:");
        StringIntComboBox comboBox = new StringIntComboBox(monsterSlot.getStatBlock().getDisposition(), getDispositions(), value -> {
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().disposition(value).build());
        });
        if(selectionState.isSafetyModeOn() && selectionState.isOnBabySlot()) {
            comboBox.setDisable(true);
        }
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private ObservableList<StringIntComboBox.StringIntPair> getDispositions() {
        StringIntComboBox.StringIntPair stoic = new StringIntComboBox.StringIntPair("Stoic", 0);
        StringIntComboBox.StringIntPair active = new StringIntComboBox.StringIntPair("Active", 1);
        StringIntComboBox.StringIntPair normal = new StringIntComboBox.StringIntPair("Normal", 2);
        StringIntComboBox.StringIntPair indoor = new StringIntComboBox.StringIntPair("Indoor", 3);
        StringIntComboBox.StringIntPair lazy = new StringIntComboBox.StringIntPair("Lazy", 4);
        return FXCollections.observableArrayList(stoic, active, normal, indoor, lazy);

    }

    private Node setupSmallAttackLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("Small Attack: " + LoadedScene.NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Small Attack:");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(AttackLabels.SMALL_ATTACKS));
        comboBox.setValue(AttackLabels.SMALL_ATTACKS[NoneUtils.defaultIfNone(monsterSlot.getStatBlock().getSmallAttackId(), 0)]);
        comboBox.setOnAction(event -> {
            String value = comboBox.getValue();
            boolean indexFound = false;
            for(int i = 0 ; i < AttackLabels.SMALL_ATTACKS.length && !indexFound; i++) {
                if(AttackLabels.SMALL_ATTACKS[i].equals(value)) {
                    monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().smallAttackId(i).build());
                    indexFound = true;
                }
            }
        });
        comboBox.setPrefWidth(200);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupBigAttackLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("Big Attack: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Big Attack:");
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(AttackLabels.BIG_ATTACKS));
        comboBox.setValue(AttackLabels.BIG_ATTACKS[NoneUtils.defaultIfNone(monsterSlot.getStatBlock().getBigAttackId(), 0)]);
        comboBox.setOnAction(event -> {
            String value = comboBox.getValue();
            boolean indexFound = false;
            for(int i = 0 ; i < AttackLabels.BIG_ATTACKS.length && !indexFound; i++) {
                if(AttackLabels.BIG_ATTACKS[i].equals(value)) {
                    monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().bigAttackId(i).build());
                    indexFound = true;
                }
            }
        });
        comboBox.setPrefWidth(180);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupDPStarsLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("DP (stars): " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("DP (stars):");
        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getStatBlock().getDpStars(), value ->
                monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().dpStars(value).build()));
        integerTextField.setPrefWidth(60);
        integerTextField.setMax(10);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupDPLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("DP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("DP:");
        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getStatBlock().getDp(), value ->
                monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().dp(value).build()));
        integerTextField.setPrefWidth(60);
        integerTextField.setMax(75);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupHpLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("HP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("HP:");
        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getStatBlock().getHp(), value ->
                monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().hp(value).build()));
        integerTextField.setPrefWidth(60);
        integerTextField.setMax(22);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupApLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("AP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("AP:");
        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getStatBlock().getAp(), value ->
                monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().ap(value).build()));
        integerTextField.setMax(9);
        integerTextField.setPrefWidth(60);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupPrevSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Prev Sprite");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO ||
                selectionState.getSpriteIndex() == 0 ||
                (selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSpriteIndex() < 2)) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            selectionState.setSpriteIndex(selectionState.getSpriteIndex() - 1);
            sceneRefresher.run();
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupNextSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Next Sprite");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO || selectionState.getSpriteIndex() == getSpriteCountForSelection(selectionState)-1) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            selectionState.setSpriteIndex(selectionState.getSpriteIndex() + 1);
            sceneRefresher.run();
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupReplaceSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Replace Sprite");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            SpriteData.Sprite currentSprite = selectionState.getSprite(dimData);
            fileChooser.setTitle("Select sprite replacement. Should be " + currentSprite.getWidth() + " x " + currentSprite.getHeight());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image format", "*.png", "*.bmp"));
            if(selectionState.getLastFileOpenPath() != null) {
                fileChooser.setInitialDirectory(selectionState.getLastFileOpenPath());
            }
            File file = fileChooser.showOpenDialog(stage);
            replaceSpriteWithFile(file, selectionState);
        });
        return button;
    }

    private void setSprite(SelectionState selectionState, SpriteData.Sprite sprite) {
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            dimData.setLogoSprite(sprite);
        } else if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
            dimData.getEggSprites().set(selectionState.getSpriteIndex(), sprite);
        } else {
            dimData.getMonsterSlotList().get(selectionState.getSlot()).getSprites().set(selectionState.getSpriteIndex(), sprite);
        }
    }

    private Node getChangeBackgroundButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Change Background Image");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image format", "*.png", "*.bmp"));
            fileChooser.setTitle("Select background. Recommend 80x160 resolution. May not work with other resolutions");
            if(selectionState.getLastFileOpenPath() != null) {
                fileChooser.setInitialDirectory(selectionState.getLastFileOpenPath());
            }
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                try {
                    selectionState.setLastFileOpenPath(file.getParentFile());
                    SpriteData.Sprite sprite = spriteImageTranslator.loadSprite(file);
                    dimData.setBackGroundSprite(sprite);
                    BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
                    this.background = new BackgroundImage(spriteImageTranslator.loadImageFromSprite(sprite), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
                    sceneRefresher.run();
                } catch (IOException e) {
                    log.error("Couldn't load image file!", e);
                }
            }
        });
        return button;
    }

    private Background getBackground(SelectionState selectionState) {
        if(selectionState.getBackgroundType() == BackgroundType.IMAGE) {
            return new Background(this.background);
        } else if(selectionState.getBackgroundType() == BackgroundType.BLUE) {
            return new Background(new BackgroundFill(Color.color(0, 0, 1), CornerRadii.EMPTY, Insets.EMPTY));
        } else if (selectionState.getBackgroundType() == BackgroundType.GREEN) {
            return new Background(new BackgroundFill(Color.color(0, 1, 0), CornerRadii.EMPTY, Insets.EMPTY));
        } else if (selectionState.getBackgroundType() == BackgroundType.ORANGE) {
            return new Background(new BackgroundFill(Color.color(1, 165.0D/255.0D, 0), CornerRadii.EMPTY, Insets.EMPTY));
        } else {
            throw new UnsupportedOperationException("Unhandled background type!");
        }
    }
}
