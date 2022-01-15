package com.github.cfogrady.dim.modifier;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FirstLoadScene scene = new FirstLoadScene(primaryStage);
        scene.setupScene();
    }
}
