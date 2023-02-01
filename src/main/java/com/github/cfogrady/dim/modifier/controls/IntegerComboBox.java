package com.github.cfogrady.dim.modifier.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.function.Consumer;

public class IntegerComboBox extends ComboBox<Integer> {

    public static final String NONE = "None";

    public IntegerComboBox(Integer currentValue, int range, int offset, boolean includeNoneValue, Consumer<Integer> valueSetter) {
        super();
        this.setItems(createList(range, includeNoneValue));
        this.setValue(getItemForValue(currentValue));
        this.setOnAction(e -> {
            int newValue = this.getValue();
            valueSetter.accept(newValue);
        });
        this.setCellFactory(lv -> new IntegerCell(offset));
        this.setButtonCell(new IntegerCell(offset));
    }

    private Integer getItemForValue(Integer value) {
        for(Integer label : getItems()) {
            if(label == value) {
                return label;
            }
        }
        return null;
    }

    private ObservableList<Integer> createList(int size, boolean includeNone) {
        ArrayList<Integer> list = new ArrayList<>(size + (includeNone ? 1 : 0));
        if(includeNone) {
            list.add(null);
        }
        for(int i = 0; i < size; i++) {
            list.add(i);
        }
        return FXCollections.observableArrayList(list);
    }

    @RequiredArgsConstructor
    public static class IntegerCell extends ListCell<Integer> {
        private final int offset;

        @Override
        protected void updateItem(Integer label, boolean empty) {
            super.updateItem(label, empty) ;
            if (empty) {
                setText(null);
            } else {
                if(label == null) {
                    setText(NONE);
                } else {
                    setText(Integer.toString(label + offset));
                }
            }
        }
    }
}
