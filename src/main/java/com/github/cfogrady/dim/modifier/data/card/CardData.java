package com.github.cfogrady.dim.modifier.data.card;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.bem.CardSprites;
import com.github.cfogrady.vb.dim.card.Card;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@SuperBuilder
public class CardData<T1 extends Character<?, T1>, T2 extends Adventure, T3 extends Card<?, ?, ?, ?, ?, ?>> {
    private final T3 originalCard;
    private MetaData metaData;
    private List<T1> characters;
    private Map<UUID, Integer> uuidToCharacterSlot;
    private List<T2> adventures;
    private CardSprites cardSprites;

    public void addCharacter(int characterIndex, SpriteImageTranslator spriteImageTranslator) {
        T1 newCharacter = characters.get(characterIndex).copyCharacter(spriteImageTranslator);
        getCharacters().add(characterIndex, newCharacter);
        resetUUIDToIndexesFrom(characterIndex);
    }

    public void deleteCharacter(int characterIndex) {
        Character<?, ?> removedCharacter = characters.remove(characterIndex);
        uuidToCharacterSlot.remove(removedCharacter.getId());
        for(int i = characterIndex; i < characters.size(); i++) {
            Character<?, ?> character = characters.get(i);
            uuidToCharacterSlot.put(character.getId(), i);
        }
    }

    public static Integer[] PHASES = {1, 2, 3, 4, 5, 6, 7, 8};

    public Integer[] getTotalAvailableStages() {
        return PHASES;
    }

    protected void resetUUIDToIndexesFrom(int index) {
        for(int i = index; i < characters.size(); i++) {
            uuidToCharacterSlot.put(characters.get(i).getId(), i);
        }
    }
}
