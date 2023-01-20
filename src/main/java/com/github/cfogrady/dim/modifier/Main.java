package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.DimDataFactory;

import java.util.prefs.Preferences;

import com.github.cfogrady.dim.modifier.data.firmware.FirmwareManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Preferences preferences = Preferences.userNodeForPackage(this.getClass());
        FirmwareManager firmwareManager = new FirmwareManager(preferences);
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        if(firmwareManager.isValidFirmwareLocationSet()) {
            FirstLoadScene scene = new FirstLoadScene(primaryStage, new DimDataFactory(), firmwareManager.loadFirmware());
            scene.setupScene();
        } else {
            //firmware load scene
            FirmwareLoadScene scene = new FirmwareLoadScene(primaryStage, firmwareManager, new DimDataFactory());
            scene.setupScene();
        }
    }
}
