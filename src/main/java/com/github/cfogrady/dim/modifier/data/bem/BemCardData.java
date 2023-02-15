package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.card.Fusions;
import com.github.cfogrady.vb.dim.card.BemCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BemCardData extends CardData<BemCharacter, BemAdventure, BemCard> {

    @Override
    protected BemCharacter createNewCharacter() {
        return BemCharacter.builder()
                .id(UUID.randomUUID())
                .transformationEntries(new ArrayList<>())
                .specificFusions(new ArrayList<>())
                .fusions(Fusions.builder().build()).id(UUID.randomUUID()).build();
    }
}
