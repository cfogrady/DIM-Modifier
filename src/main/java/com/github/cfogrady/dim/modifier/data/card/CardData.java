package com.github.cfogrady.dim.modifier.data.card;

import com.github.cfogrady.dim.modifier.data.bem.CardSprites;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
public class CardData<T1 extends Character, T2 extends Adventure> {
    private MetaData metaData;
    private List<T1> characters;
    private Map<UUID, Integer> uuidToCharacterSlot;
    private List<T2> adventures;
    private CardSprites cardSprites;
}
