package com.github.cfogrady.dim.modifier.controls;

import javafx.scene.image.Image;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageIntPair extends LabelValuePair<Integer, Image> {
    private final Image label;
    private final Integer value;
}
