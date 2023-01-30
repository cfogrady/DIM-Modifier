package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.SpriteReplacer;
import com.github.cfogrady.dim.modifier.controls.ImageIntComboBoxFactory;
import com.github.cfogrady.dim.modifier.data.AppState;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class BemStatsViewFactory {
    private final AppState appState;
    private final SpriteImageTranslator spriteImageTranslator;
    private final SpriteReplacer spriteReplacer;
    private final ImageIntComboBoxFactory imageIntComboBoxFactory;

    public BemStatsView buildBemStatsView() {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        StackPane spriteWindow = buildSpriteWindow();
        StatsGrid statsGrid = buildStatsGrid();
        Button prevButton = buildPrevButton();
        Button nextButton = buildNextButton();
        hbox.getChildren().add(setupSpriteAreaLayout(spriteWindow, prevButton, nextButton));
        hbox.getChildren().add(statsGrid.getGridPane());
        return new BemStatsView(appState, spriteImageTranslator, spriteReplacer, hbox, spriteWindow, prevButton, nextButton, statsGrid);
    }

    private StatsGrid buildStatsGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        return new StatsGrid(gridPane, appState, imageIntComboBoxFactory, appState::isSafetyModeOn);
    }

    private Node setupSpriteAreaLayout(StackPane spriteWindow, Button prevButton, Button nextButton) {
        VBox vbox = new VBox(spriteWindow);
        HBox hBox = new HBox(prevButton, nextButton);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER); //children take up as much space as they can, so we need to either align here, or control width of the parent.
        vbox.getChildren().add(hBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        return vbox;
    }

    private StackPane buildSpriteWindow() {
        StackPane spriteWindow = new StackPane();
        spriteWindow.setAlignment(Pos.BOTTOM_CENTER);
        spriteWindow.setMinSize(160.0, 320.0);
        spriteWindow.setMaxSize(160.0, 320.0);
        spriteWindow.setOnDragOver(e -> {
            if (e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.ANY);
                log.info("Drag Over Image");
                e.consume();
            } else if(e.getDragboard().hasFiles()) {
                if (e.getDragboard().getFiles().size() > 1) {
                    log.info("Can only load 1 file at a time");
                } else {
                    e.acceptTransferModes(TransferMode.ANY);
                    e.consume();
                }
            }
        });
        return spriteWindow;
    }

    private Button buildPrevButton() {
        Button button = new Button();
        button.setText("Prev Sprite");
        return button;
    }

    private Button buildNextButton() {
        Button button = new Button();
        button.setText("Next Sprite");
        return  button;
    }
}
