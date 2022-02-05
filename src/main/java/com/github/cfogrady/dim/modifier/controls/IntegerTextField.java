package com.github.cfogrady.dim.modifier.controls;

import com.github.cfogrady.vb.dim.reader.reader.DimReader;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class IntegerTextField extends TextField {

    private int min;
    private int max;

    public IntegerTextField(int initialValue, Consumer<Integer> valueConsumer) {
        super();
        this.setText(Integer.toString(initialValue));
        this.min = 0;
        this.max = DimReader.NONE_VALUE;
        this.textProperty().addListener((obs,oldv,newv) -> {
            boolean error = false;
            if(newv == null || newv.isBlank()) {
                error = true;
            } else {
                try {
                    int value = Integer.parseInt(newv);
                    if(value < min || value > max) {
                        error = true;
                    } else {
                        this.setBorder(null);
                        valueConsumer.accept(value);
                    }
                } catch (NumberFormatException e) {
                    error = true;
                }
            }
            if(error) {
                this.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            }
        });
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
