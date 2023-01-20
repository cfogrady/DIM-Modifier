package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.*;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareData;
import com.github.cfogrady.dim.modifier.view.*;
import com.github.cfogrady.vb.dim.card.DimCard;
import com.github.cfogrady.vb.dim.card.DimReader;
import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor()
public class LoadedScene {
    public static final int NONE_VALUE = 65535;
    public static final String NONE_LABEL = "None";
    private final static FileChooser.ExtensionFilter DIGIMON_EXTENSTION = new FileChooser.ExtensionFilter("MON files (*.mon)", "*.mon");
    private final static FileChooser.ExtensionFilter ALL_FILES = new FileChooser.ExtensionFilter("All Files", "*.*");

    private final FirmwareData firmwareData;
    private final DimCard dimContent;
    private final DimData dimData;
    private final DimDataFactory dimDataFactory;
    private final DimContentFactory dimContentFactory;
    private final Stage stage;
    private final DigimonWriter digimonWriter;
    private final DigimonReader digimonReader;
    private final FusionInfoView fusionInfoView;
    private final StatsInfoView statsInfoView;
    private final EvolutionInfoView evolutionInfoView;
    private final BattleInfoView battleInfoView;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SafetyValidator safetyValidator;
    private SelectionState selectionState;
    private InfoView currentView;

    public LoadedScene(FirmwareData firmwareData, DimCard dimContent, DimData dimData, Stage stage, DigimonWriter digimonWriter, DigimonReader digimonReader) {
        this.firmwareData = firmwareData;
        this.dimContent = dimContent;
        this.dimData = dimData;
        this.stage = stage;
        this.digimonWriter = digimonWriter;
        this.digimonReader = digimonReader;
        Runnable sceneRefresher = this::setupScene;
        this.spriteImageTranslator = new SpriteImageTranslator();
        this.fusionInfoView = new FusionInfoView(dimData, spriteImageTranslator, sceneRefresher);
        this.statsInfoView = new StatsInfoView(firmwareData, dimData, spriteImageTranslator, stage, sceneRefresher);
        this.evolutionInfoView = new EvolutionInfoView(dimData, spriteImageTranslator, sceneRefresher);
        this.battleInfoView = new BattleInfoView(dimData, spriteImageTranslator, sceneRefresher);
        this.selectionState = SelectionState.builder()
                .safetyModeOn(true)
                .currentView(statsInfoView)
                .backgroundType(BackgroundType.IMAGE)
                .selectionType(CurrentSelectionType.LOGO)
                .build();
        this.safetyValidator = new SafetyValidator();
        this.dimDataFactory = new DimDataFactory();
        this.dimContentFactory = new DimContentFactory();
        this.currentView = statsInfoView;
    }

    public void setupScene() {
        if(this.currentView == null) {
            this.currentView = statsInfoView;
        }
        VBox vbox = new VBox();
        vbox.getChildren().add(setupHeaderButtons());
        if(currentView != battleInfoView) {
            vbox.getChildren().add(setupNameButtons());
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
                    selectionState.setSpriteIndex(selectionState.getSpriteIndex()-1);
                    setupScene();
                }
            } else if (key.getCode() == KeyCode.D) {
                if(selectionState.getSpriteIndex() < statsInfoView.getSpriteCountForSelection(selectionState) - 1) {
                    selectionState.setSpriteIndex(selectionState.getSpriteIndex()+1);
                    setupScene();
                }
            } else if(key.getCode() == KeyCode.B) {
                changeBackground();
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            log.info("Event: {}", event);
        });
        stage.setScene(scene);
        stage.show();
    }

    private void changeBackground() {
        selectionState.setBackgroundType(BackgroundType.nextBackground(selectionState.getBackgroundType()));
        setupScene();
    }

    private Node setupHeaderButtons() {
        HBox hBox = new HBox(setupOpenButton(), setupSaveButton(), setupSafetyCheck(),
                setupDIMIdLabel(), setupDIMRevisionLabel(), setupDIMDateLabel(), setupChecksumLabel(), setupStatsViewButton(), setupEvolutionsViewButton(),
                setupFusionButton(), setupBattlesViewButton());
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        return hBox;
    }

    private Node setupNameButtons() {
        HBox hBox = new HBox(setupPrevButton(),
                setupName(),
                setupNextButton(),
                setupReplaceNameSpriteButton(),
                setupAddSlotButton(),
                setupDeleteSlotButton(),
                setupExportDigimonButton(),
                setupImportDigimonButton()
                );
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        return hBox;
    }

    private Label setupDIMIdLabel() {
        return new Label("DIM ID: " + dimContent.getDimHeader().getDimId());
    }

    private Label setupDIMRevisionLabel() {
        return new Label("Revision: " + dimContent.getDimHeader().getRevisionNumber());
    }

    private Label setupDIMDateLabel() {
        return new Label("Factory Date: " +
                dimContent.getDimHeader().getProductionYear() + "/" +
                dimContent.getDimHeader().getProductionMonth() + "/" +
                dimContent.getDimHeader().getProductionDay());
    }

    private Label setupChecksumLabel() {
        return new Label("Checksum: " + Integer.toHexString(dimContent.getChecksum()));
    }

    private Button setupEvolutionsViewButton() {
        Button button = new Button();
        button.setText("Evolutions");
        if(currentView == evolutionInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = evolutionInfoView;
            setupScene();
        });
        return button;
    }

    private static final String SAFETY_CHECK_TOOLTIP = "If this value is checked, the save button will be disabled until all values are within " +
            "official ranges. Using unofficial ranges (especially with sprites) may result in buffer overflows of devices causing freezes or at worst " +
            "bricking the device. Only uncheck this if you know what you are doing or are ok risking damage to your device.";

    private Node setupSafetyCheck() {
        Tooltip tooltip = new Tooltip(SAFETY_CHECK_TOOLTIP);
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(200);
        Label label = new Label("Only Safe Values:");
        label.setTooltip(tooltip);
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(selectionState.isSafetyModeOn());
        checkBox.setOnAction(e -> {
            selectionState.setSafetyModeOn(!selectionState.isSafetyModeOn());
            setupScene();
        });
        checkBox.setTooltip(tooltip);
        HBox hBox = new HBox(label, checkBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private Button setupStatsViewButton() {
        Button button = new Button();
        button.setText("Stats");
        if(currentView == statsInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = statsInfoView;
            setupScene();
        });
        return button;
    }

    private Button setupBattlesViewButton() {
        Button button = new Button();
        button.setText("Battles");
        if(currentView == battleInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = battleInfoView;
            setupScene();
        });
        return button;
    }

    private Button setupFusionButton() {
        Button button = new Button();
        button.setText("Fusions");
        if(currentView == fusionInfoView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            this.currentView = fusionInfoView;
            setupScene();
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
                    DimCard content = reader.readDimData(fileInputStream, false);
                    DimData data = dimDataFactory.fromDimContent(content);
                    fileInputStream.close();
                    LoadedScene scene = new LoadedScene(firmwareData, content, data, stage, digimonWriter, digimonReader);
                    scene.setupScene();
                } catch (FileNotFoundException e) {
                    log.error("Couldn't find selected file.", e);
                } catch (IOException e) {
                    log.error("Couldn't close file???", e);
                }

            }
        });
        return button;
    }

    private Button setupSaveButton() {
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

    private Node setupName() {
        CurrentSelectionType selectionType = selectionState.getSelectionType();
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

    private Node setupReplaceNameSpriteButton() {
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
                    this.setupScene();
                } catch (IOException ioe) {
                    log.error("Couldn't load image file!", ioe);
                }
            }
        });
        return button;
    }

    private Node setupAddSlotButton() {
        Button button = new Button();
        button.setText("Add Monster");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        button.setOnAction(e -> {
            dimData.addEntry(selectionState.getSlot());
            setupScene();
        });
        return button;
    }

    private Node setupExportDigimonButton() {
        Button button = new Button();
        button.setText("Export Monster");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        Dialog<String> inputDialog = setupExportDialogue();
        button.setOnAction(e -> {
            Optional<String> artistName = inputDialog.showAndWait();
            if(artistName.isPresent()) {
                log.info("Artist Name: {}", artistName.get());
                exportDigimon(artistName.get());
            }
        });
        return button;
    }

    private Node setupImportDigimonButton() {
        Button button = new Button();
        button.setText("Import Monster");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        button.setOnAction(e -> {
            importDigimon();
            setupScene();
        });
        return button;
    }

    private void importDigimon() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(DIGIMON_EXTENSTION, ALL_FILES);
        fileChooser.setTitle("Import Digimon...");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            try (InputStream fileInputStream = new FileInputStream(file)) {
                MonsterSlotWithAuthor monsterSlotWithAuthor = digimonReader.readDigimon(fileInputStream);
                dimData.addEntry(monsterSlotWithAuthor.getMonsterSlot(), selectionState.getSlot());
                Dialog<ButtonType> artistCredit = setupInportDialogue(monsterSlotWithAuthor.getAuthor());
                artistCredit.showAndWait();
            } catch (FileNotFoundException e) {
                log.error("Couldn't save selected file.", e);
            } catch (IOException e) {
                log.error("Couldn't close file???", e);
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                log.error("Selected index is out of bounds!", aioobe);
            }
        }
    }

    private void exportDigimon(String author) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(DIGIMON_EXTENSTION);
        fileChooser.setTitle("Export Digimon as...");
        File file = fileChooser.showSaveDialog(stage);
        if(file != null) {
            try (OutputStream fileOutputStream = new FileOutputStream(file)) {
                int slotIndex = selectionState.getSlot();
                MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(slotIndex);
                digimonWriter.writeDigimon(author, monsterSlot, fileOutputStream);
            } catch (FileNotFoundException e) {
                log.error("Couldn't save selected file.", e);
            } catch (IOException e) {
                log.error("Couldn't close file???", e);
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                log.error("Selected index is out of bounds!", aioobe);
            }
        }
    }

    private Dialog<String> setupExportDialogue() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Artist Name:");
        inputDialog.setGraphic(null);
        return inputDialog;
    }

    private Dialog<ButtonType> setupInportDialogue(String artist) {
        Alert alert = new Alert(Alert.AlertType.NONE, "This monster was brought to you by " + artist + "!");
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().add(okButton);
        return alert;
    }

    private Node setupDeleteSlotButton() {
        Button button = new Button();
        button.setText("Delete Monster");
        CurrentSelectionType selectionType = selectionState.getSelectionType();
        if(selectionType != CurrentSelectionType.SLOT) {
            button.setDisable(true);
        }
        button.setOnAction(e -> {
            dimData.deleteEntry(dimData.getMonsterSlotList().get(selectionState.getSlot()).getId());
            selectionState.setSpriteIndex(1);
            setupScene();
        });
        return button;
    }

    private Node setupPrevButton() {
        Button button = new Button();
        button.setText("Prev");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
                selectionState.setSelectionType(CurrentSelectionType.LOGO);
            } else if (selectionState.getSlot() == 0) {
                selectionState.setSelectionType(CurrentSelectionType.EGG);
                selectionState.setSpriteIndex(0);
            } else {
                selectionState.setSlot(selectionState.getSlot() - 1);
                selectionState.setSpriteIndex(1);
            }
            setupScene();
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupNextButton() {
        Button button = new Button();
        button.setText("Next");
        if(selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSlot() == dimData.getMonsterSlotList().size() - 1) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
                selectionState.setSelectionType(CurrentSelectionType.EGG);
            } else if (selectionState.getSelectionType() == CurrentSelectionType.EGG) {
                selectionState.setSelectionType(CurrentSelectionType.SLOT);
                selectionState.setSpriteIndex(1);
            } else {
                selectionState.setSlot(selectionState.getSlot() + 1);
                selectionState.setSpriteIndex(1);
            }
            setupScene();
        });
        StackPane pane = new StackPane(button);
        return pane;
    }
}
