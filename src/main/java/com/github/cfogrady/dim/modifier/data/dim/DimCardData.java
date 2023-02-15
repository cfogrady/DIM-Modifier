package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.card.Adventure;
import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.card.Fusions;
import com.github.cfogrady.vb.dim.card.DimCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DimCardData extends CardData<DimCharacter, Adventure, DimCard> {
    private final DimCard originalCard;

    @Override
    protected DimCharacter createNewCharacter() {
        return DimCharacter.builder()
                .id(UUID.randomUUID())
                .transformationEntries(new ArrayList<>())
                .specificFusions(new ArrayList<>())
                .fusions(Fusions.builder().build())
                .build();
    }
}
