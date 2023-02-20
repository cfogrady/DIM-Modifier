package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@RequiredArgsConstructor
public class CharacterViewController implements Initializable {
    private final Timer timer;
    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;
    private final Node statsSubView;
    private final StatsViewController statsViewController;
    private final TransformationViewController transformationViewController;
    private final Node transformationsSubView;

    @FXML
    private ImageIntComboBox characterSelectionComboBox;
    @FXML
    private StackPane nameBox;
    @FXML
    private Button statsButton;
    @FXML
    private Button transformationsButton;
    @FXML
    private Button newCharacterButton;
    @FXML
    private Button deleteCharacterButton;
    @FXML
    private Button exportCharacterSpritesButton;
    @FXML
    private Button importCharacterSpritesButton;
    @FXML
    private AnchorPane subView;

    private enum SubViewSelection {
        STATS,
        TRANSFORMATIONS;
    }

    private int characterSelection = 0;
    private NameUpdater nameUpdater;
    private SubViewSelection subViewSelection = SubViewSelection.STATS;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCharacterButtons();
        statsViewController.setRefreshIdleSprite(this::initializeCharacterSelectionComboBox);
        statsViewController.setRefreshAll(this::refreshAll);
        statsViewController.initialize(location, resources);
    }

    public void clearState() {
        characterSelection = 0;
        statsViewController.clearState();
    }

    public void refreshAll() {
        initializeCharacterSelectionComboBox();
        initializeNameBox();
        refreshButtons();
        initializeSubView();
    }

    private void initializeCharacterButtons() {
        newCharacterButton.setOnAction(e -> {
            appState.getCardData().addCharacter(characterSelection, spriteImageTranslator);
            refreshAll();
        });
        deleteCharacterButton.setOnAction(e -> {
            appState.getCardData().deleteCharacter(characterSelection);
            refreshAll();
        });
        exportCharacterSpritesButton.setOnAction(e -> {
            spriteImageTranslator.exportCharacterSpriteSheet(appState.getCharacter(characterSelection));
        });
        importCharacterSpritesButton.setOnAction(e -> {
            spriteImageTranslator.importSpriteSheet(appState.getCharacter(characterSelection));
            refreshAll();
        });
    }

    private void initializeCharacterSelectionComboBox() {
        characterSelectionComboBox.initialize(characterSelection, spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters()), 1.0, null, null);
        characterSelectionComboBox.setOnAction(e -> {
            if(characterSelectionComboBox.getValue() != null) {
                characterSelection = characterSelectionComboBox.getValue().getValue();
            }
            if(nameUpdater != null) {
                nameUpdater.cancel();
            }
            initializeNameBox();
            initializeSubView();
        });
    }

    private static final String NAME_TEXT_ERROR = "Name sprites must be a multiple of 80 pixels in width and 15 pixels in height. Examples: 80x15, 160x15, 240x15, etc.";

    private void initializeNameBox() {
        Character<?, ?> character = appState.getCardData().getCharacters().get(characterSelection);
        SpriteData.Sprite nameSprite = character.getSprites().get(0);
        Image image = spriteImageTranslator.loadImageFromSprite(nameSprite);
        ImageView imageView = new ImageView(image);
        imageView.setViewport(new Rectangle2D(0, 0, 80, 15));
        if(nameSprite.getWidth() > 80) {
            nameUpdater = new NameUpdater(nameSprite.getWidth(), imageView, -80);
            timer.scheduleAtFixedRate(nameUpdater, 0, 33);
        }
        nameBox.getChildren().clear();
        nameBox.getChildren().add(imageView);
        nameBox.setOnMouseClicked(event -> {
            SpriteData.Sprite newNameSprite = spriteReplacer.replaceSprite(nameSprite, false, true);
            if(newNameSprite != null) {
                replaceNameSprite(character, newNameSprite);
            }
        });
        nameBox.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceNameSprite(character, newSprite);
            }
        });
        nameBox.setOnDragOver(e -> {
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

    private void replaceNameSprite(Character<?, ?> character, SpriteData.Sprite nameSprite) {
        SpriteData.SpriteDimensions newSpriteDimensions = nameSprite.getSpriteDimensions();
        if(newSpriteDimensions.getHeight() == 15 && newSpriteDimensions.getWidth()%80 == 0) {
            character.getSprites().set(0, nameSprite);
            initializeNameBox();
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE, NAME_TEXT_ERROR);
            alert.getButtonTypes().add(ButtonType.OK);
            alert.show();
        }
    }

    private void refreshButtons() {
        if(subViewSelection == SubViewSelection.STATS) {
            statsButton.setDisable(true);
            transformationsButton.setDisable(false);
        } else {
            statsButton.setDisable(false);
            transformationsButton.setDisable(true);
        }
        statsButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.STATS;
            initializeSubView();
            refreshButtons();
        });
        transformationsButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.TRANSFORMATIONS;
            initializeSubView();
            refreshButtons();
        });
    }

    private void initializeSubView() {
        subView.getChildren().clear();
        switch(subViewSelection) {
            case STATS -> {
                statsViewController.setCharacter(appState.getCharacter(characterSelection));
                statsViewController.refreshAll();
                subView.getChildren().add(statsSubView);
            }
            case TRANSFORMATIONS -> {
                transformationViewController.setCharacter(appState.getCharacter(characterSelection));
                transformationViewController.refreshAll();
                subView.getChildren().add(transformationsSubView);
            }
        }
    }

    @AllArgsConstructor
    public static class NameUpdater extends TimerTask {
        private final int spriteWidth;
        private final ImageView imageView;
        private int offsetX;
        @Override
        public void run() {
            // hack to make it pause at the start and end.
            if(offsetX > spriteWidth) {
                offsetX = -80;
                imageView.setViewport(new Rectangle2D(0, 0, 80, 15));
            } else if(offsetX < 0) {
                imageView.setViewport(new Rectangle2D(0, 0, 80, 15));
            } else {
                imageView.setViewport(new Rectangle2D(offsetX % spriteWidth, 0, 80, 15));
            }
            offsetX += 1;
        }
    }
}
