package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCharacter;
import com.github.cfogrady.dim.modifier.data.bem.BemSpecificFusion;
import com.github.cfogrady.dim.modifier.data.card.Character;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SpecificFusionGridController {
    private final SpriteImageTranslator spriteImageTranslator;
    private final AppState appState;

    @Setter
    private StackPane stackPane;

    public void refreshView(Character<?> character) {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(setupSpecificFusionsGrid((BemCharacter) character));
    }

    private GridPane setupSpecificFusionsGrid(BemCharacter character) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        int rowIndex = 0;
        for(BemSpecificFusion fusion : character.getSpecificFusions()) {
            addRow(gridPane, rowIndex, fusion, character);
            rowIndex++;
        }
        return gridPane;
    }

    private void addRow(GridPane gridPane, int rowIndex, BemSpecificFusion fusion, Character<?> character) {
        int columnIndex = 0;
        gridPane.add(getSupportDimIdColumn((BemCharacter) character, fusion), columnIndex++, rowIndex);
        gridPane.add(getSupportCharacterColumn(fusion), columnIndex++, rowIndex);
        gridPane.add(getEvolveToColumn(fusion), columnIndex++, rowIndex);
        gridPane.add(getDeleteButton(fusion, (BemCharacter) character), columnIndex++, rowIndex);
    }

    private Node getSupportDimIdColumn(BemCharacter character, BemSpecificFusion fusion) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        Text text = new Text("Support DIM ID:");
        vBox.getChildren().add(text);
        IntegerTextField dimId = new IntegerTextField(fusion.getPartnerDimId(), v -> {
            fusion.setPartnerDimId(v);
            refreshView(character);
        });
        vBox.getChildren().add(dimId);
        return vBox;
    }

    private Node getSupportCharacterColumn(BemSpecificFusion fusion) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        Text text = new Text("Support Character:");
        vBox.getChildren().add(text);
        if(fusion.getPartnerDimId() == appState.getCardData().getMetaData().getId()) {
            ImageIntComboBox imageIntComboBox = new ImageIntComboBox();
            imageIntComboBox.initialize(spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters()));
            if(fusion.getSameBemPartnerCharacter() != null) {
                Integer slotId = appState.getCardData().getUuidToCharacterSlot().get(fusion.getSameBemPartnerCharacter());
                if(slotId != null) {
                    imageIntComboBox.setValue(imageIntComboBox.getItemForValue(slotId));
                }
            }
            imageIntComboBox.setOnAction(e -> {
                int value = imageIntComboBox.getValue().getValue();
                fusion.setPartnerDimSlotId(value);
                fusion.setSameBemPartnerCharacter(appState.getCharacter(value).getId());
            });
            vBox.getChildren().add(imageIntComboBox);
        } else {
            IntegerTextField slotId = new IntegerTextField(fusion.getPartnerDimSlotId(), v -> fusion.setPartnerDimSlotId(v));
            slotId.setMax(22);
            vBox.getChildren().add(slotId);
        }
        return vBox;
    }

    private Node getEvolveToColumn(BemSpecificFusion fusion) {
        Label label = new Label("Evolve To:");
        ImageIntComboBox comboBox = new ImageIntComboBox();
        comboBox.initialize(spriteImageTranslator.createImageValuePairs(appState.getIdleForCharacters()));
        if(fusion.getEvolveToCharacterId() != null) {
            Integer evolveToIndex = appState.getCardData().getUuidToCharacterSlot().get(fusion.getEvolveToCharacterId());
            if(evolveToIndex != null) {
                comboBox.setValue(comboBox.getItemForValue(evolveToIndex));
            }
        }
        comboBox.setOnAction(e -> {
            int newSlot = comboBox.getValue().getValue();
            Character<?> newEvolveToCharacter = appState.getCharacter(newSlot);
            fusion.setEvolveToCharacterId(newEvolveToCharacter.getId());
        });
        comboBox.setPrefWidth(120);
        VBox vBox = new VBox(label, comboBox);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        return vBox;
    }

    private Node getDeleteButton(BemSpecificFusion fusion, BemCharacter character) {
        Button button = new Button("Delete Entry");
        button.setOnAction(e -> {
            character.getSpecificFusions().remove(fusion);
            refreshView(character);
        });
        GridPane.setMargin(button, new Insets(10));
        return button;
    }
}
