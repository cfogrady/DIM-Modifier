package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.controls.IntegerTextField;
import com.github.cfogrady.dim.modifier.controls.SlotComboBox;
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
    private final Runnable sceneRefresher;

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
        IntegerTextField integerTextField = new IntegerTextField(adventureEntry.getSteps(), val -> adventureEntry.setSteps(val));
        integerTextField.setMin(0);
        VBox vBox = new VBox(label, integerTextField);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
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
        Label label = new Label("Boss Slot:");
        SlotComboBox slotComboBox = new SlotComboBox(dimData, adventureEntry.getMonsterId(), true, LoadedScene.NONE_LABEL, 2, sceneRefresher, id -> {
            adventureEntry.setMonsterId(id);
            int dp;
            int hp;
            int ap;
            if(id != null) {
                MonsterSlot monsterSlot = dimData.getMonsterSlotForId(id);
                dp = monsterSlot.getStatBlock().getDp();
                hp = monsterSlot.getStatBlock().getHp();
                ap = monsterSlot.getStatBlock().getAp();
            } else {
                dp = LoadedScene.NONE_VALUE;
                ap = LoadedScene.NONE_VALUE;
                hp = LoadedScene.NONE_VALUE;
            }
            adventureEntry.setBossHp(hp);
            adventureEntry.setBossAp(ap);
            adventureEntry.setBossDp(dp);
        });
        VBox vBox = new VBox(label, slotComboBox);
        vBox.setSpacing(10);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getBossDpLebel(AdventureEntry adventureEntry) {
        Label label = new Label("Boss Dp: " + NoneUtils.defaultIfNone(adventureEntry.getBossDp(), LoadedScene.NONE_LABEL));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossHpLabel(AdventureEntry adventureEntry) {
        Label label = new Label("Boss Hp: " + NoneUtils.defaultIfNone(adventureEntry.getBossHp(), LoadedScene.NONE_LABEL));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getBossApLabel(AdventureEntry adventureEntry) {
        Label label = new Label("Boss Ap: " + NoneUtils.defaultIfNone(adventureEntry.getBossAp(), LoadedScene.NONE_LABEL));
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
        Label label = new Label("Level III/IV Battle Chance:");
        IntegerTextField integerTextField = new IntegerTextField(slot.getStatBlock().getFirstPoolBattleChance(), val -> {
            slot.setStatBlock(slot.getStatBlock().toBuilder().firstPoolBattleChance(val).build());
        });
        VBox vBox = new VBox(label, integerTextField);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }

    private Node getSecondBattleChanceLabel(MonsterSlot slot) {
        Label label = new Label("Level V/VI Battle Chance: " + NoneUtils.defaultIfNone(slot.getStatBlock().getSecondPoolBattleChance(), LoadedScene.NONE_LABEL));
        IntegerTextField integerTextField = new IntegerTextField(slot.getStatBlock().getSecondPoolBattleChance(), val -> {
            slot.setStatBlock(slot.getStatBlock().toBuilder().secondPoolBattleChance(val).build());
        });
        VBox vBox = new VBox(label, integerTextField);
        GridPane.setMargin(vBox, new Insets(10));
        return vBox;
    }
}
