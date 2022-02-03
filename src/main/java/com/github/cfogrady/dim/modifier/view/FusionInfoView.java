package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.Fusions;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.dim.modifier.data.SpecificFusion;
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
public class FusionInfoView implements InfoView {
    private final DimData dimData;
    private final SpriteImageTranslator spriteImageTranslator;

    @Override
    public Node setupView(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        int monsterIndex = 0;
        for(MonsterSlot monsterSlot : dimData.getMonsterSlotList()) {
            if(monsterSlot.getFusions() != null) {
                addRow(gridPane, monsterIndex, monsterSlot);
                monsterIndex++;
            }
        }
        VBox vBox = new VBox(gridPane);
        if(dimData.getSpecificFusions().size() > 0) {
            GridPane specificFusionGridPane = new GridPane();
            specificFusionGridPane.setGridLinesVisible(true);
            int rowIndex = 0;
            for(SpecificFusion specificFusion : dimData.getSpecificFusions()) {
                addSpecificFusionRow(specificFusionGridPane, rowIndex, specificFusion);
                rowIndex++;
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

    private void addRow(GridPane gridPane, int rowIndex, MonsterSlot monsterSlot) {
        Fusions fusionEntry = monsterSlot.getFusions();
        gridPane.add(getSlotLabel(monsterSlot), 0, rowIndex);
        gridPane.add(getFromEvolutionSprite(monsterSlot), 1, rowIndex);
        gridPane.add(getType1FusionResultLabel(fusionEntry), 2, rowIndex);
        gridPane.add(getType1FusionResultSprite(fusionEntry), 3, rowIndex);
        gridPane.add(getType2FusionResultLabel(fusionEntry), 4, rowIndex);
        gridPane.add(getType2FusionResultSprite(fusionEntry), 5, rowIndex);
        gridPane.add(getType3FusionResultLabel(fusionEntry), 6, rowIndex);
        gridPane.add(getType3FusionResultSprite(fusionEntry), 7, rowIndex);
        gridPane.add(getType4FusionResultLabel(fusionEntry), 8, rowIndex);
        gridPane.add(getType4FusionResultSprite(fusionEntry), 9, rowIndex);
    }

    private void addSpecificFusionRow(GridPane gridPane, int rowIndex, SpecificFusion specificFusion) {
        gridPane.add(getSpecificFusionSlotLabel(specificFusion), 0, rowIndex);
        gridPane.add(getSpecificFusionSlotSprite(specificFusion), 1, rowIndex);
        gridPane.add(getSpecificFusionPartnerDimLabel(specificFusion), 2, rowIndex);
        gridPane.add(getSpecificFusionPartnerSlotLabel(specificFusion), 3, rowIndex);
        gridPane.add(getSpecificFusionResultLabel(specificFusion), 4, rowIndex);
        gridPane.add(getSpecificFusionResultSprite(specificFusion), 5, rowIndex);
    }

    private Node getSlotLabel(MonsterSlot monsterSlot) {
        Label label = new Label("Evolve From Slot: " + dimData.getMonsterSlotIndexForId(monsterSlot.getId()));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getFromEvolutionSprite(MonsterSlot monsterSlot) {
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(monsterSlot.getSprites().get(1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType1FusionResultLabel(Fusions fusionEntry) {
        String labelText;
        if(fusionEntry.getType1FusionResult() == null) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(dimData.getMonsterSlotIndexForId(fusionEntry.getType1FusionResult()));
        }
        Label label = new Label("Type 1 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType1FusionResultSprite(Fusions fusionEntry) {
        if(fusionEntry.getType1FusionResult() == null) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(dimData.getMosnterSprite(fusionEntry.getType1FusionResult(), 1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType2FusionResultLabel(Fusions fusionEntry) {
        String labelText;
        if(fusionEntry.getType2FusionResult() == null) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(dimData.getMonsterSlotIndexForId(fusionEntry.getType2FusionResult()));
        }
        Label label = new Label("Type 2 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType2FusionResultSprite(Fusions fusionEntry) {
        if(fusionEntry.getType2FusionResult() == null) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(dimData.getMosnterSprite(fusionEntry.getType2FusionResult(), 1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType3FusionResultLabel(Fusions fusionEntry) {
        String labelText;
        if(fusionEntry.getType3FusionResult() == null) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(dimData.getMonsterSlotIndexForId(fusionEntry.getType3FusionResult()));
        }
        Label label = new Label("Type 3 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType3FusionResultSprite(Fusions fusionEntry) {
        if(fusionEntry.getType3FusionResult() == null) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(dimData.getMosnterSprite(fusionEntry.getType3FusionResult(), 1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getType4FusionResultLabel(Fusions fusionEntry) {
        String labelText;
        if(fusionEntry.getType4FusionResult() == null) {
            labelText = LoadedScene.NONE_LABEL;
        } else {
            labelText = Integer.toString(dimData.getMonsterSlotIndexForId(fusionEntry.getType4FusionResult()));
        }
        Label label = new Label("Type 4 Fusion Result: " + labelText);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getType4FusionResultSprite(Fusions fusionEntry) {
        if(fusionEntry.getType4FusionResult() == null) {
            return new Pane();
        }
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(dimData.getMosnterSprite(fusionEntry.getType4FusionResult(), 1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getSpecificFusionSlotLabel(SpecificFusion specificFusionEntry) {
        Label label = new Label("Evolve From Slot: " + dimData.getMonsterSlotIndexForId(specificFusionEntry.getLocalMonsterId()));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionSlotSprite(SpecificFusion specificFusionEntry) {
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(dimData.getMosnterSprite(specificFusionEntry.getLocalMonsterId(), 1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }

    private Node getSpecificFusionPartnerDimLabel(SpecificFusion specificFusionEntry) {
        Label label = new Label("Fusion Partner DIM Id: " + specificFusionEntry.getPartnerDimId());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionPartnerSlotLabel(SpecificFusion specificFusionEntry) {
        Label label = new Label("Fusion Partner Slot Id: " + specificFusionEntry.getPartnerDimSlotId());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionResultLabel(SpecificFusion specificFusionEntry) {
        Label label = new Label("Fusion Result Slot Id: " + dimData.getMonsterSlotIndexForId(specificFusionEntry.getEvolveToMonsterId()));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node getSpecificFusionResultSprite(SpecificFusion specificFusionEntry) {
        ImageView imageView = new ImageView(spriteImageTranslator.loadImageFromSprite(dimData.getMosnterSprite(specificFusionEntry.getEvolveToMonsterId(), 1)));
        GridPane.setMargin(imageView, new Insets(10));
        return imageView;
    }
}
