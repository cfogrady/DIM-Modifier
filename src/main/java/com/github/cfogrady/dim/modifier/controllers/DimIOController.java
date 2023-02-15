package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.card.CardDataIO;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@RequiredArgsConstructor
public class DimIOController {
    private final Stage stage;
    private final CardDataIO cardDataIO;
    private final AppState appState;

    public void openDim(Runnable onCompletion) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select DIM File");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            try(InputStream fileInputStream = new FileInputStream(file)) {
                CardData<?, ?, ?> cardData = cardDataIO.readFromStream(fileInputStream);
                appState.setCardData(cardData);
                onCompletion.run();
            } catch (FileNotFoundException e) {
                log.error("Couldn't find selected file.", e);
            } catch (IOException e) {
                log.error("Couldn't close file???", e);
            }

        }
    }

    public void saveDim() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save DIM File As...");
        File file = fileChooser.showSaveDialog(stage);
        if(file != null) {
            saveDimToFile(file);
        }
    }

    private void saveDimToFile(File file) {
        cardDataIO.writeToFile(appState.getCardData(), file);
    }
}
