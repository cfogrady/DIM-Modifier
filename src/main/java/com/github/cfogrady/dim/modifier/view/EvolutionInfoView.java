package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.CurrentSelectionType;
import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteSlotParser;
import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EvolutionInfoView implements InfoView {
    private final List<DimEvolutionRequirements.DimEvolutionRequirementBlock> dimEvolutionRequirementBlocks;
    private final SpriteSlotParser spriteSlotParser;
    // May not work with more than 34 entries

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        for(int i = 0; i < dimEvolutionRequirementBlocks.size(); i++) {
            addRow(gridPane, i);
        }
        ScrollPane scrollPane = new ScrollPane(gridPane);
        return scrollPane;
    }

    @Override
    public double getPrefWidth() {
        return 1300;
    }

    private void addRow(GridPane gridPane, int rowIndex) {
        DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock = dimEvolutionRequirementBlocks.get(rowIndex);
        gridPane.add(getSlotLabel(evolutionRequirementBlock), 0, rowIndex);
        gridPane.add(getEvolveFromImage(evolutionRequirementBlock), 1, rowIndex);
        gridPane.add(getHoursForEvolutionLabel(evolutionRequirementBlock), 2, rowIndex);
        gridPane.add(getVitalValueRequirementLabel(evolutionRequirementBlock), 3, rowIndex);
        gridPane.add(getTrophiesRequirementLabel(evolutionRequirementBlock), 4, rowIndex);
        gridPane.add(getBattlesRequirementLabel(evolutionRequirementBlock), 5, rowIndex);
        gridPane.add(getWinRationRequirementLabel(evolutionRequirementBlock), 6, rowIndex);
        gridPane.add(getEvolveToSlotLabel(evolutionRequirementBlock), 7, rowIndex);
        gridPane.add(getEvolveToImage(evolutionRequirementBlock), 8, rowIndex);
    }

    private Node getSlotLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        Label label = new Label("Evolve From Slot: " + evolutionRequirementBlock.getEvolveFromStatIndex());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getEvolveFromImage(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, evolutionRequirementBlock.getEvolveFromStatIndex(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getHoursForEvolutionLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        String hours = evolutionRequirementBlock.getHoursUntilEvolution() == LoadedScene.NONE_VALUE ? LoadedScene.NONE_LABEL : Integer.toString(evolutionRequirementBlock.getHoursUntilEvolution());
        Label label = new Label("Hours For Evolutions: " + hours);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getVitalValueRequirementLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        Label label = new Label("Vital Value Requirement: " + evolutionRequirementBlock.getVitalRequirements());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getTrophiesRequirementLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        Label label = new Label("Trophies Requirement: " + evolutionRequirementBlock.getTrophyRequirement());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBattlesRequirementLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        Label label = new Label("Battles Requirement: " + evolutionRequirementBlock.getBattleRequirement());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getWinRationRequirementLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        Label label = new Label("Win Ration Requirement: " + evolutionRequirementBlock.getWinRatioRequirement());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getEvolveToSlotLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        String evolveToSlot;
        if(evolutionRequirementBlock.getEvolveToStatIndex() == LoadedScene.NONE_VALUE) {
            if(evolutionRequirementBlock.getHoursUntilEvolution() == LoadedScene.NONE_VALUE) {
                evolveToSlot = LoadedScene.NONE_LABEL;
            } else {
                evolveToSlot = "Fusion";
            }
        } else {
            evolveToSlot = Integer.toString(evolutionRequirementBlock.getEvolveToStatIndex());
        }
        Label label = new Label("Evolve To Slot: " + evolveToSlot);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getEvolveToImage(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        if(evolutionRequirementBlock.getEvolveToStatIndex() == LoadedScene.NONE_VALUE) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, evolutionRequirementBlock.getEvolveToStatIndex(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }
}
