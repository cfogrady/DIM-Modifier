package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.card.CardSprites;
import com.github.cfogrady.dim.modifier.data.card.*;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.vb.dim.adventure.AdventureLevels;
import com.github.cfogrady.vb.dim.card.DimCard;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.fusion.SpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class DimCardDataReader extends CardDataRreader<
        DimEvolutionRequirements.DimEvolutionRequirementBlock,
        AdventureLevels.AdventureLevel,
        SpecificFusions.SpecificFusionEntry,
        DimCard,
        DimTransformationEntity,
        DimCharacter,
        Adventure,
        DimCardData> {

    private static final int BABY_I_SPRITE_COUNT = 6;
    private static final int BABY_II_SPRITE_COUNT = 7;
    private static final int NORMAL_SPRITE_COUNT = 14;

    @Override
    protected DimCharacter.DimCharacterBuilder<?, ?> getCharacterBuilder(int index, DimCard card, List<UUID> idBySlot) {
        DimStats.DimStatBlock stats = card.getCharacterStats().getCharacterEntries().get(index);
        return DimCharacter.builder()
                .hoursUntilFusionCheck(getHoursUntilTransformation(index, card))
                .stars(stats.getDpStars())
                .finishAdventureToUnlock(stats.isUnlockRequired());
    }

    private Integer getHoursUntilTransformation(int index, DimCard dimCard) {
        Integer hours = null;
        for(DimEvolutionRequirements.DimEvolutionRequirementBlock entry : dimCard.getTransformationRequirements().getTransformationEntries()) {
            if(entry.getFromCharacterIndex() == index && entry.getToCharacterIndex() == NONE_VALUE) {
                if(hours != null) {
                    log.error("DIM encountered with different fusion evolution timers from a single digimon. Please log an issue with the BEM on https://github.com/cfogrady/DIM-Modifier/issues");
                }
                hours = entry.getHoursUntilEvolution();
            }
        }
        return hours == null ? hours : NoneUtils.nullIfNone(hours);
    }

    @Override
    protected DimCardData.DimCardDataBuilder<?, ?> getCardDataBuilder(DimCard card) {
        return DimCardData.builder();
    }

    @Override
    protected DimTransformationEntity.DimTransformationEntityBuilder<?, ?> getTransformationBuilder(DimEvolutionRequirements.DimEvolutionRequirementBlock rawEntry) {
        return DimTransformationEntity.builder().hoursUntilTransformation(rawEntry.getHoursUntilEvolution());
    }

    @Override
    protected Adventure.AdventureBuilder<?, ?> getAdventureBuilder(DimCard card, AdventureLevels.AdventureLevel adventureLevel, List<UUID> idBySlot) {
        return Adventure.builder();
    }

    @Override
    protected CardSprites getCardSprites(DimCard card) {
        List<SpriteData.Sprite> sprites = card.getSpriteData().getSprites();
        return CardSprites.builder()
                .logo(sprites.get(0))
                .backgrounds(sprites.subList(1, 2))
                .egg(sprites.subList(2, 10))
                .smallAttacks(new ArrayList<>())
                .bigAttacks(new ArrayList<>())
                .hits(new ArrayList<>())
                .types(new ArrayList<>())
                .stages(new ArrayList<>())
                .build();
    }

    @Override
    protected List<SpriteData.Sprite> getSpritesForSlot(int index, DimCard card) {
        int startingSprite = 10;
        for(int i = 0; i < index; i++) {
            int stage = card.getCharacterStats().getCharacterEntries().get(i).getStage();
            startingSprite += numberOfSpritesForStage(stage);
        }
        int stage = card.getCharacterStats().getCharacterEntries().get(index).getStage();
        return card.getSpriteData().getSprites().subList(startingSprite, startingSprite + numberOfSpritesForStage(stage));
    }

    public static int numberOfSpritesForStage(int stage) {
        if(stage == 0) {
            return BABY_I_SPRITE_COUNT;
        } else if(stage == 1) {
            return BABY_II_SPRITE_COUNT;
        } else {
            return NORMAL_SPRITE_COUNT;
        }
    }
}
