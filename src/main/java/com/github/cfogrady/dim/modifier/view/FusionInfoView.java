package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.CurrentSelectionType;
import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.SelectionState;
import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.Fusions;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.dim.modifier.data.SpecificFusion;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class FusionInfoView implements InfoView {
    private final DimData dimData;
    private final SpriteImageTranslator spriteImageTranslator;
    private final Consumer<SelectionState> stateChanger;

    @Override
    public Node setupView(SelectionState selectionState) {
        VBox vBox = new VBox();
        if(selectionState.getSelectionType() == CurrentSelectionType.SLOT) {
            MonsterSlot monsterSlot = dimData.getMonsterSlotList().get(selectionState.getSlot());
            if(monsterSlot.getFusions() == null) {
                vBox.getChildren().add(createAddFusionButton(selectionState, monsterSlot));
            } else {
                vBox.getChildren().add(new Label("Fusions:"));
                GridPane gridPane = new GridPane();
                gridPane.setGridLinesVisible(true);
                addRow(gridPane, 0, selectionState, monsterSlot);
                vBox.getChildren().add(gridPane);
            }
        }
        if(dimData.getSpecificFusions().size() > 0) {
            GridPane specificFusionGridPane = new GridPane();
            specificFusionGridPane.setGridLinesVisible(true);
            int rowIndex = 0;
            for(SpecificFusion specificFusion : dimData.getSpecificFusions()) {
                addSpecificFusionRow(specificFusionGridPane, rowIndex, specificFusion);
                rowIndex++;
            }
            vBox.getChildren().add(new Label("Specific Fusions:"));
            vBox.getChildren().add(specificFusionGridPane);
        }
        vBox.setSpacing(10);
        return new ScrollPane(vBox);
    }

    @Override
    public double getPrefWidth() {
        return 1300;
    }

    private void addRow(GridPane gridPane, int rowIndex, SelectionState selectionState, MonsterSlot monsterSlot) {
        Fusions fusionEntry = monsterSlot.getFusions();
        gridPane.add(getType1FusionResultLabel(fusionEntry), 0, rowIndex);
        gridPane.add(getType1FusionResultSprite(fusionEntry), 1, rowIndex);
        gridPane.add(getType2FusionResultLabel(fusionEntry), 2, rowIndex);
        gridPane.add(getType2FusionResultSprite(fusionEntry), 3, rowIndex);
        gridPane.add(getType3FusionResultLabel(fusionEntry), 4, rowIndex);
        gridPane.add(getType3FusionResultSprite(fusionEntry), 5, rowIndex);
        gridPane.add(getType4FusionResultLabel(fusionEntry), 6, rowIndex);
        gridPane.add(getType4FusionResultSprite(fusionEntry), 7, rowIndex);
        gridPane.add(createDeleteFusionButton(selectionState, monsterSlot), 8, rowIndex);
    }

    private void addSpecificFusionRow(GridPane gridPane, int rowIndex, SpecificFusion specificFusion) {
        gridPane.add(getSpecificFusionSlotLabel(specificFusion), 0, rowIndex);
        gridPane.add(getSpecificFusionSlotSprite(specificFusion), 1, rowIndex);
        gridPane.add(getSpecificFusionPartnerDimLabel(specificFusion), 2, rowIndex);
        gridPane.add(getSpecificFusionPartnerSlotLabel(specificFusion), 3, rowIndex);
        gridPane.add(getSpecificFusionResultLabel(specificFusion), 4, rowIndex);
        gridPane.add(getSpecificFusionResultSprite(specificFusion), 5, rowIndex);
    }

    private Node createAddFusionButton(SelectionState selectionState, MonsterSlot monsterSlot) {
        Button button = new Button("Add Fusions");
        button.setOnAction(e -> {
            monsterSlot.setFusions(Fusions.builder().build());
            stateChanger.accept(selectionState);
        });
        return button;
    }

    private Node createDeleteFusionButton(SelectionState selectionState, MonsterSlot monsterSlot) {
        Button button = new Button("Delete Fusions");
        button.setOnAction(e -> {
            monsterSlot.setFusions(null);
            stateChanger.accept(selectionState);
        });
        GridPane.setMargin(button, new Insets(10));
        return button;
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
