package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.CurrentSelectionType;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteSlotParser;
import com.github.cfogrady.vb.dim.reader.content.DimAdventures;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AdventureInfoView implements InfoView {
    private final List<DimAdventures.DimAdventureBlock> dimAdventureBlocks;
    private final SpriteSlotParser spriteSlotParser;

    // May not work with more or less than 15 entries

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        for(int i = 0; i < dimAdventureBlocks.size(); i++) {
            addRow(gridPane, i);
        }
        ScrollPane scrollPane = new ScrollPane(gridPane);
        return scrollPane;
    }

    @Override
    public double getPrefWidth() {
        return 600;
    }

    private void addRow(GridPane gridPane, int rowIndex) {
        DimAdventures.DimAdventureBlock adventureEntry = dimAdventureBlocks.get(rowIndex);
        gridPane.add(getStepsLabel(adventureEntry), 0, rowIndex);
        gridPane.add(getSpriteForSlot(adventureEntry), 1, rowIndex);
        gridPane.add(getBossSlotLabel(adventureEntry), 2, rowIndex);
        gridPane.add(getBossDpLebel(adventureEntry), 3, rowIndex);
        gridPane.add(getBossHpLabel(adventureEntry), 4, rowIndex);
        gridPane.add(getBossApLabel(adventureEntry), 5, rowIndex);
    }

    private Node getStepsLabel(DimAdventures.DimAdventureBlock adventureEntry) {
        Label label = new Label("Steps: " + adventureEntry.getSteps());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpriteForSlot(DimAdventures.DimAdventureBlock adventureEntry) {
        ImageView imageView = new ImageView(spriteSlotParser.getImageForSlotAndIndex(CurrentSelectionType.SLOT, adventureEntry.getBossStatsIndex(), 1));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getBossSlotLabel(DimAdventures.DimAdventureBlock adventureEntry) {
        Label label = new Label("Boss Slot: " + adventureEntry.getBossStatsIndex());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossDpLebel(DimAdventures.DimAdventureBlock adventureEntry) {
        Label label = new Label("Boss Dp: " + adventureEntry.getBossDp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossHpLabel(DimAdventures.DimAdventureBlock adventureEntry) {
        Label label = new Label("Boss Hp: " + adventureEntry.getBossHp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossApLabel(DimAdventures.DimAdventureBlock adventureEntry) {
        Label label = new Label("Boss Ap: " + adventureEntry.getBossAp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }
}
