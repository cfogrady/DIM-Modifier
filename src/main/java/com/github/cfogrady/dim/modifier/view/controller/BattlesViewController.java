package com.github.cfogrady.dim.modifier.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BattlesViewController implements Initializable {
    private final NFCGridController nfcGridController;
    private final AdventureGridController adventureGridController;

    @FXML
    private StackPane nfcGridHolder;
    @FXML
    private StackPane adventuresGridHolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nfcGridController.setStackPane(nfcGridHolder);
        adventureGridController.setStackPane(adventuresGridHolder);
    }

    public void refreshAll() {
        nfcGridController.refreshAll();
        adventureGridController.refreshAll();
    }
}
