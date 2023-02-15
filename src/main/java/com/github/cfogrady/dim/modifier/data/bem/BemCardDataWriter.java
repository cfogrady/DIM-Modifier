package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.CardDataWriter;
import com.github.cfogrady.dim.modifier.data.card.SpecificFusion;
import com.github.cfogrady.dim.modifier.utils.NullUtils;
import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.character.CharacterStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BemCardDataWriter extends CardDataWriter<
        BemCharacterStats.BemCharacterStatEntry,
        BemCharacterStats,
        BemTransformationRequirements.BemTransformationRequirementEntry,
        BemTransformationRequirements,
        BemAdventureLevels.BemAdventureLevel,
        BemAdventureLevels,
        AttributeFusions,
        BemSpecificFusions.BemSpecificFusionEntry,
        BemSpecificFusions,
        BemCard,
        BemTransformationEntry,
        BemCharacter,
        BemAdventure,
        BemCardData> {

    public BemCardDataWriter(DimWriter dimWriter) {
        super(dimWriter);
    }

    @Override
    protected BemCard internalMergeBack(BemCharacterStats stats, BemTransformationRequirements transformations, BemAdventureLevels adventures,
                                        AttributeFusions attributeFusions, BemSpecificFusions specificFusionT, SpriteData spriteData) {
        return BemCard.builder()
                .characterStats(stats)
                .transformationRequirements(transformations)
                .adventureLevels(adventures)
                .attributeFusions(attributeFusions)
                .specificFusions(specificFusionT)
                .spriteData(spriteData)
                .build();
    }

    @Override
    protected BemCharacterStats.BemCharacterStatEntry finalizeCharacterStats(CharacterStats.CharacterStatsEntry.CharacterStatsEntryBuilder<? extends BemCharacterStats.BemCharacterStatEntry, ?> builder, BemCharacter character) {
        if(character.getStage() < 2) {
            builder = builder.smallAttackId(255)
                    .bigAttackId(255);
        }
        return builder.build();
    }

    @Override
    protected BemCharacterStats.BemCharacterStatEntry.BemCharacterStatEntryBuilder<?, ?> getStatsBuilder(BemCharacter character) {
        return BemCharacterStats.BemCharacterStatEntry.builder()
                .spriteResizeFlag(2)
                .thirdPoolBattleChance(character.getStage() < 2 ? NONE_VALUE : NullUtils.getOrDefault(character.getThirdPoolBattleChance(), NONE_VALUE));
    }

    @Override
    protected BemCharacterStats mergeNewStatEntries(BemCharacterStats old, List<BemCharacterStats.BemCharacterStatEntry> entries) {
        return old.toBuilder().characterEntries(entries).build();
    }

    @Override
    protected BemTransformationRequirements mergeNewTransformationEntries(BemTransformationRequirements old, List<BemTransformationRequirements.BemTransformationRequirementEntry> entries) {
        return old.toBuilder().transformationEntries(entries).build();
    }

    @Override
    protected BemTransformationRequirements.BemTransformationRequirementEntry.BemTransformationRequirementEntryBuilder<?, ?> getTransformationEntryBuilder(BemCharacter character, BemTransformationEntry transformationEntry) {
        if(character.getMinutesUntilTransformation() == null) {
            throw new IllegalStateException("Somehow have no minutes until transformation even though we have evolution requirements!");
        }
        int minutesUntilTransformation = character.getMinutesUntilTransformation();
        return BemTransformationRequirements.BemTransformationRequirementEntry.builder()
                .minutesUntilTransformation(minutesUntilTransformation)
                .isNotSecret(transformationEntry.isSecret() ? 0 : 1)
                .maximumMinuteOfHour(NONE_VALUE)
                .minimumMinuteOfHour(0)
                .requiredCompletedAdventureLevel(NullUtils.getOrDefault(transformationEntry.getRequiredCompletedAdventureLevel(), NONE_VALUE));
    }

    @Override
    protected BemTransformationRequirements.BemTransformationRequirementEntry.BemTransformationRequirementEntryBuilder<?, ?> getTransformationEntryFromFusionBuilder(BemCharacter character) {
        if(character.getMinutesUntilTransformation() == null) {
            throw new IllegalStateException("Somehow have no minutes until transformation even though we have evolution requirements!");
        }
        return BemTransformationRequirements.BemTransformationRequirementEntry.builder()
                .minutesUntilTransformation(character.getMinutesUntilTransformation())
                .minimumMinuteOfHour(0)
                .maximumMinuteOfHour(NONE_VALUE)
                .requiredCompletedAdventureLevel(NONE_VALUE)
                .isNotSecret(NONE_VALUE);
    }

    @Override
    protected BemAdventureLevels.BemAdventureLevel.BemAdventureLevelBuilder<?, ?> getAdventureLevelBuilder(BemAdventure adventureEntry, List<BemCharacter> characters, Map<UUID, Integer> characterIdToSlot) {
        int bossSlotId = characterIdToSlot.get(adventureEntry.getBossId());
        BemCharacter bossCharacter = characters.get(bossSlotId);
        int giftCharacter = adventureEntry.getGiftCharacter() == null ? NONE_VALUE : characterIdToSlot.get(adventureEntry.getGiftCharacter());
        int smallAttackId = adventureEntry.getSmallAttackId() != null ? adventureEntry.getSmallAttackId() : bossCharacter.getSmallAttack();
        int bigAttackId = adventureEntry.getBigAttackId() != null ? adventureEntry.getBigAttackId() : bossCharacter.getBigAttack();
        return BemAdventureLevels.BemAdventureLevel.builder()
                .showBossIdentity(adventureEntry.isShowBossIdentiy() ? 1 : 2)
                .smallAttackId(smallAttackId)
                .bigAttackId(bigAttackId)
                .background1(adventureEntry.getWalkingBackground())
                .background2(adventureEntry.getBattleBackground())
                .giftCharacterIndex(giftCharacter);
    }

    @Override
    protected BemAdventureLevels mergeNewAdventureEntries(BemAdventureLevels old, List<BemAdventureLevels.BemAdventureLevel> levels) {
        return old.toBuilder().levels(levels).build();
    }

    @Override
    protected AttributeFusions mergeAttributeFusions(AttributeFusions oldFusions, List<AttributeFusions.AttributeFusionEntry> entries) {
        return oldFusions.toBuilder().entries(entries).build();
    }

    @Override
    protected BemSpecificFusions.BemSpecificFusionEntry.BemSpecificFusionEntryBuilder<?, ?> getSpecificFusionBuilder(SpecificFusion specificFusion, BemCardData cardData) {
        return BemSpecificFusions.BemSpecificFusionEntry.builder()
                .fromBemId(cardData.getMetaData().getId())
                .toBemId(cardData.getMetaData().getId());
    }

    @Override
    protected BemSpecificFusions mergeSpecificFusions(BemSpecificFusions old, List<BemSpecificFusions.BemSpecificFusionEntry> entries) {
        return old.toBuilder().entries(entries).build();
    }

    @Override
    protected SpriteData createSpriteData(SpriteData oldSpriteData, BemCardData bemData) {
        CardSprites cardSprites = bemData.getCardSprites();
        SpriteData.SpriteDataBuilder builder = oldSpriteData.toBuilder();
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(cardSprites.getLogo());
        sprites.add(cardSprites.getDisplayBackground());
        sprites.addAll(cardSprites.getEgg());
        sprites.add(cardSprites.getBattleBackground());
        sprites.add(cardSprites.getReady());
        sprites.add(cardSprites.getGo());
        sprites.add(cardSprites.getWin());
        sprites.add(cardSprites.getLose());
        sprites.addAll(cardSprites.getHits());
        sprites.addAll(cardSprites.getTypes());
        sprites.addAll(cardSprites.getStages());
        sprites.addAll(cardSprites.getGroupedAdventureBackgrounds());
        sprites.addAll(cardSprites.getSmallAttacks());
        sprites.addAll(cardSprites.getBigAttacks());
        List<BemCharacter> characters = bemData.getCharacters();
        for(BemCharacter character : characters) {
            // use correct number of sprites for monsters with lower level than stage 2
            sprites.addAll(character.getSprites());
        }
        return builder.sprites(sprites).build();
    }
}
