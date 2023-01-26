package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.firmware.FirmwareManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {
    private ApplicationOrchestrator applicationOrchestrator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        applicationOrchestrator = ApplicationOrchestrator.buildOrchestration(primaryStage);
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        FirmwareManager firmwareManager = applicationOrchestrator.getFirmwareManager();
        if(firmwareManager.isValidFirmwareLocationSet()) {
            applicationOrchestrator.getFirstLoadScene().setupScene();
        } else {
            //firmware load scene
            applicationOrchestrator.getFirmwareLoadScene().setupScene();
        }
    }

    @Override
    public void stop() {
        applicationOrchestrator.getTimer().cancel();
        applicationOrchestrator.getTimer().purge();
    }
}
