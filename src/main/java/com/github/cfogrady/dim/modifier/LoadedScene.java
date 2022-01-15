package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.vb.dim.reader.DimReader;
import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.nio.ByteBuffer;

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
        GridPane gridPane = new GridPane();
        setupGridConstraints(gridPane);
        setupHeaderButtons(gridPane, selectionState);
        setupNameArea(gridPane, selectionState);
        setupSpriteArea(gridPane, selectionState);
        gridPane.setAlignment(Pos.CENTER);
        javafx.scene.Scene scene = new Scene(gridPane, 1280, 720);
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

    private void setupHeaderButtons(GridPane gridPane, SelectionState selectionState) {
        gridPane.add(setupOpenButton(), 0, 0);
        gridPane.add(setupSaveButton(), 1, 0);
        gridPane.add(setupPrevButton(selectionState), 2, 0);
        gridPane.add(setupNextButton(selectionState), 4, 0);
    }

    private void setupNameArea(GridPane gridPane, SelectionState selectionState) {
        gridPane.add(setupFusionButton(), 0, 1);
        gridPane.add(setupName(selectionState.getSelectionType(), selectionState.getSlot()), 1, 1, 3, 1);
        gridPane.add(setupFusionButton(), 4, 1);
    }

    private void setupSpriteArea(GridPane gridPane, SelectionState selectionState) {
        Image image = loadImageAtLocation(getImageIndex(selectionState.getSelectionType(), selectionState.getSlot(), selectionState.getSpriteIndex()));
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setScaleX(2);
        imageView.setScaleY(2);
        StackPane stackPane = new StackPane(imageView);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setBackground(getBackground(selectionState)); //160x320
        stackPane.setPrefWidth(160);
        stackPane.setPrefHeight(320);
        gridPane.add(stackPane, 0, 3, 3, 6);
        gridPane.add(getChangeBackgroundButton(), 0, 2, 3, 1);
        if(selectionState.getSelectionType() != CurrentSelectionType.LOGO) {
            gridPane.add(setupPrevSpriteButton(selectionState), 0, 9);
            gridPane.add(setupReplaceSpriteButton(selectionState), 1, 9);
            gridPane.add(setupNextSpriteButton(selectionState), 2, 9);
        }
    }

    private Node setupPrevSpriteButton(SelectionState selectionState) {
        Button button = new Button();
        button.setText("Prev Sprite");
        if(selectionState.getSpriteIndex() == 0) {
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
            return new TextArea("LOGO");
        } else if (selectionType == CurrentSelectionType.EGG) {
            return new TextArea("EGG");
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
                newStateBuilder.selectionType(CurrentSelectionType.EGG);
            } else {
                newStateBuilder.slot(selectionState.getSlot() - 1).spriteIndex(0);
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
                newStateBuilder.selectionType(CurrentSelectionType.SLOT);
            } else {
                newStateBuilder.slot(selectionState.getSlot() + 1).spriteIndex(0);
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
        ByteBuffer byteBuffer = ByteBuffer.wrap(sprite.getBGRA());
        PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<ByteBuffer>(sprite.getWidth(), sprite.getHeight(), byteBuffer, PixelFormat.getByteBgraPreInstance());
        return new WritableImage(pixelBuffer);
    }
}
