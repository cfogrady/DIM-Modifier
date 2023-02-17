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
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
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
        initializeAddCharacterButton();
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

    private void initializeAddCharacterButton() {
        newCharacterButton.setOnAction(e -> {
            appState.getCardData().addCharacter(characterSelection, spriteImageTranslator);
            refreshAll();
        });
        deleteCharacterButton.setOnAction(e -> {
            appState.getCardData().deleteCharacter(characterSelection);
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

    private void initializeNameBox() {
        Character<?> character = appState.getCardData().getCharacters().get(characterSelection);
        SpriteData.Sprite nameSprite = character.getSprites().get(0);
        Image image = spriteImageTranslator.loadImageFromSprite(nameSprite);
        ImageView imageView = new ImageView(image);
        imageView.setViewport(new Rectangle2D(0, 0, 80, 15));
        if(nameSprite.getWidth() > 80) {
            nameUpdater = new NameUpdater(nameSprite.getWidth(), imageView, -80);
            timer.scheduleAtFixedRate(nameUpdater, 0, 33);
        }
        //nameBox.setBackground();
        nameBox.getChildren().clear();
        nameBox.getChildren().add(imageView);
        //stackPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        nameBox.setOnMouseClicked(event -> {
            SpriteData.Sprite newNameSprite = spriteReplacer.replaceSprite(nameSprite, false, true);
            if(newNameSprite != null) {
                character.getSprites().set(0, newNameSprite);
                //TODO: Make sure this works. Received bug report of it failing for an older version.
                initializeNameBox();
            }
        });
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
                statsViewController.setRefreshIdleSprite(this::initializeCharacterSelectionComboBox);
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