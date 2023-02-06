package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCardData;
import com.github.cfogrady.dim.modifier.data.bem.BemCharacter;
import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.card.Character;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class NFCGridController {
    public static final int IDLE_SPRITE_IDX = 1;

    private final SpriteImageTranslator spriteImageTranslator;
    private final AppState appState;

    @Setter
    private StackPane stackPane;

    public void refreshAll() {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(setupNfcGrid());
    }

    private GridPane setupNfcGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        Text pool1Totals = new Text();
        Text pool2Totals = new Text();
        Text pool3Totals = new Text();
        setupHeader(gridPane, pool1Totals, pool2Totals, pool3Totals);
        int rowIndex = 1;
        for(Character<?> character : appState.getCardData().getCharacters()) {
            if(character.getStage() >= 2) {
                addRow(gridPane, character, pool1Totals, pool2Totals, pool3Totals, rowIndex++);
            }
        }
        return gridPane;
    }

    private void setupHeader(GridPane gridPane, Text pool1Text, Text pool2Text, Text pool3Text) {
        CardData<?,?> cardData = appState.getCardData();
        int columnIndex = 0;
        Text text = new Text("Totals:");
        GridPane.setMargin(text, new Insets(10));
        gridPane.add(text, columnIndex++, 0);
        refreshPoolTotal(cardData.getCharacters(), pool1Text, Character::getFirstPoolBattleChance);
        GridPane.setMargin(pool1Text, new Insets(10));
        gridPane.add(pool1Text, columnIndex++, 0);
        refreshPoolTotal(cardData.getCharacters(), pool2Text, Character::getSecondPoolBattleChance);
        GridPane.setMargin(pool2Text, new Insets(10));
        gridPane.add(pool2Text, columnIndex++, 0);
        if(cardData instanceof BemCardData bemCardData) {
            refreshPoolTotal(bemCardData.getCharacters(), pool3Text, BemCharacter::getThirdPoolBattleChance);
            GridPane.setMargin(pool3Text, new Insets(10));
            gridPane.add(pool3Text, columnIndex++, 0);
        }
    }

    private void addRow(GridPane gridPane, Character<?> character, Text pool1Totals, Text pool2Totals, Text pool3Totals, int rowIndex) {
        int columnIndex = 0;
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(character.getSprites().get(IDLE_SPRITE_IDX)));
        GridPane.setMargin(imageView, new Insets(10));
        gridPane.add(imageView, columnIndex++, rowIndex);
        IntegerTextField pool1Field = new IntegerTextField(character.getFirstPoolBattleChance(), newValue -> {
            character.setFirstPoolBattleChance(newValue);
            refreshPoolTotal(appState.getCardData().getCharacters(), pool1Totals, Character::getFirstPoolBattleChance);
        });
        pool1Field.setAllowBlanks(true);
        GridPane.setMargin(pool1Field, new Insets(10));
        gridPane.add(pool1Field, columnIndex++, rowIndex);
        IntegerTextField pool2Field = new IntegerTextField(character.getSecondPoolBattleChance(), newValue -> {
            character.setSecondPoolBattleChance(newValue);
            refreshPoolTotal(appState.getCardData().getCharacters(), pool2Totals, Character::getSecondPoolBattleChance);
        });
        pool2Field.setAllowBlanks(true);
        GridPane.setMargin(pool2Field, new Insets(10));
        gridPane.add(pool2Field, columnIndex++, rowIndex);
        if(character instanceof BemCharacter bemCharacter) {
            IntegerTextField pool3Field = new IntegerTextField(bemCharacter.getThirdPoolBattleChance(), newValue -> {
                bemCharacter.setThirdPoolBattleChance(newValue);
                refreshPoolTotal(((BemCardData)appState.getCardData()).getCharacters(), pool3Totals, BemCharacter::getThirdPoolBattleChance);
            });
            pool3Field.setAllowBlanks(true);
            GridPane.setMargin(pool3Field, new Insets(10));
            gridPane.add(pool3Field, columnIndex++, rowIndex);
        }
    }

    private <T extends Character<?>> void refreshPoolTotal(List<T> characterList, Text poolText, Function<T, Integer> battleChanceFetcher) {
        int total = getStageTotals(characterList, battleChanceFetcher);
        poolText.setText(Integer.toString(total));
        if(total != 100) {
            poolText.setFill(Color.RED);
        } else {
            poolText.setFill(Color.BLACK);
        }
    }

    private <T extends Character<?>> int getStageTotals(List<T> characterList, Function<T, Integer> battleChanceFetcher) {
        int total = 0;
        for(T character : characterList) {
            Integer chance = battleChanceFetcher.apply(character);
            if(chance != null) {
                total+= chance;
            }
        }
        return total;
    }
}
