package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.*;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.content.DimStats;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.geometry.HPos;
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
import javafx.util.converter.IntegerStringConverter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import static com.github.cfogrady.dim.modifier.LoadedScene.*;

@Slf4j
@AllArgsConstructor
public class StatsInfoView implements InfoView {
    public static final int MAX_SLOTS_ENTRIES_FOR_NORMAL_VB = 17;

    private final DimContent dimContent;
    private final SpriteSlotParser spriteSlotParser;
    private final Stage stage;
    private final Consumer<SelectionState> stateChanger;
    private BackgroundImage background;
    private File lastUsedDirectory;

    public StatsInfoView(DimContent dimContent, SpriteSlotParser spriteSlotParser,  Stage stage, Consumer<SelectionState> stateChanger) {
        this.dimContent = dimContent;
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
        return 750;
    }

    public int getSpriteCountForSelection(SelectionState selectionState) {
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            return 1;
        } else if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
            return 8;
        } else {
            int level = getSelectionLevel(selectionState);
            if(level == 0) {
                return 6;
            } else if (level == 1) {
                return 7;
            } else {
                return 14;
            }
        }
    }

    private int getSelectionLevel(SelectionState selectionState) {
        return dimContent.getDimStats().getStatBlocks().get(selectionState.getSlot()).getStage();
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
        DimStats.DimStatBlock statBlock = dimContent.getDimStats().getStatBlocks().get(slot);
        gridPane.add(setupStageLabel(selectionState, statBlock), 0, 0);
        gridPane.add(setupLockedLabel(selectionState, statBlock), 1, 0);
        gridPane.add(setupAttributeLabel(statBlock), 0, 1);
        gridPane.add(setupDispositionLabel(statBlock), 1, 1);
        gridPane.add(setupSmallAttackLabel(statBlock), 0, 2);
        gridPane.add(setupBigAttackLabel(statBlock), 1, 2);
        gridPane.add(setupDPStarsLabel(statBlock), 0, 3);
        gridPane.add(setupDPLabel(selectionState, statBlock), 1, 3);
        gridPane.add(setupHpLabel(statBlock), 0, 4);
        gridPane.add(setupApLabel(statBlock), 1, 4);
        gridPane.add(setupEarlyBattleChanceLabel(statBlock), 0, 5);
        gridPane.add(setupLateBattleChanceLabel(statBlock), 1, 5);
        return gridPane;
    }

    private Node setupStageLabel(SelectionState selectionState, DimStats.DimStatBlock statBlock) {
        Label label = new Label("Stage: ");
        ComboBox<Integer> comboBox = new ComboBox<Integer>();
        comboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6));
        comboBox.setValue(statBlock.getStage() + 1);
        comboBox.setOnAction(event -> {
            int value = comboBox.getValue() - 1;
            this.dimContent.getDimStats().getStatBlocks().set(selectionState.getSlot(), statBlock.toBuilder().stage(value).build());
        });
        comboBox.setPrefWidth(20);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupLockedLabel(SelectionState selectionState, DimStats.DimStatBlock statBlock) {
        Label label = new Label("Requires Unlock:");
        ComboBox<Boolean> comboBox = new ComboBox<Boolean>();
        comboBox.setItems(FXCollections.observableArrayList(false, true));
        comboBox.setValue(statBlock.isUnlockRequired());
        comboBox.setOnAction(event -> {
            boolean value = comboBox.getValue();
            this.dimContent.getDimStats().getStatBlocks().set(selectionState.getSlot(), statBlock.toBuilder().unlockRequired(value).build());
        });
        comboBox.setPrefWidth(80);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupAttributeLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Attribute: " + statBlock.getAttribute());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupDispositionLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Disposition: " + statBlock.getDisposition());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupSmallAttackLabel(DimStats.DimStatBlock statBlock) {
        String attackLabel;
        if(statBlock.getStage() < 2) {
            attackLabel = LoadedScene.NONE_LABEL;
        } else {
            attackLabel = AttackLabels.SMALL_ATTACKS[statBlock.getSmallAttackId()];
        }
        Label label = new Label("Small Attack: " + attackLabel);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupBigAttackLabel(DimStats.DimStatBlock statBlock) {
        String attackLabel;
        if(statBlock.getStage() < 2) {
            attackLabel = LoadedScene.NONE_LABEL;
            log.info("Value is: {}", statBlock.getBigAttackId());
        } else {
            attackLabel = AttackLabels.BIG_ATTACKS[statBlock.getBigAttackId()];
        }
        Label label = new Label("Big Attack: " + attackLabel);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupDPStarsLabel(DimStats.DimStatBlock statBlock) {
        String valueText;
        if(statBlock.getDpStars() == NONE_VALUE) {
            valueText = NONE_LABEL;
        } else {
            valueText = Integer.toString(statBlock.getDpStars());
        }
        Label label = new Label("DP (stars): " + valueText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupDPLabel(SelectionState selectionState, DimStats.DimStatBlock statBlock) {
        if(statBlock.getStage() < 2) {
            Label label = new Label("DP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("DP:");
        TextField textField = new TextField();
        textField.setText(Integer.toString(statBlock.getDp()));
        textField.textProperty().addListener((obs,oldv,newv) -> {
            boolean error = false;
            if(newv == null || newv.isBlank()) {
                error = true;
            } else {
                try {
                    int value = Integer.parseInt(newv);
                    if(value < 0) {
                        error = true;
                    } else {
                        textField.setBorder(null);
                        this.dimContent.getDimStats().getStatBlocks().set(selectionState.getSlot(), statBlock.toBuilder().dp(value).build());
                    }
                } catch (NumberFormatException e) {
                    error = true;
                }
            }
            if(error) {
                textField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            }
        });
        textField.setPrefWidth(60);
        HBox hbox = new HBox(label, textField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupHpLabel(DimStats.DimStatBlock statBlock) {
        String valueText;
        if(statBlock.getDp() == NONE_VALUE) {
            valueText = NONE_LABEL;
        } else {
            valueText = Integer.toString(statBlock.getHp());
        }
        Label label = new Label("HP: " + valueText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupApLabel(DimStats.DimStatBlock statBlock) {
        String valueText;
        if(statBlock.getDp() == NONE_VALUE) {
            valueText = NONE_LABEL;
        } else {
            valueText = Integer.toString(statBlock.getAp());
        }
        Label label = new Label("AP: " + valueText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupEarlyBattleChanceLabel(DimStats.DimStatBlock statBlock) {
        String chance;
        if(statBlock.getFirstPoolBattleChance() == NONE_VALUE) {
            chance = LoadedScene.NONE_LABEL;
        } else {
            chance = Integer.toString(statBlock.getFirstPoolBattleChance());
        }
        Label label = new Label("Stage 3/4 Battle Chance: " + chance);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupLateBattleChanceLabel(DimStats.DimStatBlock statBlock) {
        String chance;
        if(statBlock.getSecondPoolBattleChance() == NONE_VALUE) {
            chance = LoadedScene.NONE_LABEL;
        } else {
            chance = Integer.toString(statBlock.getSecondPoolBattleChance());
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
        button.setOnAction(event -> {
            stateChanger.accept(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() - 1).build());
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
            stateChanger.accept(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() + 1).build());
        });
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
            SpriteData.Sprite nameSprite = spriteSlotParser.getSpriteForSlotAndIndex(selectionType, slot, 0);
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
                        spriteSlotParser.loadReplacementSprite(file, selectionState.getSelectionType(), selectionState.getSlot(), 0);
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
        if(selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSlot() == dimContent.getDimStats().getStatBlocks().size() - 1) {
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
