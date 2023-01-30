package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

@Slf4j
@RequiredArgsConstructor
public class SpriteReplacer {
    private final AppState appState;
    private final Stage stage;
    private final SpriteImageTranslator spriteImageTranslator;

    public SpriteData.Sprite replaceSprite(Integer expectedWidth, Integer expectedHeight, File file) {
        if(file == null) {
            return null;
        }
        SpriteData.Sprite newSprite = loadSpriteFromFile(file);
        boolean validReplacement = true;
        if(expectedWidth != null && expectedWidth != newSprite.getWidth()) {
            validReplacement = false;
        }
        if(expectedHeight != null && expectedHeight != newSprite.getHeight()) {
            validReplacement = false;
        }
        if(validReplacement) {
            return newSprite;
        }
        log.warn("Selected sprite doesn't match expected dimensions");
        return null;
    }

    public SpriteData.Sprite replaceSprite(SpriteData.Sprite sprite, boolean sameWidth, boolean sameHeight) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select sprite replacement. Should be " + sprite.getWidth() + " x " + sprite.getHeight());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image format", "*.png", "*.bmp"));
        if(appState.getLastOpenedFilePath() != null) {
            fileChooser.setInitialDirectory(appState.getLastOpenedFilePath());
        }
        File file = fileChooser.showOpenDialog(stage);
        return replaceSprite(sameWidth ? sprite.getWidth() : null, sameHeight ? sprite.getHeight() : null, file);
    }

    private SpriteData.Sprite loadSpriteFromFile(File file) {
        try {
            return spriteImageTranslator.loadSprite(file);
        } catch (IOException ioe) {
            log.error("Couldn't load image file!", ioe);
            throw new UncheckedIOException(ioe);
        }
    }
}
