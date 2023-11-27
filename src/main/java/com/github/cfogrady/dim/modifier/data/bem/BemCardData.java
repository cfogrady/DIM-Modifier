package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BemCardData extends CardData<BemCharacter, BemAdventure, BemCard> {
    private static final Set<Integer> SKIPPED_BABY_SPRITE_INDEXES = Set.of(4, 5, 6, 7, 8, 11, 12);
    private List<BemSpecificFusions.BemSpecificFusionEntry> fusionsFromOtherCardsWithThisCard;

    @Override
    public List<String> checkForErrors() {
        List<String> errors = new ArrayList<>();
        errors.addAll(validateAllTransformationsExist());
        errors.addAll(validateAllTransformationsHaveTime());
        errors.addAll(validateSpecificFusionExist());
        errors.addAll(validateAllAdventureCharactersExist());
        errors.addAll(validateNFCBattlesTotal100());
        errors.addAll(validateCharacterSpriteUniformity());
        return errors;
    }

    @Override
    public int getNumberOfAvailableCharacterSlots() {
        return 23;
    }

    private List<String> validateAllTransformationsHaveTime() {
        List<String> errors = new ArrayList<>();
        for(int i = 0; i < getCharacters().size(); i++) {
            BemCharacter character = getCharacters().get(i);
            if(character.hasTransformations()) {
                errors.addAll(validateCharacterTransformations(i, character));
            }
        }
        return errors;
    }

    private static List<String> validateCharacterTransformations(int characterIndex, BemCharacter character) {
        List<String> errors = new ArrayList<>();
        if ((!character.getFusions().isEmpty() || !character.getSpecificFusions().isEmpty()) && character.getMinutesUntilFusionCheck() == null) {
            errors.add("Character " + characterIndex + " has fusions," + System.lineSeparator() +
                    "  but time until fusion checks is not set.");
        }
        for (int i = 0; i < character.getTransformationEntries().size(); i++) {
            BemTransformationEntry transformationEntry = character.getTransformationEntries().get(i);
            if (transformationEntry.getMinutesUntilTransformation() == null) {
                errors.add("Character " + characterIndex + " transformation " + (i+1) + " does not have" + System.lineSeparator() +
                        "minutes until transformations set.");
            }
        }
        return errors;
    }

    private List<String> validateNFCBattlesTotal100() {
        List<String> errors = new ArrayList<>();
        int firstPoolTotal = CardData.getBattleChanceTotal(getCharacters(), BemCharacter::getFirstPoolBattleChance);
        if(firstPoolTotal != 100) {
            errors.add("Phase 3/4 Battle Pool Chances do not total 100%");
        }
        int secondPoolTotal = CardData.getBattleChanceTotal(getCharacters(), BemCharacter::getSecondPoolBattleChance);
        if(secondPoolTotal != 100) {
            errors.add("Phase 5/6 Battle Pool Chances do not total 100%");
        }
        int thridPoolTotal = CardData.getBattleChanceTotal(getCharacters(), BemCharacter::getThirdPoolBattleChance);
        if(thridPoolTotal != 100) {
            errors.add("Phase 7+ Battle Pool Chances do not total 100%");
        }
        return errors;
    }

    private List<String> validateCharacterSpriteUniformity() {
        List<String> errors = new ArrayList<>();
        for(int characterIdx = 0; characterIdx < getCharacters().size(); characterIdx++) {
            BemCharacter character = getCharacters().get(characterIdx);
            Set<SpriteData.SpriteDimensions> legitSpriteDimensionsFound = new HashSet<>();
            for(int i = 1; i < 13; i++) {
                SpriteData.SpriteDimensions spriteDimensions = character.getSprites().get(i).getSpriteDimensions();
                if(character.isSpriteSizeValid(spriteDimensions)) {
                    legitSpriteDimensionsFound.add(spriteDimensions);
                } else {
                    log.info("Illegal Sprite size for character {} at sprite index {}. Assumed placeholder sprite.", characterIdx, i);
                }
            }
            if(legitSpriteDimensionsFound.size() > 1) {
                errors.add("Character " + characterIdx + " has a mixture of sprite dimensions." + System.lineSeparator() + "  Sprites used by the character must be uniform.");
            }
        }
        return errors;
    }
}
