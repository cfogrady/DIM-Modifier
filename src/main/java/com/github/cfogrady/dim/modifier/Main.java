package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.DimDataFactory;
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
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        FirstLoadScene scene = new FirstLoadScene(primaryStage, new DimDataFactory());
        scene.setupScene();
    }
}
