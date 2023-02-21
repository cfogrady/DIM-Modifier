package com.github.cfogrady.dim.modifier.data.card;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.vb.dim.card.Card;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Data
@SuperBuilder
public abstract class CardData<T1 extends Character<?, T1>, T2 extends Adventure, T3 extends Card<?, ?, ?, ?, ?, ?>> {

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

    public abstract List<String> checkForErrors();

    public abstract int getNumberOfAvailableCharacterSlots();

    public static <T extends Character<?, T>> int getBattleChanceTotal(List<T> characters, Function<T, Integer> getter) {
        int total = 0;
        for(T character : characters){
            if(character.getStage() < 2) {
                continue;
            }
            Integer value = getter.apply(character);
            if(value != null) {
                total += value;
            }
        }
        return total;
    }

    protected List<String> validateAllTransformationsExist() {
        List<String> errors = new ArrayList<>();
        int i = 0;
        for(T1 character : getCharacters()) {
            for(TransformationEntry transformationEntry : character.getTransformationEntries()) {
                UUID uuid = transformationEntry.getToCharacter();
                if(uuid == null || getUuidToCharacterSlot().get(uuid) == null) {
                    errors.add("Character " + i + " has transformation without transformation result character.");
                }
            }
            i++;
        }
        return errors;
    }

    protected List<String> validateAllAdventureCharactersExist() {
        List<String> errors = new ArrayList<>();
        for(int adventureIdx = 0; adventureIdx < getAdventures().size(); adventureIdx++) {
            T2 adventure = getAdventures().get(adventureIdx);
            UUID uuid = adventure.getBossId();
            if(uuid == null || getUuidToCharacterSlot().get(uuid) == null) {
                errors.add("Adventure " + adventureIdx + " has missing boss character.");
            }
        }
        return errors;
    }

    protected List<String> validateSpecificFusionExist() {
        List<String> errors = new ArrayList<>();
        for(int characterIdx = 0; characterIdx < getCharacters().size(); characterIdx++) {
            T1 character = getCharacters().get(characterIdx);
            for(SpecificFusion specificFusion : character.getSpecificFusions()) {
                UUID uuid = specificFusion.getEvolveToCharacterId();
                if(uuid == null || getUuidToCharacterSlot().get(uuid) == null) {
                    errors.add("Character " + characterIdx + " has specific fusion to missing character.");
                }
                if(specificFusion.getPartnerDimId() == getMetaData().getId()) {
                    uuid = specificFusion.getSameBemPartnerCharacter();
                    if(uuid == null || getUuidToCharacterSlot().get(uuid) == null) {
                        errors.add("Character " + characterIdx + " has specific fusion with missing character.");
                    }
                } else if(specificFusion.getPartnerDimSlotId() == null) {
                    errors.add("Character " + characterIdx + " has specific fusion with missing character.");
                }
            }
        }
        return errors;
    }
}
