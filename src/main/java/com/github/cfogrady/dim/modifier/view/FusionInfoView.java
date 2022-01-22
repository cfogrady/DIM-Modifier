package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.vb.dim.reader.content.DimFusions;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FusionInfoView implements InfoView {
    private final List<DimFusions.DimFusionBlock> dimFusionBlockList;

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        for(int i = 0; i < dimFusionBlockList.size(); i++) {
            addRow(gridPane, i);
        }
        return gridPane;
    }

    @Override
    public double getPrefWidth() {
        return 600;
    }

    private void addRow(GridPane gridPane, int rowIndex) {
        gridPane.add(getSlotLabel(rowIndex), 0, rowIndex);
        gridPane.add(getHoursForEvolutionLabel(rowIndex), 0, rowIndex);
    }

    private Node getSlotLabel(int index) {
        Label label = new Label("Evolve From Slot: " + dimFusionBlockList.get(index).getStatsIndex());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getHoursForEvolutionLabel(int index) {
        Label label = new Label("Hours For Evolutions: " + dimFusionBlockList.get(index).getStatsIndex());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }
}
