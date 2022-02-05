package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.DimDataFactory;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
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
    private final Stage stage;
    private final DimDataFactory dimDataFactory;

    @Override
    public void setupScene() {
        DimReader reader = new DimReader();
        Button button = new Button();
        button.setText("Open DIM File");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DIM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                InputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    DimContent content = reader.readDimData(fileInputStream, false);
                    fileInputStream.close();
                    DimData dimData = dimDataFactory.fromDimContent(content);
                    LoadedScene scene = new LoadedScene(content, dimData, stage);
                    scene.setupScene();
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
