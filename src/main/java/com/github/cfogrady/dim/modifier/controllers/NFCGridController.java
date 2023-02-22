package com.github.cfogrady.dim.modifier.controllers;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class NFCGridController {
    public static final int IDLE_SPRITE_IDX = 1;
    private static final Insets INSETS = new Insets(10);

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
        setupHeaders(gridPane);
        Text pool1Totals = new Text();
        Text pool2Totals = new Text();
        Text pool3Totals = new Text();
        setupTotals(gridPane, pool1Totals, pool2Totals, pool3Totals);
        int rowIndex = 2;
        for(Character<?, ?> character : appState.getCardData().getCharacters()) {
            if(character.getStage() >= 2) {
                addRow(gridPane, character, pool1Totals, pool2Totals, pool3Totals, rowIndex++);
            }
        }
        return gridPane;
    }

    private void setupHeaders(GridPane gridPane) {
        CardData<?,?, ?> cardData = appState.getCardData();
        int columnIndex = 0;
        gridPane.add(getTitleText("Character"), columnIndex++, 0);
        gridPane.add(getTitleText("Phase 3/4 Chance"), columnIndex++, 0);
        gridPane.add(getTitleText("Phasse 5/6 Chance"), columnIndex++, 0);
        if(cardData instanceof BemCardData) {
            gridPane.add(getTitleText("Phase 7+ Chance"), columnIndex++, 0);
        }
    }

    private Text getTitleText(String str) {
        Text text = new Text(str);
        Font defaultFont = text.getFont();
        text.setFont(Font.font(defaultFont.getFamily(), FontWeight.BOLD, defaultFont.getSize()));
        GridPane.setMargin(text, INSETS);
        return text;
    }

    private void setupTotals(GridPane gridPane, Text pool1Text, Text pool2Text, Text pool3Text) {
        CardData<?,?, ?> cardData = appState.getCardData();
        int columnIndex = 0;
        Text text = new Text("Totals:");
        GridPane.setMargin(text, INSETS);
        gridPane.add(text, columnIndex++, 1);
        refreshPoolTotal(cardData.getCharacters(), pool1Text, Character::getFirstPoolBattleChance);
        GridPane.setMargin(pool1Text, INSETS);
        gridPane.add(pool1Text, columnIndex++, 1);
        refreshPoolTotal(cardData.getCharacters(), pool2Text, Character::getSecondPoolBattleChance);
        GridPane.setMargin(pool2Text, INSETS);
        gridPane.add(pool2Text, columnIndex++, 1);
        if(cardData instanceof BemCardData bemCardData) {
            refreshPoolTotal(bemCardData.getCharacters(), pool3Text, BemCharacter::getThirdPoolBattleChance);
            GridPane.setMargin(pool3Text, INSETS);
            gridPane.add(pool3Text, columnIndex++, 1);
        }
    }

    private void addRow(GridPane gridPane, Character<?, ?> character, Text pool1Totals, Text pool2Totals, Text pool3Totals, int rowIndex) {
        int columnIndex = 0;
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(character.getSprites().get(IDLE_SPRITE_IDX)));
        GridPane.setMargin(imageView, INSETS);
        gridPane.add(imageView, columnIndex++, rowIndex);
        IntegerTextField pool1Field = new IntegerTextField(character.getFirstPoolBattleChance(), newValue -> {
            character.setFirstPoolBattleChance(newValue);
            refreshPoolTotal(appState.getCardData().getCharacters(), pool1Totals, Character::getFirstPoolBattleChance);
        });
        pool1Field.setAllowBlanks(true);
        GridPane.setMargin(pool1Field, INSETS);
        gridPane.add(pool1Field, columnIndex++, rowIndex);
        IntegerTextField pool2Field = new IntegerTextField(character.getSecondPoolBattleChance(), newValue -> {
            character.setSecondPoolBattleChance(newValue);
            refreshPoolTotal(appState.getCardData().getCharacters(), pool2Totals, Character::getSecondPoolBattleChance);
        });
        pool2Field.setAllowBlanks(true);
        GridPane.setMargin(pool2Field, INSETS);
        gridPane.add(pool2Field, columnIndex++, rowIndex);
        if(character instanceof BemCharacter bemCharacter) {
            IntegerTextField pool3Field = new IntegerTextField(bemCharacter.getThirdPoolBattleChance(), newValue -> {
                bemCharacter.setThirdPoolBattleChance(newValue);
                refreshPoolTotal(((BemCardData)appState.getCardData()).getCharacters(), pool3Totals, BemCharacter::getThirdPoolBattleChance);
            });
            pool3Field.setAllowBlanks(true);
            GridPane.setMargin(pool3Field, INSETS);
            gridPane.add(pool3Field, columnIndex++, rowIndex);
        }
    }

    private <T extends Character<?, T>> void refreshPoolTotal(List<T> characters, Text poolText, Function<T, Integer> battleChanceFetcher) {
        int total = CardData.getBattleChanceTotal(characters, battleChanceFetcher);
        poolText.setText(Integer.toString(total));
        if(total != 100) {
            poolText.setFill(Color.RED);
        } else {
            poolText.setFill(Color.BLACK);
        }
    }
}
