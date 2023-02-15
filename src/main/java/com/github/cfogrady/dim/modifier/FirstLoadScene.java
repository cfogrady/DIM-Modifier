package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.controllers.LoadedViewController;
import com.github.cfogrady.dim.modifier.data.card.CardDataIO;
import javafx.fxml.FXMLLoader;
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
public class FirstLoadScene {
    private final AppState appState;
    private final Stage stage;
    private final CardDataIO cardDataIO;
    private final LoadedViewController loadedViewController;

    public void setupScene() {
        Button button = new Button();
        button.setText("Open DIM File");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DIM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                loadCard(file);
                setupLoadedDataView();
            }
        });
        Scene scene = new Scene(new StackPane(button), 640, 480);

        stage.setScene(scene);
        stage.show();
    }

    private void loadCard(File file) {
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            CardData<?, ?, ?> cardData = cardDataIO.readFromStream(fileInputStream);
            appState.setCardData(cardData);
        } catch (IOException e) {
            log.error("Error loading file: {}", file.getAbsolutePath(), e);
        }
    }

    private void setupLoadedDataView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoadedView.fxml"));
            loader.setControllerFactory(p -> loadedViewController);
            Scene scene = new Scene(loader.load(), 1280, 720);
            loadedViewController.refreshAll();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            log.error("Unable to load layout for loaded data view!", e);
        }
    }
}
