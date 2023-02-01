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
public class FirmwareLoadScene implements com.github.cfogrady.dim.modifier.Scene {
    private final Stage stage;
    private final FirmwareManager firmwareManager;
    private final FirstLoadScene firstLoadScene;
    private final AppState appState;

    @Override
    public void setupScene() {
        Button button = new Button();
        button.setText("Locate Firmware");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Firmware File");
            File file = fileChooser.showOpenDialog(stage);
            if(FirmwareManager.isValidFirmwareLocation(file)) {
                firmwareManager.setFirmwareLocation(file);
                appState.setFirmwareData(firmwareManager.loadFirmware());
                firstLoadScene.setupScene();
            }
        });
        Scene scene = new Scene(new StackPane(button), 640, 480);

        stage.setScene(scene);
        stage.show();
    }
}
