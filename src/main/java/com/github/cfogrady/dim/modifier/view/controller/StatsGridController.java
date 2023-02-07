package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.controls.LabelValuePair;
import com.github.cfogrady.dim.modifier.controls.StringIntComboBox;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCharacter;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.dim.card.DimCharacter;
import com.github.cfogrady.vb.dim.card.DimReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.github.cfogrady.dim.modifier.LoadedScene.NONE_LABEL;

@RequiredArgsConstructor
public class StatsGridController {
    private final static Background BLACK_BACKGROUND = new Background(new BackgroundFill(Color.color(0,0,0), CornerRadii.EMPTY, Insets.EMPTY));

    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;


    public Node refreshStatsGrid(Character<?> character) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.add(setupStageLabel(character), 0, 0);
        gridPane.add(setupAttributeLabel(character), 1, 0);
        gridPane.add(setupDispositionLabel(character), 0, 1);
        gridPane.add(setupDPLabel(character), 1, 1);
        gridPane.add(setupHpLabel(character), 0, 2);
        gridPane.add(setupApLabel(character), 1, 2);
        gridPane.add(setupSmallAttackLabel(character), 0, 3);
        gridPane.add(setupBigAttackLabel(character), 1, 3);
        gridPane.setGridLinesVisible(true);
        //gridPane.add(setupLockedLabel(character), 1, 0);
        //gridPane.add(setupDPStarsLabel(character), 0, 3);
        return gridPane;
    }

    private Node setupStageLabel(Character<?> character) {
        Label label = new Label("Stage: ");
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setValue(character.getStage() + 1);
        comboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));
        comboBox.setOnAction(event -> {
            int value = comboBox.getValue() - 1;
            character.setStage(value);
            if(value < 2) {
                character.setBp(DimReader.NONE_VALUE);
                character.setHp(DimReader.NONE_VALUE);
                character.setAp(DimReader.NONE_VALUE);
                character.setFirstPoolBattleChance(DimReader.NONE_VALUE);
                character.setSecondPoolBattleChance(DimReader.NONE_VALUE);
                if(character instanceof BemCharacter bemCharacter) {
                    character.setBigAttack(255);
                    character.setSmallAttack(255);
                    bemCharacter.setThirdPoolBattleChance(DimReader.NONE_VALUE);
                } else {
                    character.setBigAttack(DimReader.NONE_VALUE);
                    character.setSmallAttack(DimReader.NONE_VALUE);
                }
                refreshStatsGrid(character);
            }
        });
        comboBox.setPrefWidth(20);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

//    private Node setupLockedLabel(Character<?> character) {
//        Label label = new Label("Requires Unlock:");
//        ComboBox<Boolean> comboBox = new ComboBox<>();
//        comboBox.setItems(FXCollections.observableArrayList(false, true));
//        comboBox.setValue(monsterSlot.getStatBlock().isUnlockRequired());
//        if(selectionState.isSafetyModeOn() && selectionState.isOnBabySlot()) {
//            comboBox.setDisable(true);
//        }
//        comboBox.setOnAction(event -> {
//            boolean value = comboBox.getValue();
//            monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().unlockRequired(value).build());
//        });
//        comboBox.setPrefWidth(80);
//        HBox hBox = new HBox(label, comboBox);
//        hBox.setSpacing(10);
//        hBox.setAlignment(Pos.CENTER_LEFT);
//        GridPane.setMargin(hBox, new Insets(10));
//        return hBox;
//    }

    private Node setupAttributeLabel(Character<?> character) {
        if(character.getStage() < 2 && character instanceof DimCharacter) {
            Label label = new Label("Attribute: " + character.getAttribute());
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Attribute:");
        HBox hBox = new HBox(label);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        if(character instanceof BemCharacter bemCharacter) {
            hBox.getChildren().add(getBemAttributeComboBox(bemCharacter));
        } else {
            StringIntComboBox comboBox = new StringIntComboBox(character.getAttribute(), getAttributes(), character::setAttribute);
            hBox.getChildren().add(comboBox);
        }
        return hBox;
    }

    private ObservableList<StringIntComboBox.StringIntPair> getAttributes() {
        StringIntComboBox.StringIntPair virus = new StringIntComboBox.StringIntPair("Virus", 1);
        StringIntComboBox.StringIntPair data = new StringIntComboBox.StringIntPair("Data", 2);
        StringIntComboBox.StringIntPair vaccine = new StringIntComboBox.StringIntPair("Vaccine", 3);
        StringIntComboBox.StringIntPair free = new StringIntComboBox.StringIntPair("Free", 4);
        return FXCollections.observableArrayList(virus, data, vaccine, free);
    }

    private ImageIntComboBox getBemAttributeComboBox(BemCharacter bemCharacter) {
        ImageIntComboBox imageIntComboBox = new ImageIntComboBox();
        imageIntComboBox.initialize(bemCharacter.getAttribute()-1, spriteImageTranslator.createImageValuePairs(appState.getCardData().getCardSprites().getTypes()), 1.0, BLACK_BACKGROUND, null);
        imageIntComboBox.setOnAction(e -> {
            bemCharacter.setAttribute(imageIntComboBox.getValue().getValue()+1);
        });
        return imageIntComboBox;
    }

    private Node setupDispositionLabel(Character<?> character) {
        Label label = new Label("Disposition:");
        StringIntComboBox comboBox = new StringIntComboBox(character.getActivityType(), getActivityTypes(), character::setActivityType);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private ObservableList<StringIntComboBox.StringIntPair> getActivityTypes() {
        StringIntComboBox.StringIntPair stoic = new StringIntComboBox.StringIntPair("Stoic", 0);
        StringIntComboBox.StringIntPair active = new StringIntComboBox.StringIntPair("Active", 1);
        StringIntComboBox.StringIntPair normal = new StringIntComboBox.StringIntPair("Normal", 2);
        StringIntComboBox.StringIntPair indoor = new StringIntComboBox.StringIntPair("Indoor", 3);
        StringIntComboBox.StringIntPair lazy = new StringIntComboBox.StringIntPair("Lazy", 4);
        return FXCollections.observableArrayList(stoic, active, normal, indoor, lazy);
    }

    private Node setupSmallAttackLabel(Character<?> character) {
        if(character.getStage() < 2) {
            Label label = new Label("Small Attack: " + LoadedScene.NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Small Attack:");
        List<SpriteData.Sprite> attackSprites = appState.getFirmwareData().getSmallAttacks();
        attackSprites.addAll(appState.getCardData().getCardSprites().getSmallAttacks());
        ImageIntComboBox comboBox = new ImageIntComboBox();
        comboBox.initialize(character.getSmallAttack(), spriteImageTranslator.createImageValuePairs(attackSprites), 1.0, null, null);
        comboBox.setOnAction(e -> character.setSmallAttack(comboBox.getValue().getValue()));
        comboBox.setPrefWidth(120);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private Node setupBigAttackLabel(Character<?> character) {
        if(character.getStage() < 2) {
            Label label = new Label("Big Attack: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("Big Attack:");
        List<SpriteData.Sprite> attackSprites = appState.getFirmwareData().getBigAttacks();
        attackSprites.addAll(appState.getCardData().getCardSprites().getBigAttacks());
        ImageIntComboBox comboBox = new ImageIntComboBox();
        comboBox.initialize(character.getBigAttack(), spriteImageTranslator.createImageValuePairs(attackSprites), 1.0, null, null);
        comboBox.setOnAction(e -> character.setBigAttack(comboBox.getValue().getValue()));
        comboBox.setPrefWidth(120);
        HBox hBox = new HBox(label, comboBox);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hBox, new Insets(10));
        return hBox;
    }

    private <V, L, T extends LabelValuePair<V, L>> T getItemForValue(ComboBox<T> comboBox, V value) {
        for(T option : comboBox.getItems()) {
            if(option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

//    private Node setupDPStarsLabel(Character<?> character) {
//        if(monsterSlot.getStatBlock().getStage() < 2) {
//            Label label = new Label("DP (stars): " + NONE_LABEL);
//            GridPane.setMargin(label, new Insets(10));
//            return label;
//        }
//        Label label = new Label("DP (stars):");
//        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getStatBlock().getDpStars(), value ->
//                monsterSlot.setStatBlock(monsterSlot.getStatBlock().toBuilder().dpStars(value).build()));
//        integerTextField.setPrefWidth(60);
//        integerTextField.setMax(10);
//        HBox hbox = new HBox(label, integerTextField);
//        hbox.setSpacing(10);
//        hbox.setAlignment(Pos.CENTER_LEFT);
//        GridPane.setMargin(hbox, new Insets(10));
//        return hbox;
//    }

    private Node setupDPLabel(Character<?> character) {
        if(character.getStage() < 2) {
            Label label = new Label("BP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("BP:");
        IntegerTextField integerTextField = new IntegerTextField(character.getBp(), character::setBp);
        integerTextField.setPrefWidth(60);
        integerTextField.setMax(75);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupHpLabel(Character<?> character) {
        if(character.getStage() < 2) {
            Label label = new Label("HP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("HP:");
        IntegerTextField integerTextField = new IntegerTextField(character.getHp(), character::setHp);
        integerTextField.setPrefWidth(60);
        integerTextField.setMax(22);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node setupApLabel(Character<?> character) {
        if(character.getStage() < 2) {
            Label label = new Label("AP: " + NONE_LABEL);
            GridPane.setMargin(label, new Insets(10));
            return label;
        }
        Label label = new Label("AP:");
        IntegerTextField integerTextField = new IntegerTextField(character.getAp(), character::setAp);
        integerTextField.setMax(9);
        integerTextField.setPrefWidth(60);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }
}