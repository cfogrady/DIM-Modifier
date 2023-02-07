package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.bem.BemCharacter;
import com.github.cfogrady.dim.modifier.data.bem.BemSpecificFusion;
import com.github.cfogrady.dim.modifier.data.bem.BemTransformationEntry;
import com.github.cfogrady.dim.modifier.data.card.Character;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class TransformationViewController implements Initializable {
    private final RegularTransformationsGridController regularTransformationsGridController;
    private final SpecificFusionGridController specificFusionGridController;
    private final AttributeFusionGridController attributeFusionGridController;

    @FXML
    private IntegerTextField timeToEvolveBox;
    @FXML
    private Accordion accordion;
    @FXML
    private StackPane regularTransformationHolder;
    @FXML
    private Button addRegularTransformationButton;
    @FXML
    private StackPane attributeFusionHolder;
    @FXML
    private StackPane specificFusionHolder;
    @FXML
    private Button specificFusionButton;

    @Setter
    private Character<?> character;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordion.setExpandedPane(accordion.getPanes().get(0));
        regularTransformationsGridController.setStackPane(regularTransformationHolder);
        specificFusionGridController.setStackPane(specificFusionHolder);
        attributeFusionGridController.setStackPane(attributeFusionHolder);
        timeToEvolveBox.setAllowBlanks(true);
    }

    public void refreshAll() {
        refreshRegularTransformations();
        refreshSpecificFusions();
        refreshAttributeFusions();
        if(character instanceof BemCharacter bemCharacter) {
            timeToEvolveBox.setChangeReceiver(null);
            if(bemCharacter.getMinutesUntilTransformation() != null) {
                timeToEvolveBox.setText(bemCharacter.getMinutesUntilTransformation().toString());
            } else {
                timeToEvolveBox.setText("");
            }
            timeToEvolveBox.setChangeReceiver(bemCharacter::setMinutesUntilTransformation);
        }
    }

    private void refreshRegularTransformations() {
        regularTransformationsGridController.refreshView(character);
        addRegularTransformationButton.setOnAction(e -> {
            if (character instanceof BemCharacter bemCharacter) {
                bemCharacter.getTransformationEntries().add(BemTransformationEntry.builder().build());
                refreshRegularTransformations();
            } else {
                throw new IllegalStateException("Can only handle BemCharacters right now!");
            }
        });
    }

    private void refreshAttributeFusions() {
        attributeFusionGridController.refreshAll(character);
    }
    private void refreshSpecificFusions() {
        specificFusionGridController.refreshView(character);
        specificFusionButton.setOnAction(e -> {
            if (character instanceof BemCharacter bemCharacter) {
                bemCharacter.getSpecificFusions().add(BemSpecificFusion.builder().build());
                refreshSpecificFusions();
            }
        });
    }
}
