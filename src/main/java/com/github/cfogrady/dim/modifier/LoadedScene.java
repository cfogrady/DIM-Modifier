package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.DimContentFactory;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.DimDataFactory;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.dim.modifier.view.*;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import com.github.cfogrady.vb.dim.reader.writer.DimWriter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor()
public class LoadedScene {
    public static final int NONE_VALUE = 65535;
    public static final int BACKGROUND_INDEX = 1;
    public static final String NONE_LABEL = "None";

    private final DimContent dimContent;
    private final DimData dimData;
    private final DimDataFactory dimDataFactory;
    private final DimContentFactory dimContentFactory;
    private final Stage stage;
    private final FusionInfoView fusionInfoView;
    private final StatsInfoView statsInfoView;
    private final EvolutionInfoView evolutionInfoView;
    private final BattleInfoView battleInfoView;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SafetyValidator safetyValidator;
    private InfoView currentView;

    public LoadedScene(DimContent dimContent, DimData dimData, Stage stage) {
        this.dimContent = dimContent;
        this.dimData = dimData;
        this.stage = stage;
        Consumer<SelectionState> stateChanger = this::setupScene;
        this.spriteImageTranslator = new SpriteImageTranslator();
        this.fusionInfoView = new FusionInfoView(dimData, spriteImageTranslator, stateChanger);
        this.statsInfoView = new StatsInfoView(dimData, spriteImageTranslator, stage, stateChanger);
        this.evolutionInfoView = new EvolutionInfoView(dimData, spriteImageTranslator);
        this.battleInfoView = new BattleInfoView(dimData, spriteImageTranslator);
        this.safetyValidator = new SafetyValidator();
        this.dimDataFactory = new DimDataFactory();
        this.dimContentFactory = new DimContentFactory();
        this.currentView = statsInfoView;
    }

    public void setupScene(SelectionState selectionState) {
        if(this.currentView == null) {
            this.currentView = statsInfoView;
        }
        VBox vbox = new VBox();
        vbox.getChildren().add(setupHeaderButtons(selectionState));
        if(currentView != battleInfoView) {
            vbox.getChildren().add(setupNameButtons(selectionState));
        }
        vbox.getChildren().add(this.currentView.setupView(selectionState));
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        javafx.scene.Scene scene = new Scene(vbox, currentView.getPrefWidth(), 720);
        log.info("Setting scene");
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            log.info("Key pressed: {}", key.getCode().getName());
            if(key.getCode() == KeyCode.A) {
                if(selectionState.getSpriteIndex() > 0) {
                    setupScene(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() - 1).build());
                }
            } else if (key.getCode() == KeyCode.D) {
                if(selectionState.getSpriteIndex() < statsInfoView.getSpriteCountForSelection(selectionState) - 1) {
                    setupScene(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() + 1).build());
                }
            } else if(key.getCode() == KeyCode.B) {
                changeBackground(selectionState);
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            log.info("Event: {}", event);
        });
        stage.setScene(scene);
        stage.show();
    }

    private void changeBackground(SelectionState selectionState) {
        SelectionState.SelectionStateBuilder builder = selectionState.toBuilder();
        builder.backgroundType(BackgroundType.nextBackground(selectionState.getBackgroundType()));
        setupScene(builder.build());
    }

    private Node setupHeaderButtons(SelectionState selectionState) {
        HBox hBox = new HBox(setupOpenButton(), setupSaveButton(selectionState), setupSafetyCheck(selectionState),
                setupDIMIdLabel(), setupChecksumLabel(), setupStatsViewButton(selectionState), setupEvolutionsViewButton(selectionState),
                setupFusionButton(selectionState), setupBattlesViewButton(selectionState));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        return hBox;
    }

    private Node setupNameButtons(SelectionState selectionState) {
        HBox hBox = new HBox(setupPrevButton(selectionState),
                setupName(selectionState),
                setupNextButton(selectionState),
                setupReplaceNameSpriteButton(selectionState),
                setupAddSlotButton(selectionState),
                setupDeleteSlotButton(selectionState));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        return hBox;
    }

    private Label setupDIMIdLabel() {
        return new Label("DIM ID: " + dimContent.getDimHeader().getDimId());
    }

    private Label setupChecksumLabel() {
        return new Label("Checksum: " + Integer.toHexString(dimContent.getChecksum()));
    }

    private Button setupEvolutionsViewButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Evolutions");
        if(currentView == evolutionInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = evolutionInfoView;
            setupScene(selectionState);
        });
        return button;
    }

    private static final String SAFETY_CHECK_TOOLTIP = "If this value is checked, the save button will be disabled until all values are within " +
            "official ranges. Using unofficial ranges (especially with sprites) may result in buffer overflows of devices causing freezes or at worst " +
            "bricking the device. Only uncheck this if you know what you are doing or are ok risking damage to your device.";

    private Node setupSafetyCheck(SelectionState selectionState) {
        Tooltip tooltip = new Tooltip(SAFETY_CHECK_TOOLTIP);
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(200);
        Label label = new Label("Only Safe Values:");
        label.setTooltip(tooltip);
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(selectionState.isSafetyModeOn());
        checkBox.setOnAction(e -> {
            selectionState.setSafetyModeOn(!selectionState.isSafetyModeOn());
            setupScene(selectionState);
        });
        checkBox.setTooltip(tooltip);
        HBox hBox = new HBox(label, checkBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private Button setupStatsViewButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Stats");
        if(currentView == statsInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = statsInfoView;
            setupScene(selectionState);
        });
        return button;
    }

    private Button setupBattlesViewButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Battles");
        if(currentView == battleInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = battleInfoView;
            setupScene(selectionState);
        });
        return button;
    }

    private Button setupFusionButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Fusions");
        if(currentView == fusionInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = fusionInfoView;
            setupScene(selectionState);
        });
        return button;
    }

    private Button setupOpenButton() {
        Button button = new Button();
        button.setText("Open");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DIM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                InputStream fileInputStream = null;
                try {
                    DimReader reader = new DimReader();
                    fileInputStream = new FileInputStream(file);
                    DimContent content = reader.readDimData(fileInputStream, false);
                    DimData data = dimDataFactory.fromDimContent(content);
                    fileInputStream.close();
                    LoadedScene scene = new LoadedScene(content, data, stage);
                    scene.setupScene(SelectionState.builder()
                            .selectionType(CurrentSelectionType.LOGO)
                            .slot(0)
                            .spriteIndex(0)
                            .backgroundType(BackgroundType.IMAGE)
                            .build());
                } catch (FileNotFoundException e) {
                    log.error("Couldn't find selected file.", e);
                } catch (IOException e) {
                    log.error("Couldn't close file???", e);
                }

            }
        });
        return button;
    }

    private Button setupSaveButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Save");
        if(selectionState.isSafetyModeOn()) {
            boolean valid = safetyValidator.isValid(dimData);
            button.setDisable(!valid);
        }
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save DIM File As...");
            File file = fileChooser.showSaveDialog(stage);
            if(file != null) {
                OutputStream fileOutputStream = null;
                try {
                    DimWriter writer = new DimWriter();
                    fileOutputStream = new FileOutputStream(file);
                    writer.writeDimData(dimContentFactory.merge(dimContent, dimData), fileOutputStream);
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    log.error("Couldn't save selected file.", e);
                } catch (IOException e) {
                    log.error("Couldn't close file???", e);
                }
            }
        });
        return button;
    }

    private Node setupName(SelectionState selectionState) {
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        int slot = selectionState.getSlot();
        if(selectionType == CurrentSelectionType.LOGO) {
            Label label = new Label("LOGO");
            label.setPrefWidth(240);
            return label;
        } else if (selectionType == CurrentSelectionType.EGG) {
            Label label = new Label("EGG");
            label.setPrefWidth(240);
            return label;
        } else {
            MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(selectionState.getSlot());
            if(monsterSlot.getSprites().isEmpty()) {
                monsterSlot.getSprites().add(SpriteData.Sprite.builder().width(1).height(1).pixelData(DimContentFactory.createDummySprite(1, 1)).build());
            }
            SpriteData.Sprite nameSprite = monsterSlot.getSprites().get(0);
            Image image = spriteImageTranslator.loadImageFromSprite(nameSprite);
            ImageView imageView = new ImageView(image);
            StackPane stackPane = new StackPane(imageView);
            stackPane.setPrefWidth(240);
            stackPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
            return stackPane;
        }
    }

    private Node setupReplaceNameSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Replace Name");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        int slot = selectionState.getSlot();
        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select name sprite replacement. Should be have height of 15.");
            if(selectionState.getLastFileOpenPath() != null) {
                fileChooser.setInitialDirectory(selectionState.getLastFileOpenPath());
            }
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                try {
                    selectionState.setLastFileOpenPath(file.getParentFile());
                    SpriteData.Sprite replacementSprite = spriteImageTranslator.loadSprite(file);
                    dimData.getMonsterSlotList().get(slot).getSprites().set(0, replacementSprite);
                    this.setupScene(selectionState);
                } catch (IOException ioe) {
                    log.error("Couldn't load image file!", ioe);
                }
            }
        });
        return button;
    }

    private Node setupAddSlotButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Add Monster");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        button.setOnAction(e -> {
            dimData.addEntry(selectionState.getSlot());
            setupScene(selectionState);
        });
        return button;
    }

    private Node setupDeleteSlotButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Delete Monster");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        button.setOnAction(e -> {
            dimData.deleteEntry(dimData.getMonsterSlotList().get(selectionState.getSlot()).getId());
            setupScene(selectionState.toBuilder().spriteIndex(1).build());
        });
        return button;
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
            setupScene(newStateBuilder.build());
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
            setupScene(newStateBuilder.build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }
}
