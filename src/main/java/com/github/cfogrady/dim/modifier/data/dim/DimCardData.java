package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.card.Adventure;
import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.vb.dim.card.DimCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DimCardData extends CardData<DimCharacter, Adventure, DimCard> {
    private final DimCard originalCard;

    public static Integer[] DIM_PHASES = {1, 2, 3, 4, 5, 6};

    @Override
    public Integer[] getTotalAvailableStages() {
        return DIM_PHASES;
    }
}
