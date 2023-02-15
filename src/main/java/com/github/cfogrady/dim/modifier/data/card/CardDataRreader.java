package com.github.cfogrady.dim.modifier.data.card;

import com.github.cfogrady.dim.modifier.data.bem.CardSprites;
import com.github.cfogrady.dim.modifier.utils.NoneUtils;
import com.github.cfogrady.vb.dim.adventure.AdventureLevels;
import com.github.cfogrady.vb.dim.card.Card;
import com.github.cfogrady.vb.dim.character.CharacterStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.SpecificFusions;
import com.github.cfogrady.vb.dim.header.DimHeader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.TransformationRequirements;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class CardDataRreader<
        CardTransformationT extends TransformationRequirements.TransformationRequirementsEntry,
        CardAdventureT extends AdventureLevels.AdventureLevel,
        SpecificFusionT extends SpecificFusions.SpecificFusionEntry,
        CardType extends Card<?, ?, ? extends TransformationRequirements<CardTransformationT>, ? extends AdventureLevels<CardAdventureT>, ?, ? extends SpecificFusions<SpecificFusionT>>,
        TransformationType extends TransformationEntry,
        CharacterType extends Character<TransformationType>,
        AdventureType extends Adventure,
        CardDataType extends CardData<CharacterType, AdventureType, CardType>
        > {

    public static int NONE_VALUE = 0xFFFF;

    public CardDataType fromCard(CardType card) {
        int numberOfSlots = card.getCharacterStats().getCharacterEntries().size();
        List<CharacterType> characters = new ArrayList<>(numberOfSlots);
        List<UUID> idBySlot = IntStream.range(0, numberOfSlots).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toList());
        Map<UUID, Integer> uuidToCharacterSlot = new HashMap<>(numberOfSlots);
        for(int slotIndex = 0; slotIndex < card.getCharacterStats().getCharacterEntries().size(); slotIndex++) {
            CharacterType character = buildCharacter(slotIndex, card, idBySlot);
            uuidToCharacterSlot.put(character.getId(), slotIndex);
            characters.add(character);
        }
        return getCardDataBuilder()
                .originalCard(card)
                .metaData(getMetadata(card))
                .characters(characters)
                .adventures(getAdventures(card, idBySlot))
                .uuidToCharacterSlot(uuidToCharacterSlot)
                .cardSprites(getCardSprites(card))
                .build();
    }

    protected MetaData getMetadata(Card<?, ?, ?, ?, ?, ?> card) {
        DimHeader dimHeader = card.getHeader();
        return MetaData.builder()
                .id(dimHeader.getDimId())
                .revision(dimHeader.getRevisionNumber())
                .year(dimHeader.getProductionYear())
                .month(dimHeader.getProductionMonth())
                .day(dimHeader.getProductionDay())
                .originalChecksum(card.getChecksum())
                .build();
    }

    protected CharacterType buildCharacter(int index, CardType card, List<UUID> idBySlot) {
        CharacterStats<?> stats = card.getCharacterStats();
        CharacterStats.CharacterStatsEntry characterStatEntry = stats.getCharacterEntries().get(index);
        return getCharacterBuilder(index, card, idBySlot)
                .id(idBySlot.get(index))
                .stage(characterStatEntry.getStage())
                .attribute(characterStatEntry.getAttribute())
                .activityType(characterStatEntry.getType())
                .smallAttack(characterStatEntry.getSmallAttackId())
                .bigAttack(characterStatEntry.getBigAttackId())
                .bp(characterStatEntry.getDp())
                .ap(characterStatEntry.getAp())
                .hp(characterStatEntry.getHp())
                .firstPoolBattleChance(NoneUtils.nullIfNone(characterStatEntry.getFirstPoolBattleChance()))
                .secondPoolBattleChance(NoneUtils.nullIfNone(characterStatEntry.getSecondPoolBattleChance()))
                .transformationEntries(buildTransformationsForSlot(index, card, idBySlot))
                .sprites(getSpritesForSlot(index, card))
                .fusions(getAttributeFusions(index, card, idBySlot))
                .specificFusions(getSpecificFusions(index, card, idBySlot))
                .build();
    }

    private List<TransformationType> buildTransformationsForSlot(int index, CardType card, List<UUID> idBySlot) {
        List<TransformationType> transformationRequirementsForIndex = new ArrayList<>();
        for(CardTransformationT entry : card.getTransformationRequirements().getTransformationEntries()) {
            if(entry.getFromCharacterIndex() == index && entry.getToCharacterIndex() != NONE_VALUE) {
                transformationRequirementsForIndex.add(convertTransformationRequirementsEntry(entry, idBySlot));
            }
        }
        return transformationRequirementsForIndex;
    }

    private TransformationType convertTransformationRequirementsEntry(CardTransformationT rawEntry, List<UUID> idBySlot) {
        return getTransformationBuilder(rawEntry)
                .vitalRequirements(rawEntry.getRequiredVitalValues())
                .trophyRequirement(rawEntry.getRequiredTrophies())
                .battleRequirement(rawEntry.getRequiredBattles())
                .winRatioRequirement(rawEntry.getRequiredWinRatio())
                .toCharacter(idBySlot.get(rawEntry.getToCharacterIndex()))
                .build();
    }

    private Fusions getAttributeFusions(int index, CardType card, List<UUID> idBySlot) {
        for(AttributeFusions.AttributeFusionEntry attributeFusionEntry : card.getAttributeFusions().getEntries()) {
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

    private List<SpecificFusion> getSpecificFusions(int index, CardType card, List<UUID> idBySlot) {
        List<SpecificFusion> specificFusions = new ArrayList<>();
        for(SpecificFusions.SpecificFusionEntry specificFusionEntry : card.getSpecificFusions().getEntries()) {
            if(specificFusionEntry.getFromCharacterIndex() == index) {
                SpecificFusion.SpecificFusionBuilder<?, ?> builder  = SpecificFusion.builder()
                        .evolveToCharacterId(idBySlot.get(specificFusionEntry.getToCharacterIndex()))
                        .partnerDimId(specificFusionEntry.getBackupDimId());
                if(specificFusionEntry.getBackupDimId() == card.getHeader().getDimId()) {
                    builder = builder.sameBemPartnerCharacter(idBySlot.get(specificFusionEntry.getBackupCharacterIndex()))
                            .partnerDimSlotId(null);

                } else {
                    builder = builder.sameBemPartnerCharacter(null).partnerDimSlotId(specificFusionEntry.getBackupCharacterIndex());
                }
                specificFusions.add(builder.build());
            }
        }
        return specificFusions;
    }

    private List<AdventureType> getAdventures(CardType card, List<UUID> idBySlot) {
        List<AdventureType> adventures = new ArrayList<>(card.getAdventureLevels().getLevels().size());
        for(CardAdventureT adventureLevel : card.getAdventureLevels().getLevels()) {
            adventures.add(getAdventureBuilder(card, adventureLevel, idBySlot)
                    .steps(adventureLevel.getSteps())
                    .bossId(idBySlot.get(adventureLevel.getBossCharacterIndex()))
                    .bossBp(adventureLevel.getBossDp())
                    .bossAp(adventureLevel.getBossAp())
                    .bossHp(adventureLevel.getBossHp())
                    .build());
        }
        return adventures;
    }

    protected abstract List<SpriteData.Sprite> getSpritesForSlot(int index, CardType card);

    protected abstract Character.CharacterBuilder<TransformationType, ? extends CharacterType, ?> getCharacterBuilder(int index, CardType card, List<UUID> idBySlot);

    protected abstract CardData.CardDataBuilder<CharacterType, AdventureType, CardType, ? extends CardDataType, ?> getCardDataBuilder();

    protected abstract TransformationEntry.TransformationEntryBuilder<? extends TransformationType, ?> getTransformationBuilder(CardTransformationT rawEntry);

    protected abstract Adventure.AdventureBuilder<? extends AdventureType, ?> getAdventureBuilder(CardType card, CardAdventureT adventureLevel, List<UUID> idBySlot);

    protected abstract CardSprites getCardSprites(CardType card);

}
