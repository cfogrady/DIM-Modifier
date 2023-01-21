package com.github.cfogrady.dim.modifier.controls;

import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Data;

import java.util.function.Consumer;

public class ImageIntComboBox extends ComboBox<ImageIntComboBox.ImageIntPair> {

    public ImageIntComboBox(int currentValue, ObservableList<ImageIntPair> valueLabels, Consumer<Integer> valueSetter) {
        super();
        this.setItems(valueLabels);
        this.setValue(getItemForValue(currentValue));
        this.setOnAction(e -> {
            int newValue = this.getValue().getValue();
            valueSetter.accept(newValue);
        });
        this.setCellFactory(lv -> new ImageIntCell());
        this.setButtonCell(new ImageIntCell());
    }

    private ImageIntPair getItemForValue(int value) {
        for(ImageIntPair option : getItems()) {
            if(option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

    @Data
    public static class ImageIntPair {
        private final Image image;
        private final int value;
    }

    public static class ImageIntCell extends ListCell<ImageIntPair> {
        @Override
        protected void updateItem(ImageIntPair option, boolean empty) {
            super.updateItem(option, empty) ;
            setText(null);
            setGraphic(null);
            if (!empty) {
                ImageView imageView = new ImageView(option.getImage());
                imageView.setFitWidth(option.getImage().getWidth() * 2.0);
                imageView.setFitHeight(option.getImage().getHeight() * 2.0);
                VBox.setMargin(imageView, new Insets(10));
                setGraphic(imageView);
            }
        }
    }
}
