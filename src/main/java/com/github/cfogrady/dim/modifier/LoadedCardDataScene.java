package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCardData;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataReader;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataWriter;
import com.github.cfogrady.dim.modifier.data.card.MetaData;
import com.github.cfogrady.dim.modifier.view.*;
import com.github.cfogrady.vb.dim.card.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@RequiredArgsConstructor
public class LoadedCardDataScene {

    private final DimReader dimReader;
    private final BemCardDataReader bemCardDataReader;
    private final BemCardDataWriter bemCardDataWriter;
    private final BemCharactersView bemCharactersView;
    private final Stage stage;
    private final AppState appState;

    public <T> void setupScene(ViewState viewState) {
        if(this.appState.getCurrentView() == null) {
            this.appState.setCurrentView(bemCharactersView);
        }
        Runnable refresher = () -> this.setupScene(viewState);
        VBox vbox = new VBox();
        vbox.getChildren().add(setupHeaderButtons(refresher));
        vbox.getChildren().add(this.appState.getCurrentView().setupView(viewState, this::setupScene));
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    private Node setupHeaderButtons(Runnable refresher) {
        HBox hBox = new HBox(setupOpenButton(refresher), setupSaveButton(), setupSafetyCheck(refresher),
                setupDIMIdLabel(), setupDIMRevisionLabel(), setupDIMDateLabel(), setupChecksumLabel(), setupCharactersViewButton(refresher));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        return hBox;
    }

    private Label setupDIMIdLabel() {
        return new Label("DIM ID: " + appState.getCardData().getMetaData().getId());
    }

    private Label setupDIMRevisionLabel() {
        return new Label("Revision: " + appState.getCardData().getMetaData().getRevision());
    }

    private Label setupDIMDateLabel() {
        MetaData metaData = appState.getCardData().getMetaData();
        return new Label("Factory Date: " +
                metaData.getYear() + "/" +
                metaData.getMonth() + "/" +
                metaData.getDay());
    }

    private Label setupChecksumLabel() {
        return new Label("Checksum: " + Integer.toHexString(appState.getCardData().getMetaData().getOriginalChecksum()));
    }

    private static final String SAFETY_CHECK_TOOLTIP = "If this value is checked, the save button will be disabled until all values are within " +
            "official ranges. Using unofficial ranges (especially with sprites) may result in buffer overflows of devices causing freezes or at worst " +
            "bricking the device. Only uncheck this if you know what you are doing or are ok risking damage to your device.";

    private Node setupSafetyCheck(Runnable refresher) {
        Tooltip tooltip = new Tooltip(SAFETY_CHECK_TOOLTIP);
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(200);
        Label label = new Label("Only Safe Values:");
        label.setTooltip(tooltip);
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(appState.isSafetyModeOn());
        checkBox.setOnAction(e -> {
            appState.setSafetyModeOn(!appState.isSafetyModeOn());
            refresher.run();
        });
        checkBox.setTooltip(tooltip);
        HBox hBox = new HBox(label, checkBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private Button setupCharactersViewButton(Runnable refresher) {
        Button button = new Button();
        button.setText("Characters");
        if(appState.getCurrentView() == this.bemCharactersView) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            appState.setCurrentView(this.bemCharactersView);
            refresher.run();
        });
        return button;
    }

    private Button setupOpenButton(Runnable refresher) {
        Button button = new Button();
        button.setText("Open");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select BEM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                InputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    Card card = dimReader.readDimCardData(fileInputStream, false);
                    fileInputStream.close();
                    if(card instanceof BemCard bemCard) {
                        BemCardData bemCardData = bemCardDataReader.fromBemCard(bemCard);
                        appState.setRawCard(card);
                        appState.setCardData(bemCardData);
                        appState.setCurrentView(this.bemCharactersView);
                        refresher.run();
                    } else {
                        throw new IllegalStateException("DimReader returned an unknown type for DimCard");
                    }
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
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save BE Memory File As...");
            File file = fileChooser.showSaveDialog(stage);
            if(file != null) {
                if(appState.getRawCard() instanceof BemCard bemCard && appState.getCardData() instanceof BemCardData bemCardData) {
                    bemCardDataWriter.write(file, bemCard, bemCardData);
                } else {
                    throw new IllegalStateException("Both modified and original data should be in BEM format, but aren't");
                }
            }
        });
        return button;
    }
}
