package com.github.cfogrady.dim.modifier.data.card;

import com.github.cfogrady.dim.modifier.utils.NullUtils;
import com.github.cfogrady.vb.dim.adventure.AdventureLevels;
import com.github.cfogrady.vb.dim.card.Card;
import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.character.CharacterStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.SpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.TransformationRequirements;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class CardDataWriter<
        CardStatsEntryT extends CharacterStats.CharacterStatsEntry,
        CardStatsT extends CharacterStats<CardStatsEntryT>,
        CardTransformationEntryT extends TransformationRequirements.TransformationRequirementsEntry,
        CardTransformationT extends TransformationRequirements<CardTransformationEntryT>,
        CardAdventureEntryT extends AdventureLevels.AdventureLevel,
        CardAdventureT extends AdventureLevels<CardAdventureEntryT>,
        CardAttributeFusionT extends AttributeFusions,
        SpecificFusionEntryT extends SpecificFusions.SpecificFusionEntry,
        SpecificFusionT extends SpecificFusions<SpecificFusionEntryT>,
        CardType extends Card<?, CardStatsT, CardTransformationT, CardAdventureT, CardAttributeFusionT, SpecificFusionT>,
        TransformationType extends TransformationEntry,
        CharacterType extends Character<TransformationType, CharacterType>,
        AdventureType extends Adventure,
        CardDataType extends CardData<CharacterType, AdventureType, CardType>
        > {

    public static int NONE_VALUE = 0xFFFF;

    private final DimWriter dimWriter;

    public void write(File file, CardDataType modifiedData) {
        CardType card = mergeBack(modifiedData);
        try(FileOutputStream outputStream = new FileOutputStream(file)) {
            dimWriter.writeCard(card, outputStream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public CardType mergeBack(CardDataType data) {
        CardStatsT newStats = createStats(data.getOriginalCard().getCharacterStats(), data.getCharacters());
        CardTransformationT newTransformationRequirements = createTransformationRequirements(data.getOriginalCard().getTransformationRequirements(), data.getCharacters(), data.getUuidToCharacterSlot());
        CardAdventureT newAdventures = createAdventures(data.getOriginalCard().getAdventureLevels(), data.getAdventures(), data.getUuidToCharacterSlot(), data.getCharacters());
        CardAttributeFusionT newAttributeFusions = createAttributeFusions(data.getOriginalCard().getAttributeFusions(), data.getCharacters(), data.getUuidToCharacterSlot());
        SpecificFusionT newSpecificFusions = createSpecificFusions(data.getOriginalCard().getSpecificFusions(), data);
        SpriteData spriteData = createSpriteData(data.getOriginalCard().getSpriteData(), data);
        return internalMergeBack(newStats, newTransformationRequirements, newAdventures, newAttributeFusions, newSpecificFusions, spriteData);
    }

    protected abstract CardType internalMergeBack(CardStatsT stats, CardTransformationT transformations, CardAdventureT adventures, CardAttributeFusionT attributeFusions, SpecificFusionT specificFusionT, SpriteData spriteData);

    protected CardStatsT createStats(CardStatsT oldStats, List<CharacterType> characters) {
        List<CardStatsEntryT> statBlocks = new ArrayList<>(characters.size());
        for(CharacterType character : characters) {
            CharacterStats.CharacterStatsEntry.CharacterStatsEntryBuilder<? extends CardStatsEntryT, ?> builder = getStatsBuilder(character);
            builder = builder.stage(character.getStage())
                    .type(character.getActivityType());
            //Ensure we don't write values that will screw up BE for stage < 2
            if(character.getStage() < 2) {
                builder = builder
                        .attribute(character.getAttribute())
                        .smallAttackId(NONE_VALUE)
                        .bigAttackId(NONE_VALUE)
                        .dp(NONE_VALUE)
                        .ap(NONE_VALUE)
                        .hp(NONE_VALUE)
                        .firstPoolBattleChance(NONE_VALUE)
                        .secondPoolBattleChance(NONE_VALUE);
            } else {
                builder = builder.attribute(character.getAttribute())
                        .smallAttackId(character.getSmallAttack())
                        .bigAttackId(character.getBigAttack())
                        .dp(character.getBp())
                        .ap(character.getAp())
                        .hp(character.getHp())
                        .firstPoolBattleChance(NullUtils.getOrDefault(character.getFirstPoolBattleChance(), NONE_VALUE))
                        .secondPoolBattleChance(NullUtils.getOrDefault(character.getSecondPoolBattleChance(), NONE_VALUE));
            }
            statBlocks.add(finalizeCharacterStats(builder, character));
        }
        return mergeNewStatEntries(oldStats, statBlocks);
    }

    protected abstract CardStatsEntryT finalizeCharacterStats(CharacterStats.CharacterStatsEntry.CharacterStatsEntryBuilder<? extends CardStatsEntryT, ?> builder, CharacterType character);

    protected abstract CardStatsT mergeNewStatEntries(CardStatsT old, List<CardStatsEntryT> entries);

    protected abstract CharacterStats.CharacterStatsEntry.CharacterStatsEntryBuilder<? extends CardStatsEntryT, ?> getStatsBuilder(CharacterType character);

    CardTransformationT createTransformationRequirements(CardTransformationT oldRequirements, List<CharacterType> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        List<CardTransformationEntryT> transformationRequirementBlocks = new ArrayList<>();
        transformationRequirementBlocks.addAll(getTransformationEntriesForRequirements(characters, uuidToCharacterSlot));
        transformationRequirementBlocks.addAll(getTransformationEntriesForFusions(characters, uuidToCharacterSlot));
        return mergeNewTransformationEntries(oldRequirements, transformationRequirementBlocks);
    }

    protected abstract CardTransformationT mergeNewTransformationEntries(CardTransformationT old, List<CardTransformationEntryT> entries);

    protected List<CardTransformationEntryT> getTransformationEntriesForRequirements(List<CharacterType> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        List<CardTransformationEntryT> transformationRequirementBlocks = new ArrayList<>();
        for(CharacterType character : characters) {
            int transformFromIndex = uuidToCharacterSlot.get(character.getId());
            for(TransformationType transformationEntry : character.getTransformationEntries()) {
                int transformToIndex = uuidToCharacterSlot.get(transformationEntry.getToCharacter());
                transformationRequirementBlocks.add(getTransformationEntryBuilder(character, transformationEntry)
                        .fromCharacterIndex(transformFromIndex)
                        .toCharacterIndex(transformToIndex)
                        .requiredBattles(transformationEntry.getBattleRequirement())
                        .requiredVitalValues(transformationEntry.getVitalRequirements())
                        .requiredTrophies(transformationEntry.getTrophyRequirement())
                        .requiredWinRatio(transformationEntry.getWinRatioRequirement())
                        .build());
            }
        }
        return transformationRequirementBlocks;
    }

    protected abstract TransformationRequirements.TransformationRequirementsEntry.TransformationRequirementsEntryBuilder<? extends CardTransformationEntryT, ?> getTransformationEntryBuilder(CharacterType character, TransformationType transformation);

    protected List<CardTransformationEntryT> getTransformationEntriesForFusions(List<CharacterType> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        List<CardTransformationEntryT> transformationRequirementBlocks = new ArrayList<>();
        for(CharacterType character : characters) {
            int transformFromIndex = uuidToCharacterSlot.get(character.getId());
            if(includeBlankFusionRow(character)) {

                transformationRequirementBlocks.add(getTransformationEntryFromFusionBuilder(character)
                        .fromCharacterIndex(transformFromIndex)
                        .requiredVitalValues(0)
                        .requiredTrophies(0)
                        .requiredWinRatio(0)
                        .requiredBattles(0)
                        .toCharacterIndex(NONE_VALUE)
                        .build());
            }
        }
        return transformationRequirementBlocks;
    }

    protected boolean includeBlankFusionRow(CharacterType character) {
        return !character.getSpecificFusions().isEmpty() || !character.getFusions().isEmpty();
    }

    protected abstract TransformationRequirements.TransformationRequirementsEntry.TransformationRequirementsEntryBuilder<? extends CardTransformationEntryT, ?> getTransformationEntryFromFusionBuilder(CharacterType character);

    CardAdventureT createAdventures(CardAdventureT oldAdventures, List<AdventureType> adventureEntries, Map<UUID, Integer> characterIdToSlot, List<CharacterType> characters) {
        List<CardAdventureEntryT> adventureLevels = new ArrayList<>(adventureEntries.size());
        for(AdventureType adventureEntry : adventureEntries) {
            int bossSlotId = characterIdToSlot.get(adventureEntry.getBossId());
            adventureLevels.add(getAdventureLevelBuilder(adventureEntry, characters, characterIdToSlot)
                    .steps(adventureEntry.getSteps())
                    .bossCharacterIndex(bossSlotId)
                    .bossDp(adventureEntry.getBossBp())
                    .bossHp(adventureEntry.getBossHp())
                    .bossAp(adventureEntry.getBossAp())
                    .build());
        }
        return mergeNewAdventureEntries(oldAdventures, adventureLevels);
    }

    protected abstract AdventureLevels.AdventureLevel.AdventureLevelBuilder<? extends CardAdventureEntryT, ?> getAdventureLevelBuilder(AdventureType adventureEntry, List<CharacterType> characters, Map<UUID, Integer> characterIdToSlot);
    protected abstract CardAdventureT mergeNewAdventureEntries(CardAdventureT old, List<CardAdventureEntryT> entries);

    protected CardAttributeFusionT createAttributeFusions(CardAttributeFusionT oldFusions, List<CharacterType> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        //fusion will be null for monsterSlots that aren't stage 4+
        List<AttributeFusions.AttributeFusionEntry> attributeFusionEntries = new ArrayList<>();
        for(CharacterType character : characters) {
            int fromCharacterIndex = uuidToCharacterSlot.get(character.getId());
            if(character.getFusions() != null && !character.getFusions().isEmpty()) {
                UUID type1FusionResultId = character.getFusions().getType1FusionResult();
                int type1FusionResultSlot = type1FusionResultId == null ? NONE_VALUE : uuidToCharacterSlot.get(type1FusionResultId);
                UUID type2FusionResultId = character.getFusions().getType2FusionResult();
                int type2FusionResultSlot = type2FusionResultId == null ? NONE_VALUE : uuidToCharacterSlot.get(type2FusionResultId);
                UUID type3FusionResultId = character.getFusions().getType3FusionResult();
                int type3FusionResultSlot = type3FusionResultId == null ? NONE_VALUE : uuidToCharacterSlot.get(type3FusionResultId);
                UUID type4FusionResultId = character.getFusions().getType4FusionResult();
                int type4FusionResultSlot = type4FusionResultId == null ? NONE_VALUE : uuidToCharacterSlot.get(type4FusionResultId);
                attributeFusionEntries.add(AttributeFusions.AttributeFusionEntry.builder()
                        .characterIndex(fromCharacterIndex)
                        .attribute1Fusion(type1FusionResultSlot)
                        .attribute2Fusion(type2FusionResultSlot)
                        .attribute3Fusion(type3FusionResultSlot)
                        .attribute4Fusion(type4FusionResultSlot)
                        .build());
            }
        }
        return mergeAttributeFusions(oldFusions, attributeFusionEntries);
    }

    protected abstract CardAttributeFusionT mergeAttributeFusions(CardAttributeFusionT oldFusions, List<AttributeFusions.AttributeFusionEntry> fusionEntries);

    SpecificFusionT createSpecificFusions(SpecificFusionT oldSpecificFusions, CardDataType cardData) {
        List<SpecificFusionEntryT> specificFusionEntries = new ArrayList<>();
        for(CharacterType character : cardData.getCharacters()) {
            int transformFromCharacterSlot = cardData.getUuidToCharacterSlot().get(character.getId());
            for(SpecificFusion specificFusion : character.getSpecificFusions()) {
                specificFusionEntries.add(getSpecificFusionBuilder(specificFusion, cardData)
                        .fromCharacterIndex(transformFromCharacterSlot)
                        .backupDimId(specificFusion.getPartnerDimId())
                        .backupCharacterIndex(getPartnerCharacterIndex(specificFusion, cardData))
                        .toCharacterIndex(cardData.getUuidToCharacterSlot().get(specificFusion.getEvolveToCharacterId()))
                        .build());
            }
        }
        return mergeSpecificFusions(oldSpecificFusions, specificFusionEntries);
    }

    private int getPartnerCharacterIndex(SpecificFusion specificFusion, CardDataType cardData) {
        if(specificFusion.getPartnerDimId() != cardData.getMetaData().getId()) {
            return specificFusion.getPartnerDimSlotId();
        }
        return cardData.getUuidToCharacterSlot().get(specificFusion.getSameBemPartnerCharacter());
    }

    protected abstract SpecificFusions.SpecificFusionEntry.SpecificFusionEntryBuilder<? extends SpecificFusionEntryT, ?> getSpecificFusionBuilder(SpecificFusion specificFusion, CardDataType cardData);

    protected abstract SpecificFusionT mergeSpecificFusions(SpecificFusionT old, List<SpecificFusionEntryT> entries);

    protected abstract SpriteData createSpriteData(SpriteData oldSpriteData, CardDataType cardData);

}
