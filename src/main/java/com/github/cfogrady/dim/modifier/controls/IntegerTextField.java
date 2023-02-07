package com.github.cfogrady.dim.modifier.controls;

import com.github.cfogrady.vb.dim.card.DimReader;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public class IntegerTextField extends TextField {

    @Setter
    private int min;
    @Setter
    private int max;
    @Setter
    private boolean allowBlanks;

    @Setter
    private Consumer<Integer> changeReceiver;

    public IntegerTextField() {
    }

    public IntegerTextField(Integer initialValue, Consumer<Integer> valueConsumer) {
        super();
        if(initialValue != null) {
            this.setText(Integer.toString(initialValue));
        }
        this.min = 0;
        this.max = DimReader.NONE_VALUE;
        this.changeReceiver = valueConsumer;
        initialize();
    }

    public void initialize() {
        this.textProperty().addListener((obs,oldv,newv) -> {
            boolean error = false;
            if(newv == null || newv.isBlank()) {
                if(allowBlanks) {
                    changeHandler(null);
                } else {
                    error = true;
                }
            } else {
                try {
                    int value = Integer.parseInt(newv);
                    if(value < min || value > max) {
                        error = true;
                    } else {
                        this.setBorder(null);
                        changeHandler(value);
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

    public void changeHandler(Integer value) {
        if(changeReceiver != null) {
            changeReceiver.accept(value);
        }
    }

    @Override
    public void replaceText(int start, int end, String text)
    {
        if (validate(text))
        {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text)
    {
        if (validate(text))
        {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text)
    {
        return text.matches("[0-9]*");
    }


}
