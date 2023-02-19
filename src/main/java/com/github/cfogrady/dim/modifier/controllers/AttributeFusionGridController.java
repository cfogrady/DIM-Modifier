package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.card.Fusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class AttributeFusionGridController {
    private final static Background BLACK_BACKGROUND = new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0)));

    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;

    @Setter
    private StackPane stackPane;

    public void refreshAll(Character<?, ?> character) {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(setupGrid(character.getFusions()));
    }

    private Node setupGrid(Fusions fusions) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        int columnIndex = 0;
        ObservableList<ImageIntComboBox.ImageIntPair> idleCharacters = spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters());
        idleCharacters.add(0, new ImageIntComboBox.ImageIntPair(null, null));
        gridPane.add(setupAttributeFusion(idleCharacters, 1, "Virus", fusions::getType1FusionResult, fusions::setType1FusionResult), columnIndex++, 0);
        gridPane.add(setupAttributeFusion(idleCharacters, 2, "Data", fusions::getType2FusionResult, fusions::setType2FusionResult), columnIndex++, 0);
        gridPane.add(setupAttributeFusion(idleCharacters, 3, "Vaccine", fusions::getType3FusionResult, fusions::setType3FusionResult), columnIndex++, 0);
        gridPane.add(setupAttributeFusion(idleCharacters, 4, "Free", fusions::getType4FusionResult, fusions::setType4FusionResult), columnIndex++, 0);
        return gridPane;
    }

    private Node setupAttributeFusion(ObservableList<ImageIntComboBox.ImageIntPair> characterSelections, int type, String defaultText, Supplier<UUID> fusionResultGetter, Consumer<UUID> fusionResultSetter) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        List<SpriteData.Sprite> attributeSprites = appState.getAttributes();
        if(attributeSprites != null) {
            StackPane stackPane = new StackPane();
            stackPane.setBackground(BLACK_BACKGROUND);
            ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(attributeSprites.get(type-1)));
            stackPane.getChildren().add(imageView);
            hBox.getChildren().add(stackPane);
        } else {
            Text text = new Text(defaultText);
            hBox.getChildren().add(text);
        }
        ImageIntComboBox imageIntComboBox = new ImageIntComboBox();
        Integer slotId = appState.getCardData().getUuidToCharacterSlot().get(fusionResultGetter.get());
        imageIntComboBox.initialize(slotId, characterSelections, 1.0, null, null);
        imageIntComboBox.setOnAction(e -> {
            Integer newSlot = imageIntComboBox.getValue().getValue();
            if(newSlot == null) {
                fusionResultSetter.accept(null);
            } else {
                UUID id = appState.getCharacter(newSlot).getId();
                fusionResultSetter.accept(id);
            }
        });
        hBox.getChildren().add(imageIntComboBox);
        return hBox;
    }
}
