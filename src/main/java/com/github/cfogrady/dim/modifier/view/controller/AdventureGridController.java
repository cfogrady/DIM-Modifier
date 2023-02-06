package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemAdventure;
import com.github.cfogrady.dim.modifier.data.bem.BemCardData;
import com.github.cfogrady.dim.modifier.data.card.Adventure;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class AdventureGridController {
    private static final Insets INSETS = new Insets(10);

    private final SpriteImageTranslator spriteImageTranslator;
    private final AppState appState;

    @Setter
    private StackPane stackPane;

    public void refreshAll() {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(setupAdventureGrid());
    }

    private GridPane setupAdventureGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        int rowIndex = 1;
        setupHeader(gridPane);
        for(Adventure adventure : appState.getCardData().getAdventures()) {
            addRow(gridPane, adventure, rowIndex++);
        }
        return gridPane;
    }

    private void setupHeader(GridPane gridPane) {
        int columnIndex = 0;
        gridPane.add(getTitleText("Adventure Level"), columnIndex++, 0);
        gridPane.add(getTitleText("Steps"), columnIndex++, 0);
        gridPane.add(getTitleText("Boss"), columnIndex++, 0);
        gridPane.add(getTitleText("Boss BP"), columnIndex++, 0);
        gridPane.add(getTitleText("Boss HP"), columnIndex++, 0);
        gridPane.add(getTitleText("Boss AP"), columnIndex++, 0);
        if(appState.getCardData() instanceof BemCardData) {
            gridPane.add(getTitleText("Reward Character"), columnIndex++, 0);
            gridPane.add(getTitleText("Boss Small Attack"), columnIndex++, 0);
            gridPane.add(getTitleText("Boss Big Attack"), columnIndex++, 0);
            gridPane.add(getTitleText("Walking Background"), columnIndex++, 0);
            gridPane.add(getTitleText("Battle Background"), columnIndex++, 0);
        }
    }

    private Text getTitleText(String str) {
        Text text = new Text(str);
        Font defaultFont = text.getFont();
        text.setFont(Font.font(defaultFont.getFamily(), FontWeight.BOLD, defaultFont.getSize()));
        GridPane.setMargin(text, INSETS);
        return text;
    }

    private void addRow(GridPane gridPane, Adventure adventure, int rowIndex) {
        int columnIndex = 0;
        Text adventureLevel = new Text(Integer.toString(rowIndex));
        GridPane.setMargin(adventureLevel, INSETS);
        gridPane.add(adventureLevel, columnIndex++, rowIndex);
        gridPane.add(createIntegerTextField(adventure::getSteps, adventure::setSteps), columnIndex++, rowIndex);
        gridPane.add(createCharacterSelectorComboBox(adventure::getBossId, adventure::setBossId, false), columnIndex++, rowIndex);
        gridPane.add(createIntegerTextField(adventure::getBossBp, adventure::setBossBp), columnIndex++, rowIndex);
        gridPane.add(createIntegerTextField(adventure::getBossHp, adventure::setBossHp), columnIndex++, rowIndex);
        gridPane.add(createIntegerTextField(adventure::getBossAp, adventure::setBossAp), columnIndex++, rowIndex);
        if(adventure instanceof BemAdventure bemAdventure) {
            gridPane.add(createCharacterSelectorComboBox(bemAdventure::getGiftCharacter, bemAdventure::setGiftCharacter, true), columnIndex++, rowIndex);
            gridPane.add(createSmallAttackSelection(bemAdventure), columnIndex++, rowIndex);
            gridPane.add(createBigAttackSelection(bemAdventure), columnIndex++, rowIndex);
            gridPane.add(createBackgroundSelection(bemAdventure::getWalkingBackground, bemAdventure::setWalkingBackground), columnIndex++, rowIndex);
            gridPane.add(createBackgroundSelection(bemAdventure::getBattleBackground, bemAdventure::setBattleBackground), columnIndex++, rowIndex);
        }
    }

    private ImageIntComboBox createCharacterSelectorComboBox(Supplier<UUID> getter, Consumer<UUID> setter, boolean includeNoneValue) {
        ImageIntComboBox imageIntComboBox = new ImageIntComboBox();
        ObservableList<ImageIntComboBox.ImageIntPair> options = spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters());
        if(includeNoneValue) {
            options.add(0, new ImageIntComboBox.ImageIntPair(null, null));
        }
        imageIntComboBox.initialize(options);
        imageIntComboBox.setOnAction(e -> {
            Integer slotId = imageIntComboBox.getValue().getValue();
            if(slotId == null) {
                setter.accept(null);
            } else {
                setter.accept(appState.getCharacter(slotId).getId());
            }
        });
        UUID characterId = getter.get();
        Integer slotId = appState.getCardData().getUuidToCharacterSlot().get(getter.get());
        if(characterId == null) {
            imageIntComboBox.setValue(imageIntComboBox.getItemForValue(null));
        }
        if(slotId != null) {
            imageIntComboBox.setValue(imageIntComboBox.getItemForValue(slotId));
        }
        GridPane.setMargin(imageIntComboBox, INSETS);
        return imageIntComboBox;
    }

    private IntegerTextField createIntegerTextField(Supplier<Integer> valueGetter, Consumer<Integer> valueSetter) {
        IntegerTextField integerTextField = new IntegerTextField(valueGetter.get(), valueSetter);
        integerTextField.setAllowBlanks(false);
        integerTextField.setPrefWidth(50);
        GridPane.setMargin(integerTextField, INSETS);
        return integerTextField;
    }

    private ImageIntComboBox createSmallAttackSelection(BemAdventure adventure) {
        List<SpriteData.Sprite> attackSprites = appState.getFirmwareData().getSmallAttacks();
        attackSprites.addAll(appState.getCardData().getCardSprites().getSmallAttacks());
        ImageIntComboBox comboBox = new ImageIntComboBox();
        ObservableList<ImageIntComboBox.ImageIntPair> options = spriteImageTranslator.createImageValuePairs(attackSprites);
        options.add(0, new ImageIntComboBox.ImageIntPair(null, null));
        comboBox.initialize(adventure.getSmallAttackId(),options, 1.0, null, "Default Attack");
        comboBox.setOnAction(e -> adventure.setSmallAttackId(comboBox.getValue().getValue()));
        comboBox.setPrefWidth(120);
        GridPane.setMargin(comboBox, INSETS);
        return comboBox;
    }

    private ImageIntComboBox createBigAttackSelection(BemAdventure adventure) {
        List<SpriteData.Sprite> attackSprites = appState.getFirmwareData().getBigAttacks();
        attackSprites.addAll(appState.getCardData().getCardSprites().getBigAttacks());
        ImageIntComboBox comboBox = new ImageIntComboBox();
        ObservableList<ImageIntComboBox.ImageIntPair> options = spriteImageTranslator.createImageValuePairs(attackSprites);
        options.add(0, new ImageIntComboBox.ImageIntPair(null, null));
        comboBox.initialize(adventure.getBigAttackId(),options, 1.0, null, "Default Attack");
        comboBox.setOnAction(e -> adventure.setBigAttackId(comboBox.getValue().getValue()));
        comboBox.setPrefWidth(120);
        GridPane.setMargin(comboBox, INSETS);
        return comboBox;
    }

    private ImageIntComboBox createBackgroundSelection(Supplier<Integer> backgroundFetcher, Consumer<Integer> backgroundSetter) {
        ImageIntComboBox imageIntComboBox = new ImageIntComboBox();
        imageIntComboBox.initialize(backgroundFetcher.get(), spriteImageTranslator.createImageValuePairs(appState.getCardData().getCardSprites().getBackgrounds()), 1.0, null, null);
        imageIntComboBox.setOnAction(e -> {
            backgroundSetter.accept(imageIntComboBox.getValue().getValue());
        });
        GridPane.setMargin(imageIntComboBox, INSETS);
        return imageIntComboBox;
    }
}
