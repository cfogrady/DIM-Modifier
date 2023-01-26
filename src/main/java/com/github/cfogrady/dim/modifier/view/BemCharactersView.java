package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class BemCharactersView implements BemInfoView {
    private final AppState appState;
    private final ImageIntComboBoxFactory imageIntComboBoxFactory;
    private final SpriteImageTranslator spriteImageTranslator;
    private final Timer timer;
    private final BemStatsView bemStatsView;

    public Character<?> getCharacter(CharacterViewState viewState) {
        return appState.getCardData().getCharacters().get(viewState.selectedIndex);
    }

    public static abstract class CharacterViewState implements ViewState {
        int selectedIndex;
        NameUpdater nameUpdater;

        public void copyFrom(CharacterViewState other) {
            this.selectedIndex = other.selectedIndex;
        }
    }

    @Override
    public Node setupView(ViewState viewState, Consumer<ViewState> refresher) {
        if(viewState == null) {
            CharacterViewState characterViewState = new BemStatsView.StatsViewState();
            characterViewState.selectedIndex = 0;
            viewState = characterViewState;
        }
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        if(viewState instanceof CharacterViewState characterViewState) {
            vbox.getChildren().add(setupButtons(characterViewState, refresher));
            if(viewState instanceof BemStatsView.StatsViewState actualState) {
                //vbox.getChildren().add(bemStatsView.setupView(actualState, refresher));
            }
        } else {
            throw new IllegalArgumentException("BemCharacterView must take a CharacterViewState. Received a " + viewState.getClass().getName());
        }
        return vbox;
    }

    public Node setupButtons(CharacterViewState viewState, Consumer<ViewState> refresher) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(setupSlotSelector(viewState, refresher), setupName(viewState), setupStatsViewButton(viewState, refresher));
        return hBox;
    }

    public Node setupSlotSelector(CharacterViewState viewState, Consumer<ViewState> refresher) {
        ImageIntComboBox comboBox = imageIntComboBoxFactory.createImageIntComboBox(viewState.selectedIndex, getIdleForCharacters(), newSlot -> {
            viewState.selectedIndex = newSlot;
            if(viewState.nameUpdater != null) {
                viewState.nameUpdater.cancel();
                viewState.nameUpdater = null;
            }
            refresher.accept(viewState);
        });
        comboBox.setPrefWidth(120);
        return comboBox;
    }

    public Node setupName(CharacterViewState viewState) {
        Character<?> character = getCharacter(viewState);
        SpriteData.Sprite nameSprite = character.getSprites().get(0);
        Image image = spriteImageTranslator.loadImageFromSprite(nameSprite);
        ImageView imageView = new ImageView(image);
        int x = 0;
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

    private Button setupStatsViewButton(CharacterViewState viewState, Consumer<ViewState> refresher) {
        Button button = new Button();
        button.setText("Stats");
        if(viewState instanceof BemStatsView.StatsViewState) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            refresher.accept(BemStatsView.fromCharacterViewState(viewState));
        });
        return button;
    }

    List<SpriteData.Sprite> getIdleForCharacters() {
        List<SpriteData.Sprite> idleSprites = new ArrayList<>();
        for(Character<?> character : appState.getCardData().getCharacters()) {
            idleSprites.add(character.getSprites().get(1));
        }
        return idleSprites;
    }
}
