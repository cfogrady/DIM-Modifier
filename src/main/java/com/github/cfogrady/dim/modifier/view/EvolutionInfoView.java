package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.CurrentSelectionType;
import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.EvolutionEntry;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class EvolutionInfoView implements InfoView {
    private final DimData dimData;
    private final SpriteImageTranslator spriteImageTranslator;

    @Override
    public Node setupView(SelectionState selectionState) {
        if(selectionState.getSelectionType() != CurrentSelectionType.SLOT) {
            return new Pane();
        }
        MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(selectionState.getSlot());
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        int index = 0;
        for(EvolutionEntry evolutionEntry : monsterSlot.getEvolutionEntries()) {
            addRow(gridPane, index, monsterSlot, evolutionEntry);
            index++;
        }
        VBox vBox = new VBox(setupHoursUntilEvolutionLabel(monsterSlot), gridPane);
        vBox.setSpacing(10);
        return vBox;
    }

    @Override
    public double getPrefWidth() {
        return 1000;
    }

    private void addRow(GridPane gridPane, int rowIndex, MonsterSlot monsterSlot, EvolutionEntry evolutionEntry) {
        gridPane.add(getEvolveFromImage(monsterSlot), 0, rowIndex);
        gridPane.add(getVitalValueRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 1, rowIndex);
        gridPane.add(getTrophiesRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 2, rowIndex);
        gridPane.add(getBattlesRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 3, rowIndex);
        gridPane.add(getWinRatioRequirementLabel(evolutionEntry.getEvolutionRequirementBlock()), 4, rowIndex);
        gridPane.add(getEvolveToSlotLabel(monsterSlot, evolutionEntry), 5, rowIndex);
        gridPane.add(getEvolveToImage(evolutionEntry.getToMonster()), 6, rowIndex);
    }

    private Node setupHoursUntilEvolutionLabel(MonsterSlot monsterSlot) {
        Label label = new Label("Hours Until Evolution (" + DimReader.NONE_VALUE + " for NONE):");
        IntegerTextField integerTextField = new IntegerTextField(monsterSlot.getHoursUntilEvolution(), monsterSlot::setHoursUntilEvolution);
        integerTextField.setPrefWidth(60);
        integerTextField.setMin(1);
        HBox hbox = new HBox(label, integerTextField);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setMargin(hbox, new Insets(10));
        return hbox;
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

    private Node getEvolveToSlotLabel(MonsterSlot fromMonster, EvolutionEntry evolutionEntry) {
        String evolveToSlot;
        if(evolutionEntry.getToMonster() == null) {
            if(fromMonster.getHoursUntilEvolution() == LoadedScene.NONE_VALUE) {
                evolveToSlot = LoadedScene.NONE_LABEL;
            } else {
                evolveToSlot = "Fusion";
            }
        } else {
            evolveToSlot = Integer.toString(dimData.getMonsterSlotIndexForId(evolutionEntry.getToMonster()));
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
