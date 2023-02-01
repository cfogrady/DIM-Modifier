package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.*;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.dim.MonsterSlot;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
public class BemStatsView {
    public static final int IDLE_SPRITE_IDX = 1;
    public static final int CLOSE_UP_SPRITE_IDX = 13;

    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;

    @Getter
    private final Node mainView;
    private final StackPane spriteWindow;
    private final Button previousButton;
    private final Button nextButton;
    private final StatsGrid statsGrid;

    @AllArgsConstructor
    public static class StatsViewState extends BemCharactersView.CharacterViewState {
        private final Consumer<BemCharactersView.CharacterViewState> characterRefresher;
        int spriteIndex;

        public SpriteData.Sprite getCurrentSprite(AppState appState) {
            return getCharacter(appState).getSprites().get(spriteIndex);
        }

        public void replaceCurrentSprite(AppState appState, SpriteData.Sprite newSprite) {
            getCharacter(appState).getSprites().set(spriteIndex, newSprite);
        }
    }

    public static StatsViewState fromCharacterViewState(BemCharactersView.CharacterViewState original, Consumer<BemCharactersView.CharacterViewState> characterRefresher) {
        StatsViewState newState = new StatsViewState(characterRefresher, IDLE_SPRITE_IDX);
        newState.copyFrom(original);
        return newState;
    }

    public void refreshView(ViewState state) {
        if(state instanceof StatsViewState statsViewState) {
            refreshSpriteArea(statsViewState);
            refreshButtons(statsViewState);
            refreshStats(statsViewState);
        } else {
            throw new IllegalArgumentException("ViewState passed in must be a StatsViewState");
        }
    }

    public void refreshSpriteArea(StatsViewState statsViewState) {
        SpriteData.Sprite sprite = statsViewState.getCurrentSprite(appState);
        log.info("Sprite Displayed size {}x{}. Being displayed at {}x{}: ", sprite.getWidth(), sprite.getHeight(), sprite.getWidth()*2, sprite.getHeight()*2);
        ImageView imageView = convertSpriteToImageView(statsViewState, sprite);
        spriteWindow.setBackground(getBackground()); //160x320
        spriteWindow.getChildren().clear();
        spriteWindow.getChildren().add(imageView);
        spriteWindow.setOnDragDropped( e-> {
            if(e.getDragboard().hasFiles()) {
                List<File> files = e.getDragboard().getFiles();
                File file = files.get(0);
                SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite.getWidth(), sprite.getHeight(), file);
                replaceCharacterSprite(newSprite, statsViewState);
            }
        });
    }

    private void refreshButtons(StatsViewState statsViewState) {
        refreshPrevSpriteButton(statsViewState);
        refreshNextSpriteButton(statsViewState);
    }

    private void refreshStats(StatsViewState statsViewState) {
        Character<?> character = statsViewState.getCharacter(appState);
        statsGrid.refreshStatsGrid(character);

    }

    private ImageView convertSpriteToImageView(StatsViewState viewState, SpriteData.Sprite sprite) {
        ImageView imageView = getImageViewForSprite(sprite, 2.0);
        imageView.setOnMouseClicked(click -> {
            SpriteData.Sprite newSprite = spriteReplacer.replaceSprite(sprite, true, true);
            replaceCharacterSprite(newSprite, viewState);
        });
        return imageView;
    }

    private void replaceCharacterSprite(SpriteData.Sprite newSprite, StatsViewState viewState) {
        if(newSprite != null) {
            viewState.replaceCurrentSprite(appState, newSprite);
            if(viewState.spriteIndex == AppState.SELECTION_SPRITE_IDX) {
                viewState.characterRefresher.accept(viewState);
            }
            refreshSpriteArea(viewState);
        }
    }

    private void refreshPrevSpriteButton(StatsViewState statsViewState) {
        previousButton.setDisable(statsViewState.spriteIndex == IDLE_SPRITE_IDX);
        previousButton.setOnAction(event -> {
            statsViewState.spriteIndex--;
            refreshView(statsViewState);
        });
    }

    private void refreshNextSpriteButton(StatsViewState statsViewState) {
        nextButton.setDisable(statsViewState.spriteIndex == CLOSE_UP_SPRITE_IDX);
        nextButton.setOnAction(event -> {
            statsViewState.spriteIndex++;
            refreshView(statsViewState);
        });
    }

    private ImageView getImageViewForSprite(SpriteData.Sprite sprite, double scale) {
        Image image = spriteImageTranslator.loadImageFromSprite(sprite);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(sprite.getWidth() * scale);
        imageView.setFitHeight(sprite.getHeight() * scale);
        return imageView;
    }

    private Background getBackground() {
        SpriteData.Sprite sprite = appState.getSelectedBackground();
        Image image = spriteImageTranslator.loadImageFromSprite(sprite);
        BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        return new Background(backgroundImage);
    }
}
