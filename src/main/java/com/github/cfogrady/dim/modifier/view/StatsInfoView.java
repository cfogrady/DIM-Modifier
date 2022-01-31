package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.*;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.DimContentFactory;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.DimDataFactory;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.vb.dim.reader.content.DimStats;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static com.github.cfogrady.dim.modifier.LoadedScene.*;

@Slf4j
@AllArgsConstructor
public class StatsInfoView implements InfoView {

    private final DimData dimData;
    private final SpriteSlotParser spriteSlotParser;
    private final Stage stage;
    private final Consumer<SelectionState> stateChanger;
    private BackgroundImage background;
    private File lastUsedDirectory;

    public StatsInfoView(DimData dimData, SpriteSlotParser spriteSlotParser, Stage stage, Consumer<SelectionState> stateChanger) {
        this.dimData = dimData;
        this.spriteSlotParser = spriteSlotParser;
        this.stage = stage;
        this.stateChanger = stateChanger;
        BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
        this.background = new BackgroundImage(spriteSlotParser.loadImageFromSpriteIndex(BACKGROUND_INDEX), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        this.lastUsedDirectory = null;
    }

    @Override
    public Node setupView(SelectionState selectionState) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().add(setupNameArea(selectionState));
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

    private Node setupNameArea(SelectionState selectionState) {
        HBox hBox = new HBox(setupPrevButton(selectionState), setupName(selectionState), setupNextButton(selectionState));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private Node setupSpriteArea(SelectionState selectionState) {
        SpriteData.Sprite sprite = spriteSlotParser.getSpriteForSlotAndIndex(selectionState.getSelectionType(), selectionState.getSlot(), selectionState.getSpriteIndex());
        Image image = spriteSlotParser.loadImageFromSprite(sprite);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(sprite.getWidth() * 2.0);
        imageView.setFitHeight(sprite.getHeight() * 2.0);
        StackPane backgroundPane = new StackPane(imageView);
        backgroundPane.setAlignment(Pos.BOTTOM_CENTER);
        backgroundPane.setBackground(getBackground(selectionState)); //160x320
        backgroundPane.setMinSize(160.0, 320.0);
        backgroundPane.setMaxSize(160.0, 320.0);
        VBox vbox = new VBox(getChangeBackgroundButton(selectionState), backgroundPane);
        HBox hBox = new HBox(setupPrevSpriteButton(selectionState), setupReplaceSpriteButton(selectionState), setupNextSpriteButton(selectionState));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER); //children take up as much space as they can, so we need to either align here, or control width of the parent.
        vbox.getChildren().add(hBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        return vbox;
    }

    private Node setupStatArea(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        if(selectionState.getSelectionType() != CurrentSelectionType.SLOT) {
            return gridPane;
        }
        int slot = selectionState.getSlot();
        MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(slot);
        gridPane.add(setupStageLabel(monsterSlot, selectionState), 0, 0);
        gridPane.add(setupLockedLabel(monsterSlot), 1, 0);
        gridPane.add(setupAttributeLabel(monsterSlot), 0, 1);
        gridPane.add(setupDispositionLabel(monsterSlot), 1, 1);
        gridPane.add(setupSmallAttackLabel(monsterSlot), 0, 2);
        gridPane.add(setupBigAttackLabel(monsterSlot), 1, 2);
        gridPane.add(setupDPStarsLabel(monsterSlot), 0, 3);
        gridPane.add(setupDPLabel(monsterSlot), 1, 3);
        gridPane.add(setupHpLabel(monsterSlot), 0, 4);
        gridPane.add(setupApLabel(monsterSlot), 1, 4);
        gridPane.add(setupHoursUntilEvolutionLabel(monsterSlot), 0, 5);
        gridPane.add(setupEarlyBattleChanceLabel(monsterSlot), 0, 6);
        gridPane.add(setupLateBattleChanceLabel(monsterSlot), 1, 6);
        return gridPane;
    }

    private Node setupStageLabel(MonsterSlot monsterSlot, SelectionState selectionState) {
        Label label = new Label("Stage: ");
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6));
        comboBox.setValue(monsterSlot.getStatBlock().getStage() + 1);
        comboBox.setOnAction(event -> {
            int value = comboBox.getValue() - 1;
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().stage(value).build());
            this.stateChanger.accept(selectionState);
        });
        comboBox.setPrefWidth(20);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupLockedLabel(MonsterSlot monsterSlot) {
        Label label = new Label("Requires Unlock:");
        ComboBox<Boolean> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(false, true));
        comboBox.setValue(monsterSlot.getStatBlock().isUnlockRequired());
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

    private Node setupAttributeLabel(MonsterSlot monsterSlot) {
        if(monsterSlot.getStatBlock().getStage() < 2) {
            Label label = new Label("Attribute: " + monsterSlot.getStatBlock().getAttribute());
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Attribute:");
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        comboBox.setValue(monsterSlot.getStatBlock().getAttribute());
        comboBox.setOnAction(event -> {
            int value = comboBox.getValue();
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().attribute(value).build());
        });
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupDispositionLabel(MonsterSlot monsterSlot) {
        Label label = new Label("Disposition:");
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(0, 1, 2, 3, 4));
        comboBox.setValue(monsterSlot.getStatBlock().getDisposition());
        comboBox.setOnAction(event -> {
            int value = comboBox.getValue();
            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().disposition(value).build());
        });
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
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
        comboBox.setValue(AttackLabels.SMALL_ATTACKS[monsterSlot.getStatBlock().getSmallAttackId()]);
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
        comboBox.setValue(AttackLabels.BIG_ATTACKS[monsterSlot.getStatBlock().getBigAttackId()]);
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
        integerTextField.setPrefWidth(60);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupHoursUntilEvolutionLabel(MonsterSlot monsterSlot) {
        Label label = new Label("Hours Until Evolution (" + DimReader.NONE_VALUE + " for NONE):");
        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getHoursUntilEvolution(), value ->
                monsterSlot.setHoursUntilEvolution(value));
        integerTextField.setPrefWidth(60);
        integerTextField.setMin(1);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupEarlyBattleChanceLabel(MonsterSlot monsterSlot) {
        String chance;
        if(monsterSlot.getStatBlock().getFirstPoolBattleChance() == NONE_VALUE) {
            chance = LoadedScene.NONE_LABEL;
        } else {
            chance = Integer.toString(monsterSlot.getStatBlock().getFirstPoolBattleChance());
        }
        Label label = new Label("Stage 3/4 Battle Chance: " + chance);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupLateBattleChanceLabel(MonsterSlot monsterSlot) {
        String chance;
        if(monsterSlot.getStatBlock().getSecondPoolBattleChance() == NONE_VALUE) {
            chance = LoadedScene.NONE_LABEL;
        } else {
            chance = Integer.toString(monsterSlot.getStatBlock().getSecondPoolBattleChance());
        }
        Label label = new Label("Stage 5/6 Battle Chance: " + chance);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupPrevSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Prev Sprite");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO ||
                selectionState.getSpriteIndex() == 0 ||
                (selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSpriteIndex() < 2)) {
            button.setDisable(true);
        }
        button.setOnAction(event -> stateChanger.accept(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() - 1).build()));
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupNextSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Next Sprite");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO || selectionState.getSpriteIndex() == getSpriteCountForSelection(selectionState)-1) {
            button.setDisable(true);
        }
        button.setOnAction(event -> stateChanger.accept(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() + 1).build()));
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupReplaceSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Replace Sprite");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            SpriteData.Sprite currentSprite = spriteSlotParser.getSpriteForSlotAndIndex(selectionState.getSelectionType(), selectionState.getSlot(), selectionState.getSpriteIndex());
            fileChooser.setTitle("Select sprite replacement. Should be " + currentSprite.getWidth() + " x " + currentSprite.getHeight());
            if(lastUsedDirectory != null) {
                fileChooser.setInitialDirectory(lastUsedDirectory);
            }
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                try {
                    lastUsedDirectory = file.getParentFile();
                    spriteSlotParser.loadReplacementSprite(file, selectionState.getSelectionType(), selectionState.getSlot(), selectionState.getSpriteIndex());
                    stateChanger.accept(selectionState);
                } catch (IOException e) {
                    log.error("Couldn't load image file!", e);
                }
            }
        });
        return button;
    }

    private Node getChangeBackgroundButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Change Background Image");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select background. Recommend 80x160 resolution. May not work with other resolutions");
            if(lastUsedDirectory != null) {
                fileChooser.setInitialDirectory(lastUsedDirectory);
            }
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                try {
                    lastUsedDirectory = file.getParentFile();
                    spriteSlotParser.loadReplacementSprite(file, 1);
                    BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
                    this.background = new BackgroundImage(spriteSlotParser.loadImageFromSpriteIndex(BACKGROUND_INDEX), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
                    stateChanger.accept(selectionState);
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

    private Node setupName(SelectionState selectionState) {
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        int slot = selectionState.getSlot();
        if(selectionType == CurrentSelectionType.LOGO) {
            return new Label("LOGO");
        } else if (selectionType == CurrentSelectionType.EGG) {
            return new Label("EGG");
        } else {
            SpriteData.Sprite nameSprite = dimData.getMonsterSlotList().get(slot).getSprites().get(0);
            Image image = spriteSlotParser.loadImageFromSprite(nameSprite);
            ImageView imageView = new ImageView(image);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select name sprite replacement. Should be have height of " + nameSprite.getHeight());
                if(lastUsedDirectory != null) {
                    fileChooser.setInitialDirectory(lastUsedDirectory);
                }
                File file = fileChooser.showOpenDialog(stage);
                if(file != null) {
                    try {
                        lastUsedDirectory = file.getParentFile();
                        SpriteData.Sprite replacementSprite = spriteSlotParser.loadSprite(file);
                        dimData.getMonsterSlotList().get(slot).getSprites().set(0, replacementSprite);
                        stateChanger.accept(selectionState);
                    } catch (IOException e) {
                        log.error("Couldn't load image file!", e);
                    }
                }
            });
            StackPane stackPane = new StackPane(imageView);
            stackPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
            return stackPane;
        }
    }

    private Node setupPrevButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Prev");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            SelectionState.SelectionStateBuilder newStateBuilder = selectionState.toBuilder();
            if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
                newStateBuilder.selectionType(CurrentSelectionType.LOGO);
            } else if (selectionState.getSlot() == 0) {
                newStateBuilder.selectionType(CurrentSelectionType.EGG).spriteIndex(0);
            } else {
                newStateBuilder.slot(selectionState.getSlot() - 1).spriteIndex(1);
            }
            stateChanger.accept(newStateBuilder.build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupNextButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Next");
        if(selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSlot() == dimData.getMonsterSlotList().size() - 1) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            SelectionState.SelectionStateBuilder newStateBuilder = selectionState.toBuilder();
            if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
                newStateBuilder.selectionType(CurrentSelectionType.EGG);
            } else if (selectionState.getSelectionType() == CurrentSelectionType.EGG) {
                newStateBuilder.selectionType(CurrentSelectionType.SLOT).spriteIndex(1);
            } else {
                newStateBuilder.slot(selectionState.getSlot() + 1).spriteIndex(1);
            }
            stateChanger.accept(newStateBuilder.build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }
}
