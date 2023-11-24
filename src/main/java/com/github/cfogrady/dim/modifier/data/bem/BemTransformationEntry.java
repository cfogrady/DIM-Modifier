package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.TransformationEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BemTransformationEntry extends TransformationEntry {
    private Integer minutesUntilTransformation;
    private Integer requiredCompletedAdventureLevel;
    private boolean isSecret;
}
