package com.github.cfogrady.dim.modifier.controls;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import lombok.Data;

import java.util.function.Consumer;

public class StringIntComboBox extends ComboBox<StringIntComboBox.StringIntPair> {

    public StringIntComboBox(int currentValue, ObservableList<StringIntPair> valueLabels,  Consumer<Integer> valueSetter) {
        super();
        this.setItems(valueLabels);
        this.setValue(getItemForValue(currentValue));
        this.setOnAction(e -> {
            int newValue = this.getValue().getValue();
            valueSetter.accept(newValue);
        });
        this.setCellFactory(lv -> new StringIntCell());
        this.setButtonCell(new StringIntCell());
    }

    private StringIntPair getItemForValue(int value) {
        for(StringIntPair label : getItems()) {
            if(label.getValue() == value) {
                return label;
            }
        }
        return null;
    }

    @Data
    public static class StringIntPair {
        private final String label;
        private final Integer value;
    }

    public static class StringIntCell extends ListCell<StringIntPair> {
        @Override
        protected void updateItem(StringIntPair label, boolean empty) {
            super.updateItem(label, empty) ;
            if (empty) {
                setText(null);
            } else {
                setText(label.getLabel());
            }
        }
    }
}
