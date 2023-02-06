package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.dim.modifier.utils.NullUtils;
import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.card.BemCardWriter;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.fusion.BemAttributeFusions;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;
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
public class BemCardDataWriter {
    public static int NONE_VALUE = 0xFFFF;

    private final BemCardWriter bemCardWriter;

    public void write(File file, BemCard originalData, BemCardData modifiedData) {
        BemCard dataToWrite = mergeBack(modifiedData, originalData);
        try(FileOutputStream outputStream = new FileOutputStream(file)) {
            bemCardWriter.writeBemCard(dataToWrite, outputStream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public BemCard mergeBack(BemCardData newData, BemCard original) {
        BemCharacterStats newStats = createStats(original.getBemCharacterStats(), newData.getCharacters());
        BemTransformationRequirements newTransformationRequirements = createTransformationRequirements(original.getBemTransformationRequirements(), newData.getCharacters(), newData.getUuidToCharacterSlot());
        BemAdventureLevels newAdventures = createAdventures(original.getBemAdventureLevels(), newData.getAdventures(), newData.getUuidToCharacterSlot(), newData.getCharacters());
        BemAttributeFusions newAttributeFusions = createAttributeFusions(original.getBemAttributeFusions(), newData.getCharacters(), newData.getUuidToCharacterSlot());
        BemSpecificFusions newSpecificFusions = createSpecificFusions(original.getBemSpecificFusions(), newData);
        SpriteData spriteData = createSpriteData(original.getSpriteData(), newData);
        return original.toBuilder()
                .bemCharacterStats(newStats)
                .bemTransformationRequirements(newTransformationRequirements)
                .bemAdventureLevels(newAdventures)
                .bemAttributeFusions(newAttributeFusions)
                .bemSpecificFusions(newSpecificFusions)
                .spriteData(spriteData)
                .build();
    }

    BemCharacterStats createStats(BemCharacterStats oldStats, List<BemCharacter> characters) {
        List<BemCharacterStats.BemCharacterStatEntry> statBlocks = new ArrayList<>(characters.size());
        for(BemCharacter character : characters) {
            BemCharacterStats.BemCharacterStatEntry.BemCharacterStatEntryBuilder builder = BemCharacterStats.BemCharacterStatEntry.builder();
            builder = builder.stage(character.getStage())
                    .type(character.getActivityType());

            //Ensure we don't write values that will screw up BE for stage < 2
            if(character.getStage() < 2) {
                builder = builder
                        .attribute(0)
                        .smallAttackId(NONE_VALUE)
                        .bigAttackId(NONE_VALUE)
                        .bp(NONE_VALUE)
                        .ap(NONE_VALUE)
                        .hp(NONE_VALUE)
                        .firstPoolBattleChance(NONE_VALUE)
                        .secondPoolBattleChance(NONE_VALUE)
                        .thirdPoolBattleChance(NONE_VALUE);
            } else {
                builder = builder.attribute(character.getAttribute())
                        .smallAttackId(character.getSmallAttack())
                        .bigAttackId(character.getBigAttack())
                        .bp(character.getBp())
                        .ap(character.getAp())
                        .hp(character.getHp())
                        .firstPoolBattleChance(NullUtils.getOrDefault(character.getFirstPoolBattleChance(), NONE_VALUE))
                        .secondPoolBattleChance(NullUtils.getOrDefault(character.getSecondPoolBattleChance(), NONE_VALUE))
                        .thirdPoolBattleChance(NullUtils.getOrDefault(character.getThirdPoolBattleChance(), NONE_VALUE));
            }
            statBlocks.add(builder.build());
        }
        BemCharacterStats.BemCharacterStatsBuilder builder = oldStats.toBuilder();
        return builder.characterEntries(statBlocks).build();
    }

    BemTransformationRequirements createTransformationRequirements(BemTransformationRequirements oldRequirements, List<BemCharacter> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        List<BemTransformationRequirements.BemTransformationRequirementEntry> transformationRequirementBlocks = new ArrayList<>();
        transformationRequirementBlocks.addAll(getTransformationEntriesForRequirements(characters, uuidToCharacterSlot));
        transformationRequirementBlocks.addAll(getTransformationEntriesForFusions(characters, uuidToCharacterSlot));
        return oldRequirements.toBuilder().transformationEntries(transformationRequirementBlocks).build();
    }

    private List<BemTransformationRequirements.BemTransformationRequirementEntry> getTransformationEntriesForRequirements(List<BemCharacter> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        List<BemTransformationRequirements.BemTransformationRequirementEntry> transformationRequirementBlocks = new ArrayList<>();
        for(BemCharacter character : characters) {
            int transformFromIndex = uuidToCharacterSlot.get(character.getId());
            for(BemTransformationEntry transformationEntry : character.getTransformationEntries()) {
                int transformToIndex = uuidToCharacterSlot.get(transformationEntry.getToCharacter());
                if(character.getMinutesUntilTransformation() == null) {
                    throw new IllegalStateException("Somehow have no minutes until transformation even thought we have evolution requirements!");
                }
                int minutesUntilTransformation = character.getMinutesUntilTransformation().intValue();
                transformationRequirementBlocks.add(BemTransformationRequirements.BemTransformationRequirementEntry.builder()
                        .minutesUntilTransformation(minutesUntilTransformation)
                        .fromCharacterIndex(transformFromIndex)
                        .toCharacterIndex(transformToIndex)
                        .isNotSecret(transformationEntry.isSecret() ? 0 : 1)
                        .maximumMinuteOfHour(NONE_VALUE)
                        .minimumMinuteOfHour(0)
                        .requiredBattles(transformationEntry.getBattleRequirement())
                        .requiredVitalValues(transformationEntry.getVitalRequirements())
                        .requiredPp(transformationEntry.getTrophyRequirement())
                        .requiredWinRatio(transformationEntry.getWinRatioRequirement())
                        .requiredCompletedAdventureLevel(NullUtils.getOrDefault(transformationEntry.getRequiredCompletedAdventureLevel(), NONE_VALUE))
                        .build());
            }
        }
        return transformationRequirementBlocks;
    }

    private List<BemTransformationRequirements.BemTransformationRequirementEntry> getTransformationEntriesForFusions(List<BemCharacter> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        List<BemTransformationRequirements.BemTransformationRequirementEntry> transformationRequirementBlocks = new ArrayList<>();
        for(BemCharacter character : characters) {
            int transformFromIndex = uuidToCharacterSlot.get(character.getId());
            if(!character.getSpecificFusions().isEmpty() || !character.getFusions().isEmpty()) {
                if(character.getMinutesUntilTransformation() == null) {
                    throw new IllegalStateException("Somehow have no minutes until transformation even thought we have evolution requirements!");
                }
                transformationRequirementBlocks.add(BemTransformationRequirements.BemTransformationRequirementEntry.builder()
                        .minutesUntilTransformation(character.getMinutesUntilTransformation().intValue())
                        .fromCharacterIndex(transformFromIndex)
                        .requiredVitalValues(0)
                        .requiredPp(0)
                        .requiredWinRatio(0)
                        .requiredBattles(0)
                        .minimumMinuteOfHour(0)
                        .maximumMinuteOfHour(NONE_VALUE)
                        .requiredCompletedAdventureLevel(NONE_VALUE)
                        .toCharacterIndex(NONE_VALUE)
                        .isNotSecret(NONE_VALUE)
                        .build());
            }
        }
        return transformationRequirementBlocks;
    }

    BemAdventureLevels createAdventures(BemAdventureLevels oldAdventures, List<BemAdventure> adventureEntries, Map<UUID, Integer> characterIdToSlot, List<BemCharacter> characters) {
        List<BemAdventureLevels.BemAdventureLevel> adventureLevels = new ArrayList<>(adventureEntries.size());
        for(BemAdventure adventureEntry : adventureEntries) {
            int giftCharacter = adventureEntry.getGiftCharacter() == null ? NONE_VALUE : characterIdToSlot.get(adventureEntry.getGiftCharacter());
            int bossSlotId = characterIdToSlot.get(adventureEntry.getBossId());
            int smallAttackId = adventureEntry.getSmallAttackId() != null ? adventureEntry.getSmallAttackId() : characters.get(bossSlotId).getSmallAttack();
            int bigAttackId = adventureEntry.getBigAttackId() != null ? adventureEntry.getBigAttackId() : characters.get(bossSlotId).getBigAttack();
            adventureLevels.add(BemAdventureLevels.BemAdventureLevel.builder()
                    .steps(adventureEntry.getSteps())
                    .bossCharacterIndex(bossSlotId)
                    .showBossIdentity(adventureEntry.isShowBossIdentiy() ? 1 : 2)
                    .bp(adventureEntry.getBossBp())
                    .hp(adventureEntry.getBossHp())
                    .ap(adventureEntry.getBossAp())
                    .smallAttackId(smallAttackId)
                    .bigAttackId(bigAttackId)
                    .background1(adventureEntry.getWalkingBackground())
                    .background2(adventureEntry.getBattleBackground())
                    .giftCharacterIndex(giftCharacter)
                    .build());
        }
        return oldAdventures.toBuilder().levels(adventureLevels).build();
    }

    BemAttributeFusions createAttributeFusions(BemAttributeFusions oldFusions, List<BemCharacter> characters, Map<UUID, Integer> uuidToCharacterSlot) {
        //fusion will be null for monsterSlots that aren't stage 4+
        List<BemAttributeFusions.BemAttributeFusionEntry> attributeFusionEntries = new ArrayList<>();
        for(BemCharacter character : characters) {
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
                attributeFusionEntries.add(BemAttributeFusions.BemAttributeFusionEntry.builder()
                        .characterIndex(fromCharacterIndex)
                        .attribute1Fusion(type1FusionResultSlot)
                        .attribute2Fusion(type2FusionResultSlot)
                        .attribute3Fusion(type3FusionResultSlot)
                        .attribute4Fusion(type4FusionResultSlot)
                        .build());
            }
        }
        return oldFusions.toBuilder().entries(attributeFusionEntries).build();
    }

    BemSpecificFusions createSpecificFusions(BemSpecificFusions oldSpecificFusions, BemCardData cardData) {
        List<BemSpecificFusions.BemSpecificFusionEntry> specificFusionEntries = new ArrayList<>();
        for(BemCharacter character : cardData.getCharacters()) {
            int transformFromCharacterSlot = cardData.getUuidToCharacterSlot().get(character.getId());
            for(BemSpecificFusion specificFusion : character.getSpecificFusions()) {
                specificFusionEntries.add(BemSpecificFusions.BemSpecificFusionEntry.builder()
                                .fromCharacterIndex(transformFromCharacterSlot)
                                .fromBemId(cardData.getMetaData().getId())
                                .backupBemId(specificFusion.getPartnerDimId())
                                .backupCharacterId(getPartnerCharacterIndex(specificFusion, cardData))
                                .toBemId(cardData.getMetaData().getId())
                                .toCharacterIndex(cardData.getUuidToCharacterSlot().get(specificFusion.getEvolveToCharacterId()))
                        .build());
            }
        }
        return oldSpecificFusions.toBuilder().entries(specificFusionEntries).build();
    }

    private int getPartnerCharacterIndex(BemSpecificFusion specificFusion, BemCardData cardData) {
        if(specificFusion.getPartnerDimId() != cardData.getMetaData().getId()) {
            return specificFusion.getPartnerDimSlotId();
        }
        return cardData.getUuidToCharacterSlot().get(specificFusion.getSameBemPartnerCharacter());
    }

    SpriteData createSpriteData(SpriteData oldSpriteData, BemCardData bemData) {
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
