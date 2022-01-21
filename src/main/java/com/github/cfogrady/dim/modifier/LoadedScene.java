package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.vb.dim.reader.DimReader;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.content.DimStats;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Slf4j
public class LoadedScene {
    public static final int BACKGROUND_INDEX = 1;

    private final DimContent dimContent;
    private final Stage stage;
    private final BackgroundImage background;

    public LoadedScene(DimContent dimContent, Stage stage) {
        this.dimContent = dimContent;
        this.stage = stage;
        BackgroundSize size = new BackgroundSize(100, 100, true, true, true, true);
        this.background = new BackgroundImage(loadImageAtLocation(BACKGROUND_INDEX), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
    }

    private int currentIndex;

    private int getImageIndex(CurrentSelectionType selectionType, int slot, int image) {
        if(selectionType == CurrentSelectionType.LOGO) {
            return 0;
        } else if(selectionType == CurrentSelectionType.EGG) {
            return image + 2; // logo + background
        } else {
            int index = 2 + 8; //logo+background + egg sprites
            for(int monSlot = 0; monSlot < slot; monSlot++) {
                int level = dimContent.getDimStats().getStatBlocks().get(monSlot).getStage();
                if(level == 0) {
                    index += 6;
                } else if(level == 1) {
                    index += 7;
                } else {
                    index += 14;
                }
            }
            return index + image;
        }
    }

    private int getSpriteCountForSelection(SelectionState selectionState) {
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            return 1;
        } else if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
            return 8;
        } else {
            int level = getSelectionLevel(selectionState);
            if(level == 0) {
                return 6;
            } else if (level == 1) {
                return 7;
            } else {
                return 14;
            }
        }
    }

    private int getSelectionLevel(SelectionState selectionState) {
        return dimContent.getDimStats().getStatBlocks().get(selectionState.getSlot()).getStage();
    }

    public void setupScene(SelectionState selectionState) {
        VBox vbox = new VBox();
        vbox.getChildren().add(setupHeaderButtons(selectionState));
        vbox.getChildren().add(setupNameArea(selectionState));
        HBox hbox = new HBox();
        vbox.getChildren().add(hbox);
        hbox.getChildren().add(setupSpriteArea(selectionState));
        hbox.getChildren().add(setupStatArea(selectionState));
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        javafx.scene.Scene scene = new Scene(vbox, 1280, 720);
        log.info("Setting scene");
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            log.info("Key pressed: {}", key.getCode().getName());
            if(key.getCode() == KeyCode.A) {
                if(selectionState.getSpriteIndex() > 0) {
                    setupScene(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() - 1).build());
                }
            } else if (key.getCode() == KeyCode.D) {
                if(selectionState.getSpriteIndex() < getSpriteCountForSelection(selectionState) - 1) {
                    setupScene(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() + 1).build());
                }
            } else if(key.getCode() == KeyCode.B) {
                changeBackground(selectionState);
            }
        });
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            log.info("Event: {}", event);
        });
        stage.setScene(scene);
        stage.show();
    }

    private Node setupHeaderButtons(SelectionState selectionState) {
        HBox hBox = new HBox(setupOpenButton(), setupSaveButton(), setupFusionButton(), setupFusionButton());
        hBox.setSpacing(10);
        return hBox;
    }

    private Node setupNameArea(SelectionState selectionState) {
        HBox hBox = new HBox(setupPrevButton(selectionState), setupName(selectionState.getSelectionType(), selectionState.getSlot()), setupNextButton(selectionState));
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private Node setupSpriteArea(SelectionState selectionState) {
        int spriteIndex = getImageIndex(selectionState.getSelectionType(), selectionState.getSlot(), selectionState.getSpriteIndex());
        SpriteData.Sprite sprite = dimContent.getSpriteData().getSprites().get(spriteIndex);
        Image image = loadImageAtLocation(sprite);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(sprite.getWidth() * 2.0);
        imageView.setFitHeight(sprite.getHeight() * 2.0);
        //imageView.setLayoutX(0);
        //imageView.setLayoutY(0);
        StackPane backgroundPane = new StackPane(imageView);
        backgroundPane.setAlignment(Pos.BOTTOM_CENTER);
        backgroundPane.setBackground(getBackground(selectionState)); //160x320
        backgroundPane.setMinSize(160.0, 320.0);
        backgroundPane.setMaxSize(160.0, 320.0);
        VBox vbox = new VBox(getChangeBackgroundButton(), backgroundPane);
        if(selectionState.getSelectionType() != CurrentSelectionType.LOGO) {
            HBox hBox = new HBox(setupPrevSpriteButton(selectionState), setupReplaceSpriteButton(selectionState), setupNextSpriteButton(selectionState));
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER); //children take up as much space as they can, so we need to either align here, or control width of the parent.
            vbox.getChildren().add(hBox);
        }
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        return vbox;
    }

    private Node setupStatArea(SelectionState selectionState) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        if(selectionState.getSelectionType() != CurrentSelectionType.SLOT) {
            return gridPane;
        }
        int slot = selectionState.getSlot();
        DimStats.DimStatBlock statBlock = dimContent.getDimStats().getStatBlocks().get(slot);
        gridPane.add(setupStageLabel(statBlock), 0, 0);
        gridPane.add(setupLockedLabel(statBlock), 1, 0);
        gridPane.add(setupAttributeLabel(statBlock), 0, 1);
        gridPane.add(setupDispositionLabel(statBlock), 1, 1);
        gridPane.add(setupSmallAttackLabel(statBlock), 0, 2);
        gridPane.add(setupBigAttackLabel(statBlock), 1, 2);
        gridPane.add(setupDPStarsLabel(statBlock), 0, 3);
        gridPane.add(setupDPLabel(statBlock), 1, 3);
        gridPane.add(setupHpLabel(statBlock), 0, 4);
        gridPane.add(setupApLabel(statBlock), 1, 4);
        gridPane.add(setupEarlyBattleChanceLabel(statBlock), 0, 5);
        gridPane.add(setupLateBattleChanceLabel(statBlock), 1, 5);
        return gridPane;
    }

    private Node setupStageLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Stage: " + (statBlock.getStage() + 1));
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupLockedLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Requires Unlock: " + statBlock.isUnlockRequired());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupAttributeLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Attribute: " + statBlock.getAttribute());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupDispositionLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Disposition: " + statBlock.getDisposition());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupSmallAttackLabel(DimStats.DimStatBlock statBlock) {
        String attackLabel;
        if(statBlock.getStage() < 2) {
            attackLabel = "NONE";
        } else {
            attackLabel = AttackLabels.SMALL_ATTACKS[statBlock.getSmallAttackId()];
        }
        Label label = new Label("Small Attack: " + attackLabel);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupBigAttackLabel(DimStats.DimStatBlock statBlock) {
        String attackLabel;
        if(statBlock.getStage() < 2) {
            attackLabel = "NONE";
        } else {
            attackLabel = AttackLabels.BIG_ATTACKS[statBlock.getBigAttackId()];
        }
        Label label = new Label("Big Attack: " + attackLabel);
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupDPStarsLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("DP (stars): " + statBlock.getDpStars());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupDPLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("DP: " + statBlock.getDp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupHpLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("HP: " + statBlock.getHp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupApLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("AP: " + statBlock.getAp());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupEarlyBattleChanceLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Stage 3/4 Battle Chance: " + statBlock.getFirstPoolBattleChance());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupLateBattleChanceLabel(DimStats.DimStatBlock statBlock) {
        Label label = new Label("Stage 5/6 Battle Chance: " + statBlock.getSecondPoolBattleChance());
        GridPane.setMargin(label, new Insets(10));
        return label;
    }

    private Node setupPrevSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Prev Sprite");
        if(selectionState.getSpriteIndex() == 0 || (selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSpriteIndex() < 2)) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            setupScene(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() - 1).build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupNextSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Next Sprite");
        if(selectionState.getSpriteIndex() == getSpriteCountForSelection(selectionState)-1) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            setupScene(selectionState.toBuilder().spriteIndex(selectionState.getSpriteIndex() + 1).build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupReplaceSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Replace Sprite");
        button.setDisable(true);
        // TODO: Setup Fusion section
        return button;
    }

    private void changeBackground(SelectionState selectionState) {
        SelectionState.SelectionStateBuilder builder = selectionState.toBuilder();
        builder.backgroundType(BackgroundType.nextBackground(selectionState.getBackgroundType()));
        setupScene(builder.build());
    }

    private Node getChangeBackgroundButton() {
        Button button = new Button();
        button.setText("Change Background Image");
        button.setDisable(true);
        // TODO: Setup Fusion section
        return button;
    }

    private Background getBackground(SelectionState selectionState) {
        if(selectionState.getBackgroundType() == BackgroundType.IMAGE) {
            return new Background(this.background);
        } else if(selectionState.getBackgroundType() == BackgroundType.BLUE) {
            return new Background(new BackgroundFill(Color.color(0, 0, 1), CornerRadii.EMPTY, Insets.EMPTY));
        } else if (selectionState.getBackgroundType() == BackgroundType.GREEN) {
            return new Background(new BackgroundFill(Color.color(0, 1, 0), CornerRadii.EMPTY, Insets.EMPTY));
        } else if (selectionState.getBackgroundType() == BackgroundType.ORANGE) {
            return new Background(new BackgroundFill(Color.color(1, 165.0D/255.0D, 0), CornerRadii.EMPTY, Insets.EMPTY));
        } else {
            throw new UnsupportedOperationException("Unhandled background type!");
        }
    }

    private Node setupName(CurrentSelectionType selectionType, int slot) {
        if(selectionType == CurrentSelectionType.LOGO) {
            return new Label("LOGO");
        } else if (selectionType == CurrentSelectionType.EGG) {
            return new Label("EGG");
        } else {
            Image image = loadImageAtLocation(getImageIndex(selectionType, slot, 0));
            ImageView imageView = new ImageView(image);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouse -> {
                log.info("Mouse clicked for name image");
            });
            StackPane stackPane = new StackPane(imageView);
            stackPane.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
            return stackPane;
        }
    }

    private Button setupFusionButton() {
        Button button = new Button();
        button.setText("Fusion");
        button.setDisable(true);
        // TODO: Setup Fusion section
        return button;
    }

    private Node setupPrevButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Prev");
        if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            SelectionState.SelectionStateBuilder newStateBuilder = selectionState.toBuilder();
            if(selectionState.getSelectionType() == CurrentSelectionType.EGG) {
                newStateBuilder.selectionType(CurrentSelectionType.LOGO);
            } else if (selectionState.getSlot() == 0) {
                newStateBuilder.selectionType(CurrentSelectionType.EGG).spriteIndex(0);
            } else {
                newStateBuilder.slot(selectionState.getSlot() - 1).spriteIndex(1);
            }
            setupScene(newStateBuilder.build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Node setupNextButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Next");
        if(selectionState.getSelectionType() == CurrentSelectionType.SLOT && selectionState.getSlot() == dimContent.getDimStats().getStatBlocks().size() - 1) {
            button.setDisable(true);
        }
        button.setOnAction(event -> {
            SelectionState.SelectionStateBuilder newStateBuilder = selectionState.toBuilder();
            if(selectionState.getSelectionType() == CurrentSelectionType.LOGO) {
                newStateBuilder.selectionType(CurrentSelectionType.EGG);
            } else if (selectionState.getSelectionType() == CurrentSelectionType.EGG) {
                newStateBuilder.selectionType(CurrentSelectionType.SLOT).spriteIndex(1);
            } else {
                newStateBuilder.slot(selectionState.getSlot() + 1).spriteIndex(1);
            }
            setupScene(newStateBuilder.build());
        });
        StackPane pane = new StackPane(button);
        return pane;
    }

    private Button setupOpenButton() {
        Button button = new Button();
        button.setText("Open");
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select DIM File");
            File file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                InputStream fileInputStream = null;
                try {
                    DimReader reader = new DimReader();
                    fileInputStream = new FileInputStream(file);
                    DimContent content = reader.readDimData(fileInputStream, false);
                    LoadedScene scene = new LoadedScene(content, stage);
                    scene.setupScene(SelectionState.builder()
                            .selectionType(CurrentSelectionType.LOGO)
                            .slot(0)
                            .spriteIndex(0)
                            .backgroundType(BackgroundType.IMAGE)
                            .build());
                } catch (FileNotFoundException e) {
                    log.error("Couldn't find selected file.", e);
                }

            }
        });
        return button;
    }

    private Button setupSaveButton() {
        Button button = new Button();
        button.setText("Save");
        button.setDisable(true);
        // TODO: Setup DIM Writing
        return button;
    }

    private void setupGridConstraints(GridPane gridPane) {
        gridPane.getColumnConstraints().add(new ColumnConstraints(40));
        gridPane.getColumnConstraints().add(new ColumnConstraints(80));
        gridPane.getColumnConstraints().add(new ColumnConstraints(40));
        gridPane.getColumnConstraints().add(new ColumnConstraints(80));
        gridPane.getColumnConstraints().add(new ColumnConstraints(80));
        gridPane.getRowConstraints().add(new RowConstraints(40));
        gridPane.getRowConstraints().add(new RowConstraints(40));
        gridPane.getRowConstraints().add(new RowConstraints(40));
        gridPane.getRowConstraints().add(new RowConstraints(54));
        gridPane.getRowConstraints().add(new RowConstraints(54));
        gridPane.getRowConstraints().add(new RowConstraints(54));
        gridPane.getRowConstraints().add(new RowConstraints(54));
        gridPane.getRowConstraints().add(new RowConstraints(54));
        gridPane.getRowConstraints().add(new RowConstraints(54));
        gridPane.getRowConstraints().add(new RowConstraints(40));
        //gridPane.setPrefSize(80*3, 160*3);
    }

    private WritableImage loadImageAtLocation(int index) {
        SpriteData.Sprite sprite = dimContent.getSpriteData().getSprites().get(index);
        return loadImageAtLocation(sprite);
    }

    private WritableImage loadImageAtLocation(SpriteData.Sprite sprite) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(sprite.getBGRA());
        PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<ByteBuffer>(sprite.getWidth(), sprite.getHeight(), byteBuffer, PixelFormat.getByteBgraPreInstance());

        return new WritableImage(pixelBuffer);
    }
}
