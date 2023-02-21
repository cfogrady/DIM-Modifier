package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.*;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCharacter;
import com.github.cfogrady.dim.modifier.data.bem.BemTransformationEntry;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.card.TransformationEntry;
import com.github.cfogrady.vb.dim.card.DimReader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor()
public class RegularTransformationsGridController {
    private final SpriteImageTranslator spriteImageTranslator;
    private final AppState appState;

    @Setter
    private StackPane stackPane;

    public void refreshView(Character<?, ?> character) {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(setupRegularTransformationsGrid(character));
    }

    private GridPane setupRegularTransformationsGrid(Character<?, ?> character) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        int rowIndex = 0;
        for(TransformationEntry transformation : character.getTransformationEntries()) {
            addRow(gridPane, rowIndex, transformation, character);
            rowIndex++;
        }
        return gridPane;
    }

    private void addRow(GridPane gridPane, int rowIndex, TransformationEntry transformationEntry, Character<?, ?> character) {
        int columnIndex = 0;
        gridPane.add(getEvolveToColumn(transformationEntry), columnIndex++, rowIndex);
        gridPane.add(getVitalValueRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        gridPane.add(getTrophiesRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        gridPane.add(getBattlesRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        gridPane.add(getWinRatioRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        if(transformationEntry instanceof BemTransformationEntry bemTransformationEntry) {
            gridPane.add(getAreaCompletionLabel(bemTransformationEntry), columnIndex++, rowIndex);
            gridPane.add(getSecretLabel(bemTransformationEntry), columnIndex++, rowIndex);
        }
        gridPane.add(getDeleteButton(transformationEntry, character), columnIndex++, rowIndex);
    }

    private Node getEvolveToColumn(TransformationEntry transformationEntry) {
        Label label = new Label("Evolve To:");
        ImageIntComboBox comboBox = new ImageIntComboBox();
        comboBox.initialize(spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters()));
        if(transformationEntry.getToCharacter() != null) {
            Integer evolveToIndex = appState.getCardData().getUuidToCharacterSlot().get(transformationEntry.getToCharacter());
            if(evolveToIndex != null) {
                comboBox.setValue(comboBox.getItemForValue(evolveToIndex));
            }
        }
        comboBox.setOnAction(e -> {
            int newSlot = comboBox.getValue().getValue().intValue();
            transformationEntry.setToCharacter(appState.getCharacter(newSlot).getId());
        });
        comboBox.setPrefWidth(120);
        VBox vBox = new VBox(label, comboBox);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getVitalValueRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("Vital Value Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getVitalRequirements(), transformationEntry::setVitalRequirements);
        textField.setMax(10000);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getTrophiesRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("PP Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getTrophyRequirement(), transformationEntry::setTrophyRequirement);
        textField.setMax(10000);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getBattlesRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("Battles Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getBattleRequirement(), transformationEntry::setBattleRequirement);
        textField.setMax(10000);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getWinRatioRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("Win Ratio Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getWinRatioRequirement(), transformationEntry::setWinRatioRequirement);
        textField.setMin(0);
        textField.setMax(100);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getAreaCompletionLabel(BemTransformationEntry transformationEntry) {
        Label label = new Label("Adventure Completion:");
        IntegerComboBox integerComboBox = new IntegerComboBox(transformationEntry.getRequiredCompletedAdventureLevel(), 12, 1, true, transformationEntry::setRequiredCompletedAdventureLevel);
        VBox vBox = new VBox(label, integerComboBox);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getSecretLabel(BemTransformationEntry transformationEntry) {
        CheckBox checkBox = new CheckBox("Secret");
        checkBox.setSelected(transformationEntry.isSecret());
        checkBox.setOnMouseClicked(e -> {
            transformationEntry.setSecret(checkBox.isSelected());
        });
        GridPane.setMargin(checkBox, new Insets(10));
        return checkBox;
    }

    private Node getDeleteButton(TransformationEntry transformationEntry, Character<?, ?> character) {
        Button button = new Button("Delete Entry");
        button.setOnAction(e -> {
            character.getTransformationEntries().remove(transformationEntry);
            refreshView(character);
        });
        GridPane.setMargin(button, new Insets(10));
        return button;
    }
}
