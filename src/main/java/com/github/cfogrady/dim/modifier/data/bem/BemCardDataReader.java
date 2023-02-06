package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.MetaData;
import com.github.cfogrady.dim.modifier.data.dim.*;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.vb.dim.adventure.BemAdventureLevels;
import com.github.cfogrady.vb.dim.card.BemCard;
import com.github.cfogrady.vb.dim.character.BemCharacterStats;
import com.github.cfogrady.vb.dim.fusion.BemAttributeFusions;
import com.github.cfogrady.vb.dim.fusion.BemSpecificFusions;
import com.github.cfogrady.vb.dim.header.BemHeader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.BemTransformationRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Slf4j
public class BemCardDataReader {
    public static int NONE_VALUE = 0xFFFF;
    public static int FIRST_CHARACTER_SPRITE_INDEX = 54;
    public static int SPRITES_PER_CHARACTER = 14;

    public BemCardData fromBemCard(BemCard bemCard) {
        int numberOfSlots = bemCard.getBemCharacterStats().getCharacterEntries().size();
        List<BemCharacter> characters = new ArrayList<>(numberOfSlots);
        List<UUID> idBySlot = IntStream.range(0, numberOfSlots).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toList());
        Map<UUID, Integer> uuidToCharacterSlot = new HashMap<>(numberOfSlots);
        for(int slotIndex = 0; slotIndex < bemCard.getBemCharacterStats().getCharacterEntries().size(); slotIndex++) {
            BemCharacter character = getBemCharacter(slotIndex, bemCard, idBySlot);
            uuidToCharacterSlot.put(character.getId(), slotIndex);
            characters.add(character);
        }
        return BemCardData.builder()
                .metaData(getMetadata(bemCard))
                .characters(characters)
                .adventures(getAdventures(bemCard, idBySlot))
                .uuidToCharacterSlot(uuidToCharacterSlot)
                .cardSprites(getCardSprites(bemCard))
                .build();
    }

    private MetaData getMetadata(BemCard bemCard) {
        BemHeader bemHeader = bemCard.getBemHeader();
        return MetaData.builder()
                .id(bemHeader.getDimId())
                .revision(bemHeader.getRevisionNumber())
                .year(bemHeader.getProductionYear())
                .month(bemHeader.getProductionMonth())
                .day(bemHeader.getProductionDay())
                .originalChecksum(bemCard.getChecksum())
                .build();
    }

    private BemCharacter getBemCharacter(int index, BemCard bemCard, List<UUID> idBySlot) {
        BemCharacterStats.BemCharacterStatEntry characterStatEntry = bemCard.getBemCharacterStats().getCharacterEntries().get(index);
        return BemCharacter.builder()
                .id(idBySlot.get(index))
                .stage(characterStatEntry.getStage())
                .attribute(characterStatEntry.getAttribute())
                .activityType(characterStatEntry.getType())
                .smallAttack(characterStatEntry.getSmallAttackId())
                .bigAttack(characterStatEntry.getBigAttackId())
                .bp(characterStatEntry.getBp())
                .ap(characterStatEntry.getAp())
                .hp(characterStatEntry.getHp())
                .firstPoolBattleChance(NoneUtils.nullIfNone(characterStatEntry.getFirstPoolBattleChance()))
                .secondPoolBattleChance(NoneUtils.nullIfNone(characterStatEntry.getSecondPoolBattleChance()))
                .thirdPoolBattleChance(NoneUtils.nullIfNone(characterStatEntry.getThirdPoolBattleChance()))
                .minutesUntilTransformation(getMinutesUntilTransformation(index, bemCard))
                .transformationEntries(getTransformationsForSlot(index, bemCard, idBySlot))
                .sprites(getSpritesForSlot(index, bemCard))
                .fusions(getAttributeFusions(index, bemCard, idBySlot))
                .specificFusions(getSpecificFusions(index, bemCard, idBySlot))
                .build();
    }

    private Integer getMinutesUntilTransformation(int index, BemCard bemCard) {
        Integer minutes = null;
        for(BemTransformationRequirements.BemTransformationRequirementEntry entry : bemCard.getBemTransformationRequirements().getTransformationEntries()) {
            if(entry.getFromCharacterIndex() == index) {
                if(minutes != null && entry.getMinutesUntilTransformation() != minutes) {
                    log.error("BEM encountered with different evolution timers from a single digimon. Please log an issue with the BEM on https://github.com/cfogrady/DIM-Modifier/issues");
                }
                minutes = entry.getMinutesUntilTransformation();
            }
        }
        return minutes;
    }

    private List<BemTransformationEntry> getTransformationsForSlot(int index, BemCard bemCard, List<UUID> idBySlot) {
        List<BemTransformationEntry> transformationRequirementsForIndex = new ArrayList<>();
        for(BemTransformationRequirements.BemTransformationRequirementEntry entry : bemCard.getBemTransformationRequirements().getTransformationEntries()) {
            if(entry.getFromCharacterIndex() == index && entry.getToCharacterIndex() != NONE_VALUE) {
                transformationRequirementsForIndex.add(convertTransformationRequirementsEntry(entry, idBySlot));
            }
        }
        return transformationRequirementsForIndex;
    }

    private BemTransformationEntry convertTransformationRequirementsEntry(BemTransformationRequirements.BemTransformationRequirementEntry rawEntry, List<UUID> idBySlot) {
        return BemTransformationEntry.builder()
                .vitalRequirements(rawEntry.getRequiredVitalValues())
                .trophyRequirement(rawEntry.getRequiredPp())
                .battleRequirement(rawEntry.getRequiredBattles())
                .winRatioRequirement(rawEntry.getRequiredWinRatio())
                .requiredCompletedAdventureLevel(NoneUtils.nullIfNone(rawEntry.getRequiredCompletedAdventureLevel()))
                .toCharacter(idBySlot.get(rawEntry.getToCharacterIndex()))
                .isSecret(rawEntry.getIsNotSecret() == 0)
                .build();
    }

    private Fusions getAttributeFusions(int index, BemCard bemCard, List<UUID> idBySlot) {
        for(BemAttributeFusions.BemAttributeFusionEntry attributeFusionEntry : bemCard.getBemAttributeFusions().getEntries()) {
            if(attributeFusionEntry.getCharacterIndex() == index) {
                UUID attribute1FusionResult = attributeFusionEntry.getAttribute1Fusion() == NONE_VALUE ? null : idBySlot.get(attributeFusionEntry.getAttribute1Fusion());
                UUID attribute2FusionResult = attributeFusionEntry.getAttribute2Fusion() == NONE_VALUE ? null : idBySlot.get(attributeFusionEntry.getAttribute2Fusion());
                UUID attribute3FusionResult = attributeFusionEntry.getAttribute3Fusion() == NONE_VALUE ? null : idBySlot.get(attributeFusionEntry.getAttribute3Fusion());
                UUID attribute4FusionResult = attributeFusionEntry.getAttribute4Fusion() == NONE_VALUE ? null : idBySlot.get(attributeFusionEntry.getAttribute4Fusion());
                return Fusions.builder()
                        .type1FusionResult(attribute1FusionResult)
                        .type2FusionResult(attribute2FusionResult)
                        .type3FusionResult(attribute3FusionResult)
                        .type4FusionResult(attribute4FusionResult)
                        .build();
            }
        }
        return Fusions.builder().build();
    }

    private List<BemSpecificFusion> getSpecificFusions(int index, BemCard bemCard, List<UUID> idBySlot) {
        List<BemSpecificFusion> specificFusions = new ArrayList<>();
        for(BemSpecificFusions.BemSpecificFusionEntry specificFusionEntry : bemCard.getBemSpecificFusions().getEntries()) {
            if(specificFusionEntry.getFromCharacterIndex() == index) {
                BemSpecificFusion.BemSpecificFusionBuilder<?, ?> builder  = BemSpecificFusion.builder()
                        .evolveToCharacterId(idBySlot.get(specificFusionEntry.getToCharacterIndex()))
                        .partnerDimId(specificFusionEntry.getBackupBemId());
                if(specificFusionEntry.getBackupBemId() == bemCard.getBemHeader().getDimId()) {
                    builder = builder.sameBemPartnerCharacter(idBySlot.get(specificFusionEntry.getBackupCharacterId()))
                            .partnerDimSlotId(null);

                } else {
                    builder = builder.sameBemPartnerCharacter(null).partnerDimSlotId(specificFusionEntry.getBackupCharacterId());
                }
                specificFusions.add(builder.build());
            }
        }
        return specificFusions;
    }

    private List<SpriteData.Sprite> getSpritesForSlot(int index, BemCard bemCard) {
        int start = FIRST_CHARACTER_SPRITE_INDEX + (index * SPRITES_PER_CHARACTER);
        int end = FIRST_CHARACTER_SPRITE_INDEX + ((index + 1) * SPRITES_PER_CHARACTER);
        return bemCard.getSpriteData().getSprites().subList(start, end);
    }

    private List<BemAdventure> getAdventures(BemCard bemCard, List<UUID> idBySlot) {
        List<BemAdventure> adventures = new ArrayList<>(bemCard.getBemAdventureLevels().getLevels().size());
        for(BemAdventureLevels.BemAdventureLevel adventureLevel : bemCard.getBemAdventureLevels().getLevels()) {
            UUID giftCharacter = adventureLevel.getGiftCharacterIndex() == NONE_VALUE ? null : idBySlot.get(adventureLevel.getGiftCharacterIndex());
            Integer smallAttackId = adventureLevel.getSmallAttackId() == bemCard.getBemCharacterStats().getCharacterEntries().get(adventureLevel.getBossCharacterIndex()).getSmallAttackId() ? null : adventureLevel.getSmallAttackId();
            Integer bigAttackId = adventureLevel.getBigAttackId() == bemCard.getBemCharacterStats().getCharacterEntries().get(adventureLevel.getBossCharacterIndex()).getBigAttackId() ? null : adventureLevel.getBigAttackId();
            adventures.add(BemAdventure.builder()
                            .steps(adventureLevel.getSteps())
                            .bossId(idBySlot.get(adventureLevel.getBossCharacterIndex()))
                            .bossBp(adventureLevel.getBp())
                            .bossAp(adventureLevel.getAp())
                            .bossHp(adventureLevel.getHp())
                            .smallAttackId(smallAttackId)
                            .bigAttackId(bigAttackId)
                            .battleBackground(adventureLevel.getBackground2())
                            .walkingBackground(adventureLevel.getBackground1())
                            .showBossIdentiy(adventureLevel.getShowBossIdentity() == 1)
                            .giftCharacter(giftCharacter)
                    .build());
        }
        return adventures;
    }

    private CardSprites getCardSprites(BemCard bemCard) {
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
}
