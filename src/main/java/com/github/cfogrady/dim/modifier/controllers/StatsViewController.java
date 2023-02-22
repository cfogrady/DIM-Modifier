package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.CardSprites;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
public class StatsViewController implements Initializable {
    public static final int IDLE_SPRITE_IDX = 1;

    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;
    private final StatsGridController statsGridController;

    @FXML
    private StackPane backgroundStackPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Button prevSpriteButton;
    @FXML
    private Button nextSpriteButton;
    @FXML
    private StackPane gridContainer;

    @Setter
    private Runnable refreshIdleSprite;
    @Setter
    private Runnable refreshAll;
    @Setter
    private Character<?, ?> character;
    private int spriteOption = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statsGridController.setStackPane(gridContainer);
        statsGridController.setResetView(refreshAll);
    }

    public void clearState() {
        spriteOption = 1;
    }

    public void refreshAll() {
        refreshSpriteSection();
        refreshGrid();
    }

    private void refreshSpriteSection() {
        if(spriteOption >= character.getSprites().size()) {
            spriteOption = IDLE_SPRITE_IDX;
        }
        SpriteData.Sprite sprite = character.getSprites().get(spriteOption);
        refreshSprite(sprite);
        refreshBackground(sprite);
        refreshSpriteButtons();
    }

    private void refreshSpriteButtons() {
        prevSpriteButton.setDisable(spriteOption == IDLE_SPRITE_IDX);
        prevSpriteButton.setOnAction(event -> {
            spriteOption--;
            refreshSprite(character.getSprites().get(spriteOption));
            refreshSpriteButtons();
        });
        nextSpriteButton.setDisable(spriteOption == character.getSprites().size() - 1);
        nextSpriteButton.setOnAction(event -> {
            spriteOption++;
            refreshSprite(character.getSprites().get(spriteOption));
            refreshSpriteButtons();
        });
    }

    private void refreshBackground(SpriteData.Sprite sprite) {
        backgroundStackPane.setBackground(getBackground()); //160x320
        backgroundStackPane.setOnDragOver(e -> {
            if (e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.ANY);
                log.info("Drag Over Image");
                e.consume();
            } else if(e.getDragboard().hasFiles()) {
                if (e.getDragboard().getFiles().size() > 1) {
                    log.info("Can only load 1 file at a time");
                } else {
                    e.acceptTransferModes(TransferMode.ANY);
                    e.consume();
                }
            }
        });
        backgroundStackPane.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceCharacterSprite(newSprite);
            }
        });
        backgroundStackPane.setOnMouseClicked(click -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            replaceCharacterSprite(newSprite);
        });
    }

    private void refreshSprite(SpriteData.Sprite sprite) {
        setImageViewToSprite(sprite);
    }

    private void setImageViewToSprite(SpriteData.Sprite sprite) {
        log.info("Sprite Displayed size {}x{}. Being displayed at {}x{}: ", sprite.getWidth(), sprite.getHeight(), sprite.getWidth()*2, sprite.getHeight()*2);
        Image image = spriteImageTranslator.loadImageFromSprite(sprite);
        imageView.setImage(image);
        imageView.setFitWidth(sprite.getWidth() * 2.0);
        imageView.setFitHeight(sprite.getHeight() * 2.0);
    }

    private void replaceCharacterSprite(SpriteData.Sprite newSprite) {
        if(newSprite != null) {
            SpriteData.SpriteDimensions proposedDimensions = newSprite.getSpriteDimensions();
            if(character.isSpriteSizeValid(proposedDimensions)) {
                character.getSprites().set(spriteOption, newSprite);
                if(spriteOption == AppState.SELECTION_SPRITE_IDX) {
                    refreshIdleSprite.run();
                }
                refreshSprite(newSprite);
            } else {
                Alert alert = new Alert(Alert.AlertType.NONE, CardSprites.getDimensionsText(proposedDimensions, character.getValidDimensions()));
                alert.getButtonTypes().add(ButtonType.OK);
                alert.show();
            }
        }
    }

    private void refreshGrid() {
        statsGridController.refreshStatsGrid(character);
    }

    private Background getBackground() {
        SpriteData.Sprite sprite = appState.getSelectedBackground();
        Image image = spriteImageTranslator.loadImageFromSprite(sprite);
        BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        return new Background(backgroundImage);
    }
}
