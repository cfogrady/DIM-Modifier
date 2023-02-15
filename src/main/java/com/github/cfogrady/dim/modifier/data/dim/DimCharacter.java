package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.card.TransformationEntry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder()
@EqualsAndHashCode(callSuper = true)
public class DimCharacter extends Character<TransformationEntry> {
    private Integer hoursUntilTransformation;
    private int stars;
    private boolean finishAdventureToUnlock;
}
