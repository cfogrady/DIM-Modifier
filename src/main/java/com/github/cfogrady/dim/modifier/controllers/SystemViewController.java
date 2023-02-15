package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class SystemViewController implements Initializable {
    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;

    @FXML
    ImageView backgroundsView;
    @FXML
    Button prevBackgroundButton;
    @FXML
    Button nextBackgroundButton;
    @FXML
    ImageView textSpritesView;
    @FXML
    Button prevTextSpriteButton;
    @FXML
    Button nextTextSpriteButton;
    @FXML
    ImageView stageSpritesView;
    @FXML
    Button prevStageButton;
    @FXML
    Button nextStageButton;
    @FXML
    ImageView attributeSpritesView;
    @FXML
    Button prevAttributeButton;
    @FXML
    Button nextAttributeButton;
    @FXML
    ImageView iconSpriteView;
    @FXML
    ImageView eggSpritesView;
    @FXML
    Button prevEggButton;
    @FXML
    Button nextEggButton;
    @FXML
    ImageView hitSpritesView;
    @FXML
    Button prevHitButton;
    @FXML
    Button nextHitButton;
    @FXML
    ImageView smallAttackSpritesView;
    @FXML
    Button prevSmallAttackButton;
    @FXML
    Button nextSmallAttackButton;
    @FXML
    ImageView bigAttackSpritesView;
    @FXML
    Button prevBigAttackButton;
    @FXML
    Button nextBigAttackButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeBackground();
        initializeTextSprites();
        initializeStagesSprites();
        initializeAttributeSprites();
        initializeLogoSprites();
        initializeEggSprites();
        initializeHitSprites();
        initializeSmallAttackSprites();
        initializeBigAttackSprites();
    }

    public void clearState() {
        textSpriteIndex = 0;
        stageSelection = 0;
        attributeSelection = 0;
        eggSelection = 0;
        hitSelection = 0;
        bigAttackSelection = 0;
        smallAttackSelection = 0;
    }

    public void refreshAll() {
        refreshBackground();
        refreshTextSprite();
        refreshStageSprite();
        refreshAttributeSprite();
        refreshLogoSprite();
        refreshEggSprite();
        refreshHitSprite();
        refreshSmallAttackSprite();
        refreshBigAttackSprite();
    }

    private void initializeBackground() {
        nextBackgroundButton.setOnAction(e -> {
            int newBackgroundIndex = (appState.getSelectedBackgroundIndex() + 1) % appState.getCardData().getCardSprites().getBackgrounds().size();
            appState.setSelectedBackgroundIndex(newBackgroundIndex);
            refreshBackground();
        });
        prevBackgroundButton.setOnAction(e -> {
            int newBackgroundIndex = appState.getSelectedBackgroundIndex() -1;
            if(newBackgroundIndex < 0) {
                newBackgroundIndex = appState.getCardData().getCardSprites().getBackgrounds().size() - 1;
            }
            appState.setSelectedBackgroundIndex(newBackgroundIndex);
            refreshBackground();
        });
        backgroundsView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(appState.getSelectedBackground(), true, true);
            if(newSprite != null) {
                appState.getCardData().getCardSprites().getBackgrounds().set(appState.getSelectedBackgroundIndex(), newSprite);
                refreshBackground();
            }
        });
        backgroundsView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = appState.getSelectedBackground();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                appState.getCardData().getCardSprites().getBackgrounds().set(appState.getSelectedBackgroundIndex(), newSprite);
                refreshBackground();
            }
        });
    }

    private void refreshBackground() {
        backgroundsView.setImage(spriteImageTranslator.loadImageFromSprite(appState.getSelectedBackground()));
    }

    private int textSpriteIndex = 0;

    private void initializeTextSprites() {
        nextTextSpriteButton.setOnAction(e -> {
            textSpriteIndex = (textSpriteIndex + 1) % 4;
            refreshTextSprite();
        });
        prevTextSpriteButton.setOnAction(e -> {
            textSpriteIndex = textSpriteIndex -1;
            if(textSpriteIndex < 0) {
                textSpriteIndex = 3;
            }
            refreshTextSprite();
        });
        textSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getTextSprite(), true, true);
            if(newSprite != null) {
                setTextSprite(newSprite);
                refreshTextSprite();
            }
        });
        textSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getTextSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setTextSprite(newSprite);
                refreshTextSprite();
            }
        });
    }

    private SpriteData.Sprite getTextSprite() {
        switch (textSpriteIndex) {
            case 0 -> {
                return appState.getCardData().getCardSprites().getReady();
            }
            case 1 -> {
                return appState.getCardData().getCardSprites().getGo();
            }
            case 2 -> {
                return appState.getCardData().getCardSprites().getWin();
            }
            case 3 -> {
                return appState.getCardData().getCardSprites().getLose();
            }
        }
        throw new IllegalArgumentException("Bad textSprite index");
    }

    private void setTextSprite(SpriteData.Sprite newSprite) {
        switch (textSpriteIndex) {
            case 0 -> appState.getCardData().getCardSprites().setReady(newSprite);
            case 1 -> appState.getCardData().getCardSprites().setGo(newSprite);
            case 2 -> appState.getCardData().getCardSprites().setWin(newSprite);
            case 3 -> appState.getCardData().getCardSprites().setLose(newSprite);
        }
    }

    private void refreshTextSprite() {
        textSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getTextSprite()));
    }

    private int stageSelection = 0;

    private void initializeStagesSprites() {
        nextStageButton.setOnAction(e -> {
            stageSelection = (stageSelection + 1) % appState.getCardData().getCardSprites().getStages().size();
            refreshStageSprite();
        });
        prevStageButton.setOnAction(e -> {
            stageSelection = stageSelection -1;
            if(stageSelection < 0) {
                stageSelection = appState.getCardData().getCardSprites().getStages().size() - 1;
            }
            refreshStageSprite();
        });
        stageSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getStageSprite(), true, true);
            if(newSprite != null) {
                setStageSprite(newSprite);
                refreshStageSprite();
            }
        });
        stageSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getStageSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setStageSprite(newSprite);
                refreshStageSprite();
            }
        });
    }

    private SpriteData.Sprite getStageSprite() {
        return appState.getCardData().getCardSprites().getStages().get(stageSelection);
    }

    private void setStageSprite(SpriteData.Sprite newSprite) {
        appState.getCardData().getCardSprites().getStages().set(stageSelection, newSprite);
    }

    private void refreshStageSprite() {
        stageSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getStageSprite()));
    }

    private int attributeSelection = 0;

    private void initializeAttributeSprites() {
        nextAttributeButton.setOnAction(e -> {
            attributeSelection = (attributeSelection + 1) % appState.getCardData().getCardSprites().getTypes().size();
            refreshAttributeSprite();
        });
        prevAttributeButton.setOnAction(e -> {
            attributeSelection = attributeSelection -1;
            if(attributeSelection < 0) {
                attributeSelection = appState.getCardData().getCardSprites().getTypes().size() - 1;
            }
            refreshAttributeSprite();
        });
        attributeSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getAttributeSprite(), true, true);
            if(newSprite != null) {
                setAttributeSprite(newSprite);
                refreshAttributeSprite();
            }
        });
        attributeSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getAttributeSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setAttributeSprite(newSprite);
                refreshAttributeSprite();
            }
        });
    }

    private SpriteData.Sprite getAttributeSprite() {
        return appState.getCardData().getCardSprites().getTypes().get(attributeSelection);
    }

    private void setAttributeSprite(SpriteData.Sprite newSprite) {
        appState.getCardData().getCardSprites().getTypes().set(attributeSelection, newSprite);
    }

    private void refreshAttributeSprite() {
        attributeSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getAttributeSprite()));
    }

    private void initializeLogoSprites() {
        iconSpriteView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(appState.getCardData().getCardSprites().getLogo(), true, true);
            if(newSprite != null) {
                appState.getCardData().getCardSprites().setLogo(newSprite);
                refreshLogoSprite();
            }
        });
        iconSpriteView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = appState.getCardData().getCardSprites().getLogo();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                appState.getCardData().getCardSprites().setLogo(newSprite);
                refreshLogoSprite();
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
        eggSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getEggSprite(), true, true);
            if(newSprite != null) {
                setEggSprite(newSprite);
                refreshEggSprite();
            }
        });
        eggSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getEggSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setEggSprite(newSprite);
                refreshEggSprite();
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

    private int hitSelection = 0;

    private void initializeHitSprites() {
        nextHitButton.setOnAction(e -> {
            hitSelection = (hitSelection + 1) % appState.getCardData().getCardSprites().getHits().size();
            refreshHitSprite();
        });
        prevHitButton.setOnAction(e -> {
            hitSelection = hitSelection -1;
            if(hitSelection < 0) {
                hitSelection = appState.getCardData().getCardSprites().getHits().size() - 1;
            }
            refreshHitSprite();
        });
        hitSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getHitSprite(), true, true);
            if(newSprite != null) {
                setHitSprite(newSprite);
                refreshHitSprite();
            }
        });
        hitSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getHitSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setHitSprite(newSprite);
                refreshHitSprite();
            }
        });
    }

    private SpriteData.Sprite getHitSprite() {
        return appState.getCardData().getCardSprites().getHits().get(hitSelection);
    }

    private void setHitSprite(SpriteData.Sprite newSprite) {
        appState.getCardData().getCardSprites().getHits().set(hitSelection, newSprite);
    }

    private void refreshHitSprite() {
        hitSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getHitSprite()));
    }

    private int smallAttackSelection = 0;

    private void initializeSmallAttackSprites() {
        nextSmallAttackButton.setOnAction(e -> {
            smallAttackSelection = (smallAttackSelection + 1) % appState.getCardData().getCardSprites().getSmallAttacks().size();
            refreshSmallAttackSprite();
        });
        prevSmallAttackButton.setOnAction(e -> {
            smallAttackSelection = smallAttackSelection -1;
            if(smallAttackSelection < 0) {
                smallAttackSelection = appState.getCardData().getCardSprites().getSmallAttacks().size() - 1;
            }
            refreshSmallAttackSprite();
        });
        smallAttackSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getSmallAttackSprite(), true, true);
            if(newSprite != null) {
                setSmallAttackSprite(newSprite);
                refreshSmallAttackSprite();
            }
        });
        smallAttackSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getSmallAttackSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setSmallAttackSprite(newSprite);
                refreshSmallAttackSprite();
            }
        });
    }

    private SpriteData.Sprite getSmallAttackSprite() {
        return appState.getCardData().getCardSprites().getSmallAttacks().get(smallAttackSelection);
    }

    private void setSmallAttackSprite(SpriteData.Sprite newSprite) {
        appState.getCardData().getCardSprites().getSmallAttacks().set(smallAttackSelection, newSprite);
    }

    private void refreshSmallAttackSprite() {
        smallAttackSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getSmallAttackSprite()));
    }

    private int bigAttackSelection = 0;

    private void initializeBigAttackSprites() {
        nextBigAttackButton.setOnAction(e -> {
            bigAttackSelection = (bigAttackSelection + 1) % appState.getCardData().getCardSprites().getBigAttacks().size();
            refreshBigAttackSprite();
        });
        prevBigAttackButton.setOnAction(e -> {
            bigAttackSelection = bigAttackSelection -1;
            if(bigAttackSelection < 0) {
                bigAttackSelection = appState.getCardData().getCardSprites().getBigAttacks().size() - 1;
            }
            refreshBigAttackSprite();
        });
        bigAttackSpritesView.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(getBigAttackSprite(), true, true);
            if(newSprite != null) {
                setBigAttackSprite(newSprite);
                refreshBigAttackSprite();
            }
        });
        bigAttackSpritesView.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                SpriteData.Sprite sprite = getBigAttackSprite();
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                setBigAttackSprite(newSprite);
                refreshBigAttackSprite();
            }
        });
    }

    private SpriteData.Sprite getBigAttackSprite() {
        return appState.getCardData().getCardSprites().getBigAttacks().get(bigAttackSelection);
    }

    private void setBigAttackSprite(SpriteData.Sprite newSprite) {
        appState.getCardData().getCardSprites().getBigAttacks().set(bigAttackSelection, newSprite);
    }

    private void refreshBigAttackSprite() {
        bigAttackSpritesView.setImage(spriteImageTranslator.loadImageFromSprite(getBigAttackSprite()));
    }
}
