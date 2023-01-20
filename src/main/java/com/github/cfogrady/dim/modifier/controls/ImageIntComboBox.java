package com.github.cfogrady.dim.modifier.controls;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
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
        for(ImageIntPair label : getItems()) {
            if(label.getValue() == value) {
                return label;
            }
        }
        return null;
    }

    @Data
    public static class ImageIntPair {
        private final ImageView label;
        private final int value;
    }

    public static class ImageIntCell extends ListCell<ImageIntPair> {
        @Override
        protected void updateItem(ImageIntPair label, boolean empty) {
            super.updateItem(label, empty) ;
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(label.getLabel());
            }
        }
    }
}
