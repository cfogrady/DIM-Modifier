package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.view.EvolutionInfoView;
import com.github.cfogrady.dim.modifier.view.FusionInfoView;
import com.github.cfogrady.dim.modifier.view.InfoView;
import com.github.cfogrady.dim.modifier.view.StatsInfoView;
import com.github.cfogrady.vb.dim.reader.DimReader;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@RequiredArgsConstructor
public class LoadedScene {
    public static final int NONE_VALUE = 65535;
    public static final int BACKGROUND_INDEX = 1;

    private final DimContent dimContent;
    private final Stage stage;
    private final FusionInfoView fusionInfoView;
    private final StatsInfoView statsInfoView;
    private final EvolutionInfoView evolutionInfoView;
    private InfoView currentView;

    public LoadedScene(DimContent dimContent, Stage stage) {
        this.dimContent = dimContent;
        this.stage = stage;
        this.fusionInfoView = new FusionInfoView(dimContent.getDimFusions().getFusionBlocks());
        this.statsInfoView = new StatsInfoView(dimContent, selectionState -> setupScene(selectionState));
        this.evolutionInfoView = new EvolutionInfoView(dimContent.getDimEvolutionRequirements().getEvolutionRequirementBlocks());
        this.currentView = statsInfoView;
    }

    public void setupScene(SelectionState selectionState) {
        if(this.currentView == null) {
            this.currentView = statsInfoView;
        }
        VBox vbox = new VBox();
        vbox.getChildren().add(setupHeaderButtons(selectionState));
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
        HBox hBox = new HBox(setupOpenButton(), setupSaveButton(), setupEvolutionsViewButton(selectionState), setupStatsViewButton(selectionState));
        hBox.setSpacing(10);
        return hBox;
    }

    private Button setupEvolutionsViewButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Evolutions");
        button.setOnAction(event -> {
            this.currentView = evolutionInfoView;
            setupScene(selectionState);
        });
        return button;
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

    private Button setupFusionButton() {
        Button button = new Button();
        button.setText("Fusion");
        button.setDisable(true);
        // TODO: Setup Fusion section
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
                    fileInputStream.close();
                    LoadedScene scene = new LoadedScene(content, stage);
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

    private Button setupSaveButton() {
        Button button = new Button();
        button.setText("Save");
        button.setDisable(true);
        // TODO: Setup DIM Writing
        return button;
    }
}
