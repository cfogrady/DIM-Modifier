package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.CurrentSelectionType;
import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteSlotParser;
import com.github.cfogrady.vb.dim.reader.content.DimFusions;
import com.github.cfogrady.vb.dim.reader.content.DimSpecificFusions;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FusionInfoView implements InfoView {
    private final List<DimFusions.DimFusionBlock> dimFusionBlockList;
    private final List<DimSpecificFusions.DimSpecificFusionBlock> dimSpecificFusionBlockList;
    private final SpriteSlotParser spriteSlotParser;

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        for(int i = 0; i < dimFusionBlockList.size(); i++) {
            addRow(gridPane, i);
        }
        VBox vBox = new VBox(gridPane);
        if(dimSpecificFusionBlockList.size() > 0) {
            GridPane specificFusionGridPane = new GridPane();
            specificFusionGridPane.setGridLinesVisible(true);
            for(int i = 0; i < dimSpecificFusionBlockList.size(); i++) {
                addSpecificFusionRow(specificFusionGridPane, i);
            }
            vBox.getChildren().add(new Label("Specific Fusions"));
            vBox.getChildren().add(specificFusionGridPane);
        }
        vBox.setSpacing(10);
        return new ScrollPane(vBox);
    }

    @Override
    public double getPrefWidth() {
        return 1300;
    }

    private void addRow(GridPane gridPane, int rowIndex) {
        DimFusions.DimFusionBlock fusionEntry = dimFusionBlockList.get(rowIndex);
        gridPane.add(getSlotLabel(fusionEntry), 0, rowIndex);
        gridPane.add(getFromEvolutionSprite(fusionEntry), 1, rowIndex);
        gridPane.add(getType1FusionResultLabel(fusionEntry), 2, rowIndex);
        gridPane.add(getType1FusionResultSprite(fusionEntry), 3, rowIndex);
        gridPane.add(getType2FusionResultLabel(fusionEntry), 4, rowIndex);
        gridPane.add(getType2FusionResultSprite(fusionEntry), 5, rowIndex);
        gridPane.add(getType3FusionResultLabel(fusionEntry), 6, rowIndex);
        gridPane.add(getType3FusionResultSprite(fusionEntry), 7, rowIndex);
        gridPane.add(getType4FusionResultLabel(fusionEntry), 8, rowIndex);
        gridPane.add(getType4FusionResultSprite(fusionEntry), 9, rowIndex);
    }

    private void addSpecificFusionRow(GridPane gridPane, int rowIndex) {
        DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry = dimSpecificFusionBlockList.get(rowIndex);
        gridPane.add(getSpecificFusionSlotLabel(specificFusionEntry), 0, rowIndex);
        gridPane.add(getSpecificFusionSlotSprite(specificFusionEntry), 1, rowIndex);
        gridPane.add(getSpecificFusionPartnerDimLabel(specificFusionEntry), 2, rowIndex);
        gridPane.add(getSpecificFusionPartnerSlotLabel(specificFusionEntry), 3, rowIndex);
        gridPane.add(getSpecificFusionResultLabel(specificFusionEntry), 4, rowIndex);
        gridPane.add(getSpecificFusionResultSprite(specificFusionEntry), 5, rowIndex);
    }

    private Node getSlotLabel(DimFusions.DimFusionBlock fusionEntry) {
        Label label = new Label("Evolve From Slot: " + fusionEntry.getStatsIndex());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getFromEvolutionSprite(DimFusions.DimFusionBlock fusionEntry) {
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, fusionEntry.getStatsIndex(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType1FusionResultLabel(DimFusions.DimFusionBlock fusionEntry) {
        String labelText;
        if(fusionEntry.getStatsIndexForFusionWithType1() == LoadedScene.NONE_VALUE) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(fusionEntry.getStatsIndexForFusionWithType1());
        }
        Label label = new Label("Type 1 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType1FusionResultSprite(DimFusions.DimFusionBlock fusionEntry) {
        if(fusionEntry.getStatsIndexForFusionWithType1() == LoadedScene.NONE_VALUE) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, fusionEntry.getStatsIndexForFusionWithType1(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType2FusionResultLabel(DimFusions.DimFusionBlock fusionEntry) {
        String labelText;
        if(fusionEntry.getStatsIndexForFusionWithType2() == LoadedScene.NONE_VALUE) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(fusionEntry.getStatsIndexForFusionWithType2());
        }
        Label label = new Label("Type 2 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType2FusionResultSprite(DimFusions.DimFusionBlock fusionEntry) {
        if(fusionEntry.getStatsIndexForFusionWithType2() == LoadedScene.NONE_VALUE) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, fusionEntry.getStatsIndexForFusionWithType2(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType3FusionResultLabel(DimFusions.DimFusionBlock fusionEntry) {
        String labelText;
        if(fusionEntry.getStatsIndexForFusionWithType3() == LoadedScene.NONE_VALUE) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(fusionEntry.getStatsIndexForFusionWithType3());
        }
        Label label = new Label("Type 3 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType3FusionResultSprite(DimFusions.DimFusionBlock fusionEntry) {
        if(fusionEntry.getStatsIndexForFusionWithType3() == LoadedScene.NONE_VALUE) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, fusionEntry.getStatsIndexForFusionWithType3(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType4FusionResultLabel(DimFusions.DimFusionBlock fusionEntry) {
        String labelText;
        if(fusionEntry.getStatsIndexForFusionWithType4() == LoadedScene.NONE_VALUE) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(fusionEntry.getStatsIndexForFusionWithType4());
        }
        Label label = new Label("Type 4 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType4FusionResultSprite(DimFusions.DimFusionBlock fusionEntry) {
        if(fusionEntry.getStatsIndexForFusionWithType4() == LoadedScene.NONE_VALUE) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, fusionEntry.getStatsIndexForFusionWithType4(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getSpecificFusionSlotLabel(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry) {
        Label label = new Label("Evolve From Slot: " + specificFusionEntry.getStatsIndex());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionSlotSprite(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry) {
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, specificFusionEntry.getStatsIndex(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getSpecificFusionPartnerDimLabel(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry) {
        Label label = new Label("Fusion Partner DIM Id: " + specificFusionEntry.getFusionDimId());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionPartnerSlotLabel(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry) {
        Label label = new Label("Fusion Partner Slot Id: " + specificFusionEntry.getFusionDimSlotId());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionResultLabel(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry) {
        Label label = new Label("Fusion Result Slot Id: " + specificFusionEntry.getStatsIndexForFusionResult());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionResultSprite(DimSpecificFusions.DimSpecificFusionBlock specificFusionEntry) {
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, specificFusionEntry.getStatsIndexForFusionResult(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }
}
