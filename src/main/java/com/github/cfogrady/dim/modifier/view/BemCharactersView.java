package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBoxFactory;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BemCharactersView implements BemInfoView {

    private final AppState appState;
    private final ImageIntComboBoxFactory imageIntComboBoxFactory;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;
    private final Timer timer;
    private final BemStatsView bemStatsView;
    private final BemEvolutionsView bemEvolutionsView;

    public static abstract class CharacterViewState implements ViewState {
        int selectedIndex;
        NameUpdater nameUpdater;

        public void copyFrom(CharacterViewState other) {
            this.selectedIndex = other.selectedIndex;
        }
        public Character<?> getCharacter(AppState appState) {
            return appState.getCardData().getCharacters().get(selectedIndex);
        }
    }

    @Override
    public Node setupView(ViewState viewState, Consumer<ViewState> refresher) {
        setupCharacterView();
        if(viewState == null) {
            CharacterViewState characterViewState = new BemStatsView.StatsViewState(this::refreshCharacterSelector, BemStatsView.IDLE_SPRITE_IDX);
            characterViewState.selectedIndex = 0;
            viewState = characterViewState;
        }
        if(viewState instanceof CharacterViewState characterViewState) {
            refreshAll(characterViewState);
        } else {
            throw new IllegalArgumentException("BemCharacterView must take a CharacterViewState. Received a " + viewState.getClass().getName());
        }
        return characterView;
    }

    private VBox characterView;
    private HBox characterSelector;
    private HBox subViewButtons;

    private void refreshAll(CharacterViewState characterViewState) {
        refreshCharacterSelector(characterViewState);
        refreshViewButtons(characterViewState);
        refreshSubView(characterViewState);
    }

    private void setupCharacterView() {
        if(characterView == null) {
            characterView = new VBox();
            characterSelector = new HBox();
            characterSelector.setSpacing(10);
            characterSelector.setPadding(new Insets(10));
            subViewButtons = new HBox();
            subViewButtons.setSpacing(10);
            subViewButtons.setPadding(new Insets(10));
            characterView.setSpacing(10);
            characterView.setPadding(new Insets(10));
            characterView.getChildren().add(new HBox(characterSelector, subViewButtons));
            characterView.getChildren().add(new HBox());
        }
    }

    private void refreshCharacterSelector(CharacterViewState viewState) {
        characterSelector.getChildren().clear();
        characterSelector.getChildren().addAll(setupSlotSelector(viewState), setupName(viewState));
    }

    private void refreshViewButtons(CharacterViewState characterViewState) {
        this.subViewButtons.getChildren().clear();
        this.subViewButtons.getChildren().addAll(setupStatsViewButton(characterViewState), setupEvolutionsViewButton(characterViewState));
    }

    private void refreshSubView(ViewState viewState) {
        Node subView;
        if(viewState instanceof BemStatsView.StatsViewState actualState) {
            bemStatsView.refreshView(actualState);
            subView = bemStatsView.getMainView();
        } else if(viewState instanceof  BemEvolutionsView.EvolutionsViewState evolutionsViewState) {
            bemEvolutionsView.refreshView(evolutionsViewState);
            subView = bemEvolutionsView.getMainView();
        } else {
            throw new IllegalArgumentException("BemCharacterView must be a StatsViewState. Received a " + viewState.getClass().getName());
        }
        characterView.getChildren().set(1, subView);
    }

    private Node setupSlotSelector(CharacterViewState viewState) {
        ImageIntComboBox comboBox = imageIntComboBoxFactory.createImageIntComboBox(viewState.selectedIndex, appState.getIdleForCharacters(), newSlot -> {
            viewState.selectedIndex = newSlot;
            if(viewState.nameUpdater != null) {
                viewState.nameUpdater.cancel();
                viewState.nameUpdater = null;
            }
            refreshAll(viewState);
        });
        comboBox.setPrefWidth(120);
        return comboBox;
    }

    private Node setupName(CharacterViewState viewState) {
        Character<?> character = viewState.getCharacter(appState);
        SpriteData.Sprite nameSprite = character.getSprites().get(0);
        Image image = spriteImageTranslator.loadImageFromSprite(nameSprite);
        ImageView imageView = new ImageView(image);
        imageView.setViewport(new Rectangle2D(0, 0, 80, 15));
        if(nameSprite.getWidth() > 80) {
            viewState.nameUpdater = new NameUpdater(nameSprite.getWidth(), imageView, -80);
            timer.scheduleAtFixedRate(viewState.nameUpdater, 0, 33);
        }
        StackPane stackPane = new StackPane(imageView);
        stackPane.setMaxWidth(80);
        stackPane.setMinWidth(80);
        stackPane.setMaxHeight(25);
        stackPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        stackPane.setOnMouseClicked(event -> {
            SpriteData.Sprite newNameSprite = spriteReplacer.replaceSprite(nameSprite, false, true);
            if(newNameSprite != null) {
                character.getSprites().set(0, newNameSprite);
                refreshCharacterSelector(viewState);
            }
        });
        return stackPane;
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

    private Button setupStatsViewButton(CharacterViewState viewState) {
        Button button = new Button();
        button.setText("Stats");
        if(viewState instanceof BemStatsView.StatsViewState) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            BemStatsView.StatsViewState newState = BemStatsView.fromCharacterViewState(viewState, this::refreshCharacterSelector);
            refreshViewButtons(newState);
            refreshSubView(newState);
        });
        return button;
    }

    private Button setupEvolutionsViewButton(CharacterViewState viewState) {
        Button button = new Button();
        button.setText("Evolutions");
        if(viewState instanceof BemEvolutionsView.EvolutionsViewState) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            BemEvolutionsView.EvolutionsViewState newState = BemEvolutionsView.fromCharacterViewState(viewState);
            refreshViewButtons(newState);
            refreshSubView(newState);
        });
        return button;
    }
}
