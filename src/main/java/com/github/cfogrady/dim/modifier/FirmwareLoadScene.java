package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareManager;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@RequiredArgsConstructor
@Slf4j
public class FirmwareLoadScene {
    private final Stage stage;
    private final FirmwareManager firmwareManager;
    private final FirstLoadScene firstLoadScene;
    private final AppState appState;

    public void setupScene() {
        Button button = new Button();
        button.setText("Locate BE Firmware");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BE Firmware", "*.vb2"));
            fileChooser.setTitle("Select BE Firmware File");
            File file = fileChooser.showOpenDialog(stage);
            if(FirmwareManager.isValidFirmwareLocation(file)) {
                try {
                    firmwareManager.setFirmwareLocation(file);
                    appState.setFirmwareData(firmwareManager.loadFirmware());
                    firstLoadScene.setupScene();
                } catch (IOException e) {
                    log.error("Unable to read firmware. Are you sure this is BE firmware?");
                    firmwareManager.clearFirmwareLocation();
                }
            }
        });
        Scene scene = new Scene(new StackPane(button), 640, 480);

        stage.setScene(scene);
        stage.show();
    }
}
