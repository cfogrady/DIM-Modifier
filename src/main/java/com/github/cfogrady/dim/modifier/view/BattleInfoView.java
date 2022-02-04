package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.AdventureEntry;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BattleInfoView implements InfoView {
    private final DimData dimData;
    private final SpriteImageTranslator spriteImageTranslator;

    // May not work with more or less than 15 entries

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane adventuresGridPane = new GridPane();
        adventuresGridPane.setGridLinesVisible(true);
        for(int i = 0; i < dimData.getAdventureEntries().size(); i++) {
            addRow(adventuresGridPane, i);
        }
        GridPane randomBattlesGridPane = addRandomBattlesPane();
        VBox vBox = new VBox(new Label("Adventures:"), adventuresGridPane, new Label("Random Battles:"), randomBattlesGridPane);
        vBox.setSpacing(10);
        ScrollPane scrollPane = new ScrollPane(vBox);
        return scrollPane;
    }

    @Override
    public double getPrefWidth() {
        return 700;
    }

    private void addRow(GridPane gridPane, int rowIndex) {
        AdventureEntry adventureEntry = dimData.getAdventureEntries().get(rowIndex);
        gridPane.add(getStepsLabel(adventureEntry), 0, rowIndex);
        gridPane.add(getSpriteForSlot(adventureEntry), 1, rowIndex);
        gridPane.add(getBossSlotLabel(adventureEntry), 2, rowIndex);
        gridPane.add(getBossDpLebel(adventureEntry), 3, rowIndex);
        gridPane.add(getBossHpLabel(adventureEntry), 4, rowIndex);
        gridPane.add(getBossApLabel(adventureEntry), 5, rowIndex);
    }

    private Node getStepsLabel(AdventureEntry adventureEntry) {
        Label label = new Label("Steps: " + adventureEntry.getSteps());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpriteForSlot(AdventureEntry adventureEntry) {
        if(adventureEntry.getMonsterId() == null) {
            return new Pane();
        }
        SpriteData.Sprite monsterSprite = dimData.getMosnterSprite(adventureEntry.getMonsterId(), 1);
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(monsterSprite));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getBossSlotLabel(AdventureEntry adventureEntry) {
        String monsterSlotLabel;
        if(adventureEntry.getMonsterId() == null) {
            monsterSlotLabel = LoadedScene.NONE_LABEL;
        } else {
            monsterSlotLabel = Integer.toString(dimData.getMonsterSlotIndexForId(adventureEntry.getMonsterId()));
        }
        Label label = new Label("Boss Slot: " + monsterSlotLabel);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossDpLebel(AdventureEntry adventureEntry) {
        Label label = new Label("Boss Dp: " + adventureEntry.getBossDp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossHpLabel(AdventureEntry adventureEntry) {
        Label label = new Label("Boss Hp: " + adventureEntry.getBossHp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossApLabel(AdventureEntry adventureEntry) {
        Label label = new Label("Boss Ap: " + adventureEntry.getBossAp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private GridPane addRandomBattlesPane() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        int rowIndex = 0;
        for(MonsterSlot slot : dimData.getMonsterSlotList()) {
            if(slot.getStatBlock().getStage() > 1) {
                addRandomBattleRow(gridPane, rowIndex, slot);
                rowIndex++;
            }
        }
        return gridPane;
    }

    private void addRandomBattleRow(GridPane gridPane, int rowIndex, MonsterSlot slot) {
        gridPane.add(getRandomBattleSprite(slot), 0, rowIndex);
        gridPane.add(getFirstBattleChanceLabel(slot), 1, rowIndex);
        gridPane.add(getSecondBattleChanceLabel(slot), 2, rowIndex);
    }

    private Node getRandomBattleSprite(MonsterSlot slot) {
        SpriteData.Sprite monsterSprite = slot.getSprites().get(1);
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(monsterSprite));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getFirstBattleChanceLabel(MonsterSlot slot) {
        Label label = new Label("Level III/IV Battle Chance: " + NoneUtils.defaultIfNone(slot.getStatBlock().getFirstPoolBattleChance(), LoadedScene.NONE_LABEL));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSecondBattleChanceLabel(MonsterSlot slot) {
        Label label = new Label("Level V/VI Battle Chance: " + NoneUtils.defaultIfNone(slot.getStatBlock().getSecondPoolBattleChance(), LoadedScene.NONE_LABEL));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }
}
