package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
public class DimSystemViewController implements Initializable {
    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;

    @FXML
    ImageView backgroundsView;
    @FXML
    StackPane backgroundSpriteContainer;
    @FXML
    ImageView iconSpriteView;
    @FXML
    StackPane iconSpriteContainer;
    @FXML
    ImageView eggSpritesView;
    @FXML
    StackPane eggSpriteContainer;
    @FXML
    Button prevEggButton;
    @FXML
    Button nextEggButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeBackground();
        initializeLogoSprites();
        initializeEggSprites();
    }

    public void clearState() {
        eggSelection = 0;
    }

    public void refreshAll() {
        refreshBackground();
        refreshLogoSprite();
        refreshEggSprite();
    }

    private void initializeBackground() {
        backgroundSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                appState.getCardData().getCardSprites().getBackgrounds().set(appState.getSelectedBackgroundIndex(), newSprite);
                refreshBackground();
            }
        });
        backgroundSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                appState.getCardData().getCardSprites().getBackgrounds().set(appState.getSelectedBackgroundIndex(), newSprite);
                refreshBackground();
            }
        });
        backgroundSpriteContainer.setOnDragOver(e -> {
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
    }

    private void refreshBackground() {
        backgroundsView.setImage(spriteImageTranslator.loadImageFromSprite(appState.getSelectedBackground()));
    }

    private void initializeLogoSprites() {
        iconSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                appState.getCardData().getCardSprites().setLogo(newSprite);
                refreshLogoSprite();
            }
        });
        iconSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                appState.getCardData().getCardSprites().setLogo(newSprite);
                refreshLogoSprite();
            }
        });
        iconSpriteContainer.setOnDragOver(e -> {
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
    }

    private void refreshLogoSprite() {
        iconSpriteView.setImage(spriteImageTranslator.loadImageFromSprite(appState.getCardData().getCardSprites().getLogo()));
    }

    private int eggSelection = 0;

    private void initializeEggSprites() {
        nextEggButton.setOnAction(e -> {
            eggSelection = (eggSelection + 1) % appState.getCardData().getCardSprites().getEgg().size();
            refreshEggSprite();
        });
        prevEggButton.setOnAction(e -> {
            eggSelection = eggSelection -1;
            if(eggSelection < 0) {
                eggSelection = appState.getCardData().getCardSprites().getEgg().size() - 1;
            }
            refreshEggSprite();
        });
        eggSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                setEggSprite(newSprite);
                refreshEggSprite();
            }
        });
        eggSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                setEggSprite(newSprite);
                refreshEggSprite();
            }
        });
        eggSpriteContainer.setOnDragOver(e -> {
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
    }

    private SpriteData.Sprite getEggSprite() {
        return appState.getCardData().getCardSprites().getEgg().get(eggSelection);
    }

    private void setEggSprite(SpriteData.Sprite newSprite) {
        appState.getCardData().getCardSprites().getEgg().set(eggSelection, newSprite);
    }

    private void refreshEggSprite() {
        eggSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getEggSprite()));
    }
}
