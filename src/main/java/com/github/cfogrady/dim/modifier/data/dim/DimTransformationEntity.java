package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.card.TransformationEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DimTransformationEntity  extends TransformationEntry {
    private Integer hoursUntilTransformation;
}
