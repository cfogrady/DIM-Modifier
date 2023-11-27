package com.github.cfogrady.dim.modifier.controllers;

import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.bem.BemCharacter;
import com.github.cfogrady.dim.modifier.data.card.SpecificFusion;
import com.github.cfogrady.dim.modifier.data.bem.BemTransformationEntry;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.dim.DimCharacter;
import com.github.cfogrady.dim.modifier.data.dim.DimTransformationEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
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
    private IntegerTextField timeUntilFusionChecksBox;
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
    private Character<?, ?> character;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordion.setExpandedPane(accordion.getPanes().get(0));
        regularTransformationsGridController.setStackPane(regularTransformationHolder);
        specificFusionGridController.setStackPane(specificFusionHolder);
        attributeFusionGridController.setStackPane(attributeFusionHolder);
        timeUntilFusionChecksBox.setAllowBlanks(true);
    }

    public void refreshAll() {
        refreshRegularTransformations();
        refreshSpecificFusions();
        refreshAttributeFusions();
        if(character instanceof BemCharacter bemCharacter) {
            timeUntilFusionChecksBox.setChangeReceiver(null);
            if(bemCharacter.getMinutesUntilFusionCheck() != null) {
                timeUntilFusionChecksBox.setText(bemCharacter.getMinutesUntilFusionCheck().toString());
            } else {
                timeUntilFusionChecksBox.setText("");
            }
            timeUntilFusionChecksBox.setChangeReceiver(bemCharacter::setMinutesUntilFusionCheck);
        } else if(character instanceof DimCharacter dimCharacter) {
            timeUntilFusionChecksBox.setChangeReceiver(null);
            if(dimCharacter.getHoursUntilFusionCheck() != null) {
                timeUntilFusionChecksBox.setText(dimCharacter.getHoursUntilFusionCheck().toString());
            } else {
                timeUntilFusionChecksBox.setText("");
            }
            timeUntilFusionChecksBox.setChangeReceiver(dimCharacter::setHoursUntilFusionCheck);
        }
    }

    private void refreshRegularTransformations() {
        regularTransformationsGridController.refreshView(character);
        addRegularTransformationButton.setOnAction(e -> {
            if (character instanceof BemCharacter bemCharacter) {
                bemCharacter.getTransformationEntries().add(BemTransformationEntry.builder().build());
                refreshRegularTransformations();
            } else if(character instanceof DimCharacter dimCharacter) {
                dimCharacter.getTransformationEntries().add(DimTransformationEntity.builder().build());
                refreshRegularTransformations();
            } else {
                throw new IllegalStateException("Unknown character type!");
            }
        });
    }

    private void refreshAttributeFusions() {
        attributeFusionGridController.refreshAll(character);
    }
    private void refreshSpecificFusions() {
        specificFusionGridController.refreshView(character);
        specificFusionButton.setOnAction(e -> {
            character.getSpecificFusions().add(SpecificFusion.builder().build());
            refreshSpecificFusions();
        });
    }
}
