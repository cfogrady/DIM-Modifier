package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.EvolutionEntry;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class EvolutionInfoView implements InfoView {
    private final DimData dimData;
    private final SpriteImageTranslator spriteImageTranslator;
    // May not work with more than 34 entries

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        int index = 0;
        int slotIndex = 0;
        for(MonsterSlot monsterSlot : dimData.getMonsterSlotList()) {
            for(EvolutionEntry evolutionEntry : monsterSlot.getEvolutionEntries()) {
                addRow(gridPane, index, slotIndex, monsterSlot, evolutionEntry);
                index++;
            }
            slotIndex++;
        }
        ScrollPane scrollPane = new ScrollPane(gridPane);
        return scrollPane;
    }

    @Override
    public double getPrefWidth() {
        return 1300;
    }

    private void addRow(GridPane gridPane, int rowIndex, int slotIndex, MonsterSlot monsterSlot, EvolutionEntry evolutionEntry) {
        gridPane.add(getSlotLabel(slotIndex), 0, rowIndex);
        gridPane.add(getEvolveFromImage(monsterSlot), 1, rowIndex);
        gridPane.add(getHoursForEvolutionLabel(evolutionEntry.getEvolutionRequirementBlock()), 2, rowIndex);
        gridPane.add(getVitalValueRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 3, rowIndex);
        gridPane.add(getTrophiesRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 4, rowIndex);
        gridPane.add(getBattlesRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 5, rowIndex);
        gridPane.add(getWinRatioRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 6, rowIndex);
        gridPane.add(getEvolveToSlotLabel(evolutionEntry.getEvolutionRequirementBlock()), 7, rowIndex);
        gridPane.add(getEvolveToImage(evolutionEntry.getToMonster()), 8, rowIndex);
    }

    private Node getSlotLabel(int slotIndex) {
        Label label = new Label("Evolve From Slot: " + slotIndex);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getEvolveFromImage(MonsterSlot monsterSlot) {
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(monsterSlot.getSprites().get(1)));
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

    private Node getWinRatioRequirementLabel(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock) {
        Label label = new Label("Win Ratio Requirement: " + evolutionRequirementBlock.getWinRatioRequirement());
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

    private Node getEvolveToImage(UUID toMonster) {
        if(toMonster == null) {
            return new Pane();
        }
        int slotIndex = dimData.getMonsterSlotIndexById().get(toMonster);
        SpriteData.Sprite toMonsterSprite = dimData.getMonsterSlotList().get(slotIndex).getSprites().get(1);
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(toMonsterSprite));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }
}
