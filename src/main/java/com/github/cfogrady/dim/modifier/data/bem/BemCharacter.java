package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.Character;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BemCharacter extends Character<BemTransformationEntry> {
    private int thirdPoolBattleChance;
    private Integer minutesUntilTransformation;
    private List<BemSpecificFusion> specificFusions;
}
