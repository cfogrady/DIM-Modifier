package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.CurrentSelectionType;
import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.controls.SlotComboBox;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.EvolutionEntry;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private final Runnable sceneRefresher;

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
        VBox vBox = new VBox(setupHoursUntilEvolutionLabel(monsterSlot), gridPane, getAddEntry(monsterSlot));
        vBox.setSpacing(10);
        return vBox;
    }

    @Override
    public double getPrefWidth() {
        return 1000;
    }

    private void addRow(GridPane gridPane, int rowIndex, MonsterSlot monsterSlot, EvolutionEntry evolutionEntry) {
        gridPane.add(getVitalValueRequirementLabel(evolutionEntry), 0, rowIndex);
        gridPane.add(getTrophiesRequirementLabel(evolutionEntry), 1, rowIndex);
        gridPane.add(getBattlesRequirementLabel(evolutionEntry), 2, rowIndex);
        gridPane.add(getWinRatioRequirementLabel(evolutionEntry), 3, rowIndex);
        gridPane.add(getEvolveToSlotLabel(monsterSlot, evolutionEntry), 4, rowIndex);
        gridPane.add(getEvolveToImage(evolutionEntry.getToMonster()), 5, rowIndex);
        gridPane.add(getDeleteEntry(monsterSlot, rowIndex), 6, rowIndex);
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

    private Node getVitalValueRequirementLabel(EvolutionEntry evolutionEntry) {
        Label label = new Label("Vital Value Requirement:");
        IntegerTextField textField = new IntegerTextField(evolutionEntry.getEvolutionRequirementBlock().getVitalRequirements(), val -> {
            evolutionEntry.setEvolutionRequirementBlock(evolutionEntry.getEvolutionRequirementBlock().toBuilder().vitalRequirements(val).build());
        });
        textField.setMin(0);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getTrophiesRequirementLabel(EvolutionEntry evolutionEntry) {
        Label label = new Label("Trophies Requirement:");
        IntegerTextField textField = new IntegerTextField(evolutionEntry.getEvolutionRequirementBlock().getTrophyRequirement(), val -> {
            evolutionEntry.setEvolutionRequirementBlock(evolutionEntry.getEvolutionRequirementBlock().toBuilder().trophyRequirement(val).build());
        });
        textField.setMin(0);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getBattlesRequirementLabel(EvolutionEntry evolutionEntry) {
        Label label = new Label("Battles Requirement:");
        IntegerTextField textField = new IntegerTextField(evolutionEntry.getEvolutionRequirementBlock().getBattleRequirement(), val -> {
            evolutionEntry.setEvolutionRequirementBlock(evolutionEntry.getEvolutionRequirementBlock().toBuilder().battleRequirement(val).build());
        });
        textField.setMin(0);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getWinRatioRequirementLabel(EvolutionEntry evolutionEntry) {
        Label label = new Label("Win Ratio Requirement:");
        IntegerTextField textField = new IntegerTextField(evolutionEntry.getEvolutionRequirementBlock().getWinRatioRequirement(), val -> {
            evolutionEntry.setEvolutionRequirementBlock(evolutionEntry.getEvolutionRequirementBlock().toBuilder().winRatioRequirement(val).build());
        });
        textField.setMin(0);
        textField.setMax(100);
        VBox vBox = new VBox(label, textField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getEvolveToSlotLabel(MonsterSlot fromMonster, EvolutionEntry evolutionEntry) {
        String noneOptionDisplay = fromMonster.getEvolutionEntries().size() == 1 && fromMonster.getHoursUntilEvolution() != LoadedScene.NONE_VALUE ? "Fusion" : LoadedScene.NONE_LABEL;
        Label label = new Label("Evolve To Slot:");
        SlotComboBox slotComboBox = new SlotComboBox(dimData, evolutionEntry.getToMonster(), noneOptionDisplay, sceneRefresher, id -> evolutionEntry.setToMonster(id));
        VBox vBox = new VBox(label, slotComboBox);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
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

    private Node getDeleteEntry(MonsterSlot monsterSlot, int index) {
        Button button = new Button("Delete Entry");
        button.setOnAction(e -> {
            monsterSlot.getEvolutionEntries().remove(index);
            sceneRefresher.run();
        });
        GridPane.setMargin(button, new Insets(10));
        return button;
    }

    private Node getAddEntry(MonsterSlot monsterSlot) {
        Button button = new Button("Add Entry");
        button.setOnAction(e -> {
            monsterSlot.getEvolutionEntries().add(EvolutionEntry.builder().evolutionRequirementBlock(DimEvolutionRequirements.DimEvolutionRequirementBlock.builder().build()).toMonster(null).build());
            sceneRefresher.run();
        });
        GridPane.setMargin(button, new Insets(10));
        return button;
    }
}
