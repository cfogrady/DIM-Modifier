package com.github.cfogrady.dim.modifier.view;

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
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BemEvolutionsView {
    private final ImageIntComboBoxFactory imageIntComboBoxFactory;
    private final AppState appState;
    @Getter
    private final GridPane mainView;

    public static class EvolutionsViewState extends BemCharactersView.CharacterViewState {
    }

    public static EvolutionsViewState fromCharacterViewState(BemCharactersView.CharacterViewState original) {
        EvolutionsViewState newState = new EvolutionsViewState();
        newState.copyFrom(original);
        return newState;
    }

    public void refreshView(EvolutionsViewState state) {
        if(mainView.isGridLinesVisible()) {
            Node node = mainView.getChildren().get(0);
            mainView.getChildren().clear();
            mainView.getChildren().add(node);
        } else {
            mainView.getChildren().clear();
        }
        int rowIndex = 0;
        for(TransformationEntry transformation : state.getCharacter(appState).getTransformationEntries()) {
            addRow(rowIndex, transformation, state);
            rowIndex++;
        }
    }

    private void addRow(int rowIndex, TransformationEntry transformationEntry, EvolutionsViewState state) {
        int columnIndex = 0;
        mainView.add(getEvolveToColumn(transformationEntry), columnIndex++, rowIndex);
        mainView.add(getVitalValueRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        mainView.add(getTrophiesRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        mainView.add(getBattlesRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        mainView.add(getWinRatioRequirementLabel(transformationEntry), columnIndex++, rowIndex);
        if(transformationEntry instanceof BemTransformationEntry bemTransformationEntry) {
            mainView.add(getAreaCompletionLabel(bemTransformationEntry), columnIndex++, rowIndex);
        }
        mainView.add(getDeleteButton(transformationEntry, state), columnIndex++, rowIndex);
    }

    Node getEvolveToColumn(TransformationEntry transformationEntry) {
        Label label = new Label("Evolve To:");
        Integer evolveToIndex = appState.getCardData().getUuidToCharacterSlot().get(transformationEntry.getToCharacter());
        ImageIntComboBox comboBox = imageIntComboBoxFactory.createImageIntComboBox(evolveToIndex, appState.getIdleForCharacters(), newSlot -> {
            transformationEntry.setToCharacter(appState.getCardData().getCharacters().get(newSlot).getId());
        });
        comboBox.setPrefWidth(120);
        VBox vBox = new VBox(label, comboBox);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node setupMinutesUntilEvolutionLabel(BemCharacter character) {
        Label label = new Label("Hours Until Evolution (" + DimReader.NONE_VALUE + " for NONE):");
        IntegerTextField integerTextField = new IntegerTextField(character.getMinutesUntilTransformation(), character::setMinutesUntilTransformation);
        integerTextField.setPrefWidth(60);
        integerTextField.setMin(1);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
    }

    private Node getVitalValueRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("Vital Value Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getVitalRequirements(), transformationEntry::setVitalRequirements);
        textField.setMin(0);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getTrophiesRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("PP Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getTrophyRequirement(), transformationEntry::setTrophyRequirement);
        textField.setMin(0);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getBattlesRequirementLabel(TransformationEntry transformationEntry) {
        Label label = new Label("Battles Requirement:");
        IntegerTextField textField = new IntegerTextField(transformationEntry.getBattleRequirement(), transformationEntry::setBattleRequirement);
        textField.setMin(0);
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

    private Node getDeleteButton(TransformationEntry transformationEntry, EvolutionsViewState transformationViewState) {
        Character<?> character = transformationViewState.getCharacter(appState);
        Button button = new Button("Delete Entry");
        button.setOnAction(e -> {
            character.getTransformationEntries().remove(transformationEntry);
            refreshView(transformationViewState);
        });
        GridPane.setMargin(button, new Insets(10));
        return button;
    }
}
