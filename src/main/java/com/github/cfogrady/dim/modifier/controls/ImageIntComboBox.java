package com.github.cfogrady.dim.modifier.controls;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class ImageIntComboBox extends ComboBox<ImageIntComboBox.ImageIntPair> {

    public ImageIntComboBox() {
        super();
    }

    public ImageIntComboBox(int currentValue, ObservableList<ImageIntPair> valueLabels, Consumer<Integer> valueSetter) {
        this(currentValue, valueLabels, valueSetter, 1.0);
    }

    public ImageIntComboBox(int currentValue, ObservableList<ImageIntPair> valueLabels, Consumer<Integer> valueSetter, double imageScaler) {
        this(currentValue, valueLabels, valueSetter, imageScaler, null);
    }

    public ImageIntComboBox(int currentValue, ObservableList<ImageIntPair> valueLabels, Consumer<Integer> valueSetter, double imageScaler, Background background) {
        super();
        this.setItems(valueLabels);
        this.setValue(getItemForValue(currentValue));
        if(valueSetter != null) {
            this.setOnAction(e -> {
                int newValue = this.getValue().getValue();
                valueSetter.accept(newValue);
            });
        }
        this.setCellFactory(lv -> new ImageIntCell(imageScaler, null, background));
        this.setButtonCell(new ImageIntCell(imageScaler, null, background));
    }

    public void initialize(Integer currentValue, ObservableList<ImageIntPair> valueLabels, double imageScaler, Background background, String noneText) {
        this.setItems(valueLabels);
        this.setValue(getItemForValue(currentValue));
        this.setCellFactory(lv -> new ImageIntCell(imageScaler, noneText, background));
        this.setButtonCell(new ImageIntCell(imageScaler, noneText, background));
    }

    public void initialize(ObservableList<ImageIntPair> valueLabels) {
        initialize(null, valueLabels, 1.0, null, null);
    }

    public ImageIntPair getItemForValue(Integer value) {
        for(ImageIntPair option : getItems()) {
            if(option.getValue() == value) {
                return option;
            }
        }
        return null;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ImageIntPair extends LabelValuePair<Integer, Image> {
        private final Image label;
        private final Integer value;
    }

    @RequiredArgsConstructor
    public static class ImageIntCell extends ListCell<ImageIntPair> {
        private final double scaler;
        private final String noneText;
        private final Background background;

        @Override
        protected void updateItem(ImageIntPair option, boolean empty) {
            super.updateItem(option, empty) ;
            setText(null);
            setGraphic(null);
            if(empty || option.getValue() == null) {
                StackPane stackPane = new StackPane(new Text(noneText == null ? "NONE" : noneText));
                VBox.setMargin(stackPane, new Insets(10));
                setGraphic(stackPane);
            } else if (!empty) {
                ImageView imageView = new ImageView(option.getLabel());
                imageView.setFitWidth(option.getLabel().getWidth() * scaler);
                imageView.setFitHeight(option.getLabel().getHeight() * scaler);
                if(background != null) {
                    StackPane stackPane = new StackPane(imageView);
                    stackPane.setMaxHeight(option.getLabel().getHeight() * scaler);
                    stackPane.setMaxWidth(option.getLabel().getWidth() * scaler);
                    stackPane.setBackground(background);
                    VBox.setMargin(stackPane, new Insets(10));
                    setGraphic(stackPane);
                } else {
                    VBox.setMargin(imageView, new Insets(10));
                    setGraphic(imageView);
                }
            }
        }
    }
}
