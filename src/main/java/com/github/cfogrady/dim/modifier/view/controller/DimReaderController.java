package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCardData;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataReader;
import com.github.cfogrady.dim.modifier.data.dim.DimData;
import com.github.cfogrady.dim.modifier.data.dim.DimDataFactory;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.card.Card;
import com.github.cfogrady.vb.dim.card.DimCard;
import com.github.cfogrady.vb.dim.card.DimReader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@RequiredArgsConstructor
public class DimReaderController {
    private final Stage stage;
    private final DimReader dimReader;
    private final BemCardDataReader bemCardDataReader;
    private final AppState appState;

    public void openDim(Runnable onCompletion) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select DIM File");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            InputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file);
                Card card = dimReader.readDimCardData(fileInputStream, false);
                fileInputStream.close();
                if(card instanceof BemCard bemCard) {
                    BemCardData bemCardData = bemCardDataReader.fromBemCard(bemCard);
                    appState.setRawCard(card);
                    appState.setCardData(bemCardData);
                    onCompletion.run();
                } else {
                    throw new IllegalStateException("DimReader returned an unknown type for DimCard");
                }
            } catch (FileNotFoundException e) {
                log.error("Couldn't find selected file.", e);
            } catch (IOException e) {
                log.error("Couldn't close file???", e);
            }

        }
    }
}
