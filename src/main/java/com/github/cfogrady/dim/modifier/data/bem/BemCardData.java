package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BemCardData extends CardData<BemCharacter, BemAdventure, BemCard> {
    private static final Set<Integer> SKIPPED_BABY_SPRITE_INDEXES = Set.of(4, 5, 6, 7, 8, 11, 12);

    @Override
    public List<String> checkForErrors() {
        List<String> errors = new ArrayList<>();
        errors.addAll(validateAllTransformationsExist());
        errors.addAll(validateSpecificFusionExist());
        errors.addAll(validateAllAdventureCharactersExist());
        errors.addAll(validateNFCBattlesTotal100());
        errors.addAll(validateCharacterSpriteUniformity());
        return errors;
    }

    private List<String> validateNFCBattlesTotal100() {
        List<String> errors = new ArrayList<>();
        int firstPoolTotal = CardData.getBattleChanceTotal(getCharacters(), BemCharacter::getFirstPoolBattleChance);
        if(firstPoolTotal != 100) {
            errors.add("• Phase 3/4 Battle Pool Chances do not total 100%");
        }
        int secondPoolTotal = CardData.getBattleChanceTotal(getCharacters(), BemCharacter::getSecondPoolBattleChance);
        if(secondPoolTotal != 100) {
            errors.add("• Phase 5/6 Battle Pool Chances do not total 100%");
        }
        int thridPoolTotal = CardData.getBattleChanceTotal(getCharacters(), BemCharacter::getThirdPoolBattleChance);
        if(thridPoolTotal != 100) {
            errors.add("• Phase 7+ Battle Pool Chances do not total 100%");
        }
        return errors;
    }

    private List<String> validateCharacterSpriteUniformity() {
        List<String> errors = new ArrayList<>();
        for(int characterIdx = 0; characterIdx < getCharacters().size(); characterIdx++) {
            BemCharacter character = getCharacters().get(characterIdx);
            SpriteData.SpriteDimensions spriteDimensions = null;
            for(int i = 1; i < 13; i++) {
                if(SKIPPED_BABY_SPRITE_INDEXES.contains(i)) {
                    continue;
                }
                if(spriteDimensions == null) {
                    spriteDimensions = character.getSprites().get(i).getSpriteDimensions();
                } else if(!spriteDimensions.equals(character.getSprites().get(i).getSpriteDimensions())) {
                    errors.add("• Character " + characterIdx + " has a mixture of sprite dimensions." + System.lineSeparator() + "Sprites used by the phase must be uniform.");
                    break;
                }
            }
        }
        return errors;
    }
}
