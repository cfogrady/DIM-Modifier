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
public abstract class CardData<T1 extends Character, T2 extends Adventure, T3 extends Card> {
    private final T3 originalCard;
    private MetaData metaData;
    private List<T1> characters;
    private Map<UUID, Integer> uuidToCharacterSlot;
    private List<T2> adventures;
    private CardSprites cardSprites;

    protected abstract T1 createNewCharacter();

    public void addCharacter(int characterIndex, SpriteImageTranslator spriteImageTranslator) {
        T1 newCharacter = createNewCharacter();
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(spriteImageTranslator.getBlankNameSprite());
        sprites.addAll(CardData.createInitialSpriteList(12, spriteImageTranslator.getBlankCharacterSprite()));
        sprites.add(spriteImageTranslator.getBlankBackgroundSprite());
        newCharacter.setSprites(sprites);
        getCharacters().add(characterIndex, newCharacter);
        resetUUIDToIndexesFrom(characterIndex);
    }

    public void deleteCharacter(int characterIndex) {
        Character<?> removedCharacter = characters.remove(characterIndex);
        uuidToCharacterSlot.remove(removedCharacter.getId());
        for(int i = characterIndex; i < characters.size(); i++) {
            Character<?> character = characters.get(i);
            uuidToCharacterSlot.put(character.getId(), i);
        }
    }

    protected void resetUUIDToIndexesFrom(int index) {
        for(int i = index; i < characters.size(); i++) {
            uuidToCharacterSlot.put(characters.get(i).getId(), i);
        }
    }

    protected static List<SpriteData.Sprite> createInitialSpriteList(int number, SpriteData.Sprite blankSprite) {
        List<SpriteData.Sprite> sprites = new ArrayList<>(number);
        for(int i = 0; i < number; i++) {
            sprites.add(blankSprite);
        }
        return sprites;
    }
}
