package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.CardDataRreader;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class BemCardDataReader extends CardDataRreader<
        BemTransformationRequirements.BemTransformationRequirementEntry,
        BemAdventureLevels.BemAdventureLevel,
        BemSpecificFusions.BemSpecificFusionEntry,
        BemCard,
        BemTransformationEntry,
        BemCharacter,
        BemAdventure,
        BemCardData> {
    public static int NONE_VALUE = 0xFFFF;
    public static int FIRST_CHARACTER_SPRITE_INDEX = 54;
    public static int SPRITES_PER_CHARACTER = 14;

    private Integer getMinutesUntilTransformation(int index, BemCard bemCard) {
        Integer minutes = null;
        for(BemTransformationRequirements.BemTransformationRequirementEntry entry : bemCard.getTransformationRequirements().getTransformationEntries()) {
            if(entry.getFromCharacterIndex() == index) {
                if(minutes != null && entry.getMinutesUntilTransformation() != minutes) {
                    log.error("BEM encountered with different evolution timers from a single digimon. Please log an issue with the BEM on https://github.com/cfogrady/DIM-Modifier/issues");
                }
                minutes = entry.getMinutesUntilTransformation();
            }
        }
        return minutes;
    }

    @Override
    protected CardSprites getCardSprites(BemCard bemCard) {
        List<SpriteData.Sprite> sprites = bemCard.getSpriteData().getSprites();
        List<SpriteData.Sprite> backgrounds = new ArrayList<>();
        backgrounds.add(sprites.get(1));
        backgrounds.add(sprites.get(10));
        backgrounds.addAll(sprites.subList(30, 34));
        return CardSprites.builder()
                .logo(sprites.get(0))
                .backgrounds(backgrounds)
                .egg(sprites.subList(2, 10))
                .ready(sprites.get(11))
                .go(sprites.get(12))
                .win(sprites.get(13))
                .lose(sprites.get(14))
                .hits(sprites.subList(15, 18))
                .types(sprites.subList(18, 22))
                .stages(sprites.subList(22, 30))
                .smallAttacks(sprites.subList(34, 44))
                .bigAttacks(sprites.subList(44, 54))
                .build();
    }

    @Override
    protected BemCardData.BemCardDataBuilder<?, ?> getCardDataBuilder() {
        return BemCardData.builder();
    }

    @Override
    protected BemTransformationEntry.BemTransformationEntryBuilder<?, ?> getTransformationBuilder(BemTransformationRequirements.BemTransformationRequirementEntry rawEntry) {
        return BemTransformationEntry.builder()
                .isSecret(rawEntry.getIsNotSecret() == 0)
                .requiredCompletedAdventureLevel(NoneUtils.nullIfNone(rawEntry.getRequiredCompletedAdventureLevel()));
    }

    @Override
    protected BemCharacter.BemCharacterBuilder<?, ?> getCharacterBuilder(int index, BemCard card, List<UUID> idBySlot) {
        BemCharacterStats.BemCharacterStatEntry characterStatEntry = card.getCharacterStats().getCharacterEntries().get(index);
        return BemCharacter.builder()
                .thirdPoolBattleChance(NoneUtils.nullIfNone(characterStatEntry.getThirdPoolBattleChance()))
                .minutesUntilTransformation(getMinutesUntilTransformation(index, card));
    }

    @Override
    protected BemAdventure.BemAdventureBuilder<? extends BemAdventure, ?> getAdventureBuilder(BemCard bemCard, BemAdventureLevels.BemAdventureLevel adventureLevel, List<UUID> idBySlot) {
        UUID giftCharacter = adventureLevel.getGiftCharacterIndex() == NONE_VALUE ? null : idBySlot.get(adventureLevel.getGiftCharacterIndex());
        Integer smallAttackId = adventureLevel.getSmallAttackId() == bemCard.getCharacterStats().getCharacterEntries().get(adventureLevel.getBossCharacterIndex()).getSmallAttackId() ? null : adventureLevel.getSmallAttackId();
        Integer bigAttackId = adventureLevel.getBigAttackId() == bemCard.getCharacterStats().getCharacterEntries().get(adventureLevel.getBossCharacterIndex()).getBigAttackId() ? null : adventureLevel.getBigAttackId();
        return BemAdventure.builder()
                .smallAttackId(smallAttackId)
                .bigAttackId(bigAttackId)
                .battleBackground(adventureLevel.getBackground2())
                .walkingBackground(adventureLevel.getBackground1())
                .showBossIdentiy(adventureLevel.getShowBossIdentity() == 1)
                .giftCharacter(giftCharacter);
    }

    @Override
    protected List<SpriteData.Sprite> getSpritesForSlot(int index, BemCard bemCard) {
        int start = FIRST_CHARACTER_SPRITE_INDEX + (index * SPRITES_PER_CHARACTER);
        int end = FIRST_CHARACTER_SPRITE_INDEX + ((index + 1) * SPRITES_PER_CHARACTER);
        return bemCard.getSpriteData().getSprites().subList(start, end);
    }
}
