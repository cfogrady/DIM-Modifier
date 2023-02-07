package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCardData;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataReader;
import com.github.cfogrady.dim.modifier.data.dim.DimData;
import com.github.cfogrady.dim.modifier.data.dim.DimDataFactory;
import com.github.cfogrady.dim.modifier.view.controller.LoadedViewController;
import com.github.cfogrady.vb.dim.card.*;
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
public class FirstLoadScene implements com.github.cfogrady.dim.modifier.Scene {
    private final AppState appState;
    private final Stage stage;
    private final DimReader dimReader;
    private final DimDataFactory dimDataFactory;
    private final BemCardDataReader bemCardDataReader;
    private final LoadedSceneFactory loadedSceneFactory;
    private final LoadedViewController loadedViewController;

    @Override
    public void setupScene() {
        Button button = new Button();
        button.setText("Open DIM File");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DIM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                InputStream fileInputStream;
                try {
                    fileInputStream = new FileInputStream(file);
                    Card card = dimReader.readDimCardData(fileInputStream, false);
                    fileInputStream.close();
                    if(card instanceof DimCard dimCard) {
                        DimData dimData = dimDataFactory.fromDimContent(dimCard);
                        LoadedScene scene = loadedSceneFactory.createLoadedScene(appState.getFirmwareData(), dimCard, dimData);
                        scene.setupScene();
                    } else if(card instanceof BemCard bemCard) {
                        BemCardData bemCardData = bemCardDataReader.fromBemCard(bemCard);
                        appState.setRawCard(card);
                        appState.setCardData(bemCardData);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoadedView.fxml"));
                        loader.setControllerFactory(p -> loadedViewController);
                        Scene scene = new Scene(loader.load(), 1280, 720);
                        loadedViewController.refreshAll();
                        stage.setScene(scene);
                        stage.show();
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
        Scene scene = new Scene(new StackPane(button), 640, 480);

        stage.setScene(scene);
        stage.show();
    }
}
