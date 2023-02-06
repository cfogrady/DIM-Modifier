package com.github.cfogrady.dim.modifier.view.controller;

import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.MetaData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class LoadedViewController implements Initializable {
    private final AppState appState;
    private final Node charactersSubView;
    private final CharacterViewController characterViewController;
    private final BattlesViewController battlesViewController;
    private final Node battlesSubView;
    private final SystemViewController systemViewController;
    private final Node systemSubView;

    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private CheckBox onlySafeValuesCheckbox;
    @FXML
    private Text dimIdText;
    @FXML
    private Text revisionIdText;
    @FXML
    private Text factoryDateText;
    @FXML
    private Text checksumText;
    @FXML
    private Button charactersButton;
    @FXML
    private Button battlesButton;
    @FXML
    private Button systemButton;
    @FXML
    private AnchorPane subView;

    private SubViewSelection subViewSelection = SubViewSelection.CHARACTERS;

    private enum SubViewSelection {
        CHARACTERS,
        BATTLES,
        SYSTEM;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        charactersButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.CHARACTERS;
            refreshButtons();
            refreshSubview();
        });
        battlesButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.BATTLES;
            refreshButtons();
            refreshSubview();
        });
        systemButton.setOnAction(e -> {
            subViewSelection = SubViewSelection.SYSTEM;
            refreshButtons();
            refreshSubview();
        });
    }

    public void refreshAll() {
        MetaData metaData = appState.getCardData().getMetaData();
        onlySafeValuesCheckbox.setSelected(appState.isSafetyModeOn());
        onlySafeValuesCheckbox.setOnAction(e -> {
            appState.setSafetyModeOn(!appState.isSafetyModeOn());
        });
        dimIdText.setText("DIM ID: " + metaData.getId());
        revisionIdText.setText("Revision: " + metaData.getRevision());
        factoryDateText.setText("Factory Date: " + metaData.getYear() + "/" + metaData.getMonth() + "/" + metaData.getDay());
        checksumText.setText("Checksum At Load: " + Integer.toHexString(metaData.getOriginalChecksum()));
        refreshButtons();
        refreshSubview();
    }

    public void refreshButtons() {
        charactersButton.setDisable(subViewSelection == SubViewSelection.CHARACTERS);
        battlesButton.setDisable(subViewSelection == SubViewSelection.BATTLES);
        systemButton.setDisable(subViewSelection == SubViewSelection.SYSTEM);
    }

    public void refreshSubview() {
        Node subViewNode = getSubview();
        subView.getChildren().clear();
        subView.getChildren().add(subViewNode);
    }

    private Node getSubview() {
        switch (subViewSelection) {
            case CHARACTERS -> {
                characterViewController.refreshAll();
                return charactersSubView;
            }
            case BATTLES -> {
                battlesViewController.refreshAll();
                return battlesSubView;
            }
            case SYSTEM -> {
                systemViewController.refreshAll();
                return systemSubView;
            }
            default -> {
                return null;
            }
        }
    }
}
