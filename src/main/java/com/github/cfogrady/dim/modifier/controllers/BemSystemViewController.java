package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.CardSprites;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class BemSystemViewController implements Initializable {
    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;

    @FXML
    ImageView backgroundsView;
    @FXML
    StackPane backgroundSpriteContainer;
    @FXML
    Button prevBackgroundButton;
    @FXML
    Button nextBackgroundButton;
    @FXML
    ImageView textSpritesView;
    @FXML
    StackPane textSpriteContainer;
    @FXML
    Button prevTextSpriteButton;
    @FXML
    Button nextTextSpriteButton;
    @FXML
    ImageView stageSpritesView;
    @FXML
    StackPane stageSpriteContainer;
    @FXML
    Button prevStageButton;
    @FXML
    Button nextStageButton;
    @FXML
    ImageView attributeSpritesView;
    @FXML
    StackPane attributeSpriteContainer;
    @FXML
    Button prevAttributeButton;
    @FXML
    Button nextAttributeButton;
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
    @FXML
    ImageView hitSpritesView;
    @FXML
    StackPane hitSpriteContainer;
    @FXML
    Button prevHitButton;
    @FXML
    Button nextHitButton;
    @FXML
    StackPane smallAttackSpriteContainer;
    @FXML
    ImageView smallAttackSpritesView;
    @FXML
    Button prevSmallAttackButton;
    @FXML
    Button nextSmallAttackButton;
    @FXML
    StackPane bigAttackSpriteContainer;
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
        backgroundSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(80).height(160).build(), appState::setBackgroundSprite, this::refreshBackground);
            }
        });
        backgroundSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(80).height(160).build(), appState::setBackgroundSprite, this::refreshBackground);
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

    private void replaceSprite(SpriteData.Sprite newSprite, SpriteData.SpriteDimensions expectedDimensions, Consumer<SpriteData.Sprite> setter, Runnable refresher) {
        SpriteData.SpriteDimensions proposedDimensions = newSprite.getSpriteDimensions();
        if(proposedDimensions.equals(expectedDimensions)) {
            setter.accept(newSprite);
            refresher.run();
        } else {
            Alert alert = new Alert(Alert.AlertType.NONE, CardSprites.getDimensionsText(proposedDimensions, List.of(expectedDimensions)));
            alert.getButtonTypes().add(ButtonType.OK);
            alert.show();
        }
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
        textSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, getTextSprite().getSpriteDimensions(), this::setTextSprite, this::refreshTextSprite);
            }
        });
        textSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, getTextSprite().getSpriteDimensions(), this::setTextSprite, this::refreshTextSprite);
            }
        });
        textSpriteContainer.setOnDragOver(e -> {
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
        stageSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(78).height(18).build(), this::setStageSprite, this::refreshStageSprite);
            }
        });
        stageSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(78).height(18).build(), this::setStageSprite, this::refreshStageSprite);
            }
        });
        stageSpriteContainer.setOnDragOver(e -> {
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
        attributeSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(30).height(16).build(), this::setAttributeSprite, this::refreshAttributeSprite);
            }
        });
        attributeSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(30).height(16).build(), this::setAttributeSprite, this::refreshAttributeSprite);
            }
        });
        attributeSpriteContainer.setOnDragOver(e -> {
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
        iconSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(42).height(42).build(), appState.getCardData().getCardSprites()::setLogo, this::refreshLogoSprite);
            }
        });
        iconSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(42).height(42).build(), appState.getCardData().getCardSprites()::setLogo, this::refreshLogoSprite);
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
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(32).height(40).build(), this::setEggSprite, this::refreshEggSprite);
            }
        });
        eggSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(32).height(40).build(), this::setEggSprite, this::refreshEggSprite);
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
        hitSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(61).height(57).build(), this::setHitSprite, this::refreshHitSprite);
            }
        });
        hitSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(61).height(57).build(), this::setHitSprite, this::refreshHitSprite);
            }
        });
        hitSpriteContainer.setOnDragOver(e -> {
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
        smallAttackSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(20).height(20).build(), this::setSmallAttackSprite, this::refreshSmallAttackSprite);
            }
        });
        smallAttackSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                if(newSprite != null) {
                    replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(20).height(20).build(), this::setSmallAttackSprite, this::refreshSmallAttackSprite);
                }
            }
        });
        smallAttackSpriteContainer.setOnDragOver(e -> {
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
        bigAttackSpriteContainer.setOnMouseClicked(e -> {
            SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFileChooser();
            if(newSprite != null) {
                replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(34).height(44).build(), this::setBigAttackSprite, this::refreshBigAttackSprite);
            }
        });
        bigAttackSpriteContainer.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.loadSpriteFromFile(file);
                if(newSprite != null) {
                    replaceSprite(newSprite, SpriteData.SpriteDimensions.builder().width(34).height(44).build(), this::setBigAttackSprite, this::refreshBigAttackSprite);
                }
            }
        });
        bigAttackSpriteContainer.setOnDragOver(e -> {
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
