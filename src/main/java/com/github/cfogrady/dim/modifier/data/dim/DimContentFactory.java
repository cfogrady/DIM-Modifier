package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.vb.dim.adventure.DimAdventures;
import com.github.cfogrady.vb.dim.card.DimCard;
import com.github.cfogrady.vb.dim.card.DimReader;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.fusion.DimFusions;
import com.github.cfogrady.vb.dim.fusion.DimSpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DimContentFactory {
    /**
     * @param dimContent - Format used by writing library
     * @param dimData - Format used for this app
     * @return
     */
    public DimCard merge(DimCard dimContent, DimData dimData) {
        // TODO: Move this into the createDimStat method and return as tuple
        Map<UUID, Integer> monsterIdToSlot = new HashMap<>();
        for(int slotId = 0; slotId < dimData.getMonsterSlotList().size(); slotId++) {
            monsterIdToSlot.put(dimData.getMonsterSlotList().get(slotId).getId(), slotId);
        }
        DimStats newDimStats = createDimStat(dimContent.getDimStats(), dimData.getMonsterSlotList());
        DimEvolutionRequirements newEvolutionRequirements = createDimEvolutionRequirements(dimContent.getDimEvolutionRequirements(), dimData.getMonsterSlotList(), monsterIdToSlot);
        DimAdventures newAdventures = createDimAdventures(dimContent.getDimAdventures(), dimData.getAdventureEntries(), monsterIdToSlot);
        DimFusions newFusions = createDimFusions(dimContent.getDimFusions(), dimData.getMonsterSlotList(), monsterIdToSlot);
        DimSpecificFusions newSpecificFusions = createDimSpecificFusions(dimContent.getDimSpecificFusion(), dimData.getSpecificFusions(), monsterIdToSlot);
        SpriteData spriteData = createSpriteData(dimContent.getSpriteData(), dimData);
        return dimContent.toBuilder()
                .dimStats(newDimStats)
                .dimEvolutionRequirements(newEvolutionRequirements)
                .dimAdventures(newAdventures)
                .dimFusions(newFusions)
                .dimSpecificFusion(newSpecificFusions)
                .spriteData(spriteData)
                .build();
    }

    DimStats createDimStat(DimStats oldDimStats, List<MonsterSlot> monsterSlots) {
        List<DimStats.DimStatBlock> statBlocks = new ArrayList<>(monsterSlots.size());
        for(MonsterSlot monsterSlot : monsterSlots) {
            //Ensure we don't write values that will screw up VB for stage < 2
            if(monsterSlot.getStatBlock().getStage() < 2) {
                statBlocks.add(DimStats.DimStatBlock.builder()
                                .stage(monsterSlot.getStatBlock().getStage())
                                .attribute(0)
                                .disposition(monsterSlot.getStatBlock().getDisposition())
                                .unlockRequired(false)
                                .dp(DimReader.NONE_VALUE)
                                .dpStars(DimReader.NONE_VALUE)
                                .ap(DimReader.NONE_VALUE)
                                .hp(DimReader.NONE_VALUE)
                                .smallAttackId(DimReader.NONE_VALUE)
                                .bigAttackId(DimReader.NONE_VALUE)
                                .firstPoolBattleChance(DimReader.NONE_VALUE)
                                .secondPoolBattleChance(DimReader.NONE_VALUE)
                                .build());
            } else {
                statBlocks.add(monsterSlot.getStatBlock());
            }
        }
        DimStats.DimStatsBuilder builder = oldDimStats.toBuilder();
        if(oldDimStats.getStatBlocks().size() != statBlocks.size() && statBlocks.size() < DimStats.VB_TABLE_SIZE) {
            log.info("Number of digimon on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimStats.VB_TABLE_SIZE - statBlocks.size());
        }
        return builder.statBlocks(statBlocks).build();
    }

    DimEvolutionRequirements createDimEvolutionRequirements(DimEvolutionRequirements oldRequirements, List<MonsterSlot> monsterSlots, Map<UUID, Integer> monsterIdToSlot) {
        List<DimEvolutionRequirements.DimEvolutionRequirementBlock> evolutionRequirementBlocks = new ArrayList<>();
        DimEvolutionRequirements.DimEvolutionRequirementsBuilder evolutionRequirementsBuilder = oldRequirements.toBuilder();
        for(MonsterSlot monsterSlot : monsterSlots) {
            for(EvolutionEntry evolutionEntry : monsterSlot.getEvolutionEntries()) {
                UUID evolveToMonsterId = evolutionEntry.getToMonster();
                int evolveToMonsterSlot = evolveToMonsterId == null ? DimReader.NONE_VALUE : monsterIdToSlot.get(evolveToMonsterId);
                int hoursUntilEvolve = monsterSlot.getHoursUntilEvolution();
                evolutionRequirementBlocks.add(evolutionEntry.getEvolutionRequirementBlock().toBuilder()
                                .evolveFromStatIndex(monsterIdToSlot.get(monsterSlot.getId()))
                                .evolveToStatIndex(evolveToMonsterSlot)
                                .hoursUntilEvolution(hoursUntilEvolve)
                                .build());
            }
        }
        if(evolutionRequirementBlocks.size() != oldRequirements.getEvolutionRequirementBlocks().size() && evolutionRequirementBlocks.size() < DimEvolutionRequirements.VB_TABLE_SIZE) {
            log.info("Number of evolutions on this dim has changed and the new value is less than table size. Using full table dummy rows");
            evolutionRequirementsBuilder = evolutionRequirementsBuilder.dummyRows(DimEvolutionRequirements.VB_TABLE_SIZE - evolutionRequirementBlocks.size());
        }
        return evolutionRequirementsBuilder.evolutionRequirementBlocks(evolutionRequirementBlocks).build();
    }

    DimAdventures createDimAdventures(DimAdventures oldAdventures, List<AdventureEntry> adventureEntries, Map<UUID, Integer> monsterIdToSlot) {
        List<DimAdventures.DimAdventureBlock> adventureBlocks = new ArrayList<>(adventureEntries.size());
        for(AdventureEntry adventureEntry : adventureEntries) {
            adventureBlocks.add(DimAdventures.DimAdventureBlock.builder()
                            .steps(adventureEntry.getSteps())
                            .bossStatsIndex(monsterIdToSlot.get(adventureEntry.getMonsterId()))
                            .bossHp(adventureEntry.getBossHp())
                            .bossAp(adventureEntry.getBossAp())
                            .bossDp(adventureEntry.getBossDp())
                            .build());
        }
        if(adventureBlocks.size() != oldAdventures.getAdventureBlocks().size()) {
            log.warn("Number of adventures on this dim has changed and the new value is less than table size. All known valid DIMs have 15 adventures. Leaving remainder of table blank");
        }
        return oldAdventures.toBuilder().adventureBlocks(adventureBlocks).build();
    }

    DimFusions createDimFusions(DimFusions oldFusions, List<MonsterSlot> monsterSlots, Map<UUID, Integer> monsterIdToSlot) {
        //fusion will be null for monsterSlots that aren't stage 4+
        List<DimFusions.DimFusionBlock> fusionBlocks = new ArrayList<>();
        for(MonsterSlot monsterSlot : monsterSlots) {
            if(monsterSlot.getFusions() != null) {
                UUID type1FusionResultId = monsterSlot.getFusions().getType1FusionResult();
                int type1FusionResultSlot = type1FusionResultId == null ? DimReader.NONE_VALUE : monsterIdToSlot.get(type1FusionResultId);
                UUID type2FusionResultId = monsterSlot.getFusions().getType2FusionResult();
                int type2FusionResultSlot = type2FusionResultId == null ? DimReader.NONE_VALUE : monsterIdToSlot.get(type2FusionResultId);
                UUID type3FusionResultId = monsterSlot.getFusions().getType3FusionResult();
                int type3FusionResultSlot = type3FusionResultId == null ? DimReader.NONE_VALUE : monsterIdToSlot.get(type3FusionResultId);
                UUID type4FusionResultId = monsterSlot.getFusions().getType4FusionResult();
                int type4FusionResultSlot = type4FusionResultId == null ? DimReader.NONE_VALUE : monsterIdToSlot.get(type4FusionResultId);
                fusionBlocks.add(DimFusions.DimFusionBlock.builder()
                                .statsIndex(monsterIdToSlot.get(monsterSlot.getId()))
                                .statsIndexForFusionWithType1(type1FusionResultSlot)
                                .statsIndexForFusionWithType2(type2FusionResultSlot)
                                .statsIndexForFusionWithType3(type3FusionResultSlot)
                                .statsIndexForFusionWithType4(type4FusionResultSlot)
                                .build());
            }
        }
        DimFusions.DimFusionsBuilder builder = oldFusions.toBuilder();
        if(fusionBlocks.size() != oldFusions.getFusionBlocks().size() && fusionBlocks.size() < DimFusions.VB_TABLE_SIZE) {
            log.info("Number of fusions on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimFusions.VB_TABLE_SIZE - fusionBlocks.size());
        }
        return builder.fusionBlocks(fusionBlocks).build();
    }

    DimSpecificFusions createDimSpecificFusions(DimSpecificFusions oldSpecificFusions, List<SpecificFusion> specificFusions, Map<UUID, Integer> monsterIdToSlot) {
        List<DimSpecificFusions.DimSpecificFusionBlock> specificFusionBlocks = new ArrayList<>(specificFusions.size());
        DimSpecificFusions.DimSpecificFusionsBuilder builder = oldSpecificFusions.toBuilder();
        for(SpecificFusion specificFusion : specificFusions) {
            Integer evolveFromMonsterSlot = monsterIdToSlot.get(specificFusion.getLocalMonsterId());
            Integer evolveToMonsterSlot = monsterIdToSlot.get(specificFusion.getEvolveToMonsterId());
            if(evolveFromMonsterSlot == null || evolveToMonsterSlot == null) {
                log.warn("Missing monster for fusion... skipping entry");
            } else {
                specificFusionBlocks.add(DimSpecificFusions.DimSpecificFusionBlock.builder()
                                .statsIndex(evolveFromMonsterSlot)
                                .statsIndexForFusionResult(evolveToMonsterSlot)
                                .fusionDimId(specificFusion.partnerDimId)
                                .fusionDimSlotId(specificFusion.getPartnerDimSlotId())
                                .build());
            }
        }
        if(specificFusionBlocks.size() != oldSpecificFusions.getDimSpecificFusionBlocks().size() && oldSpecificFusions.getDimSpecificFusionBlocks().size() < DimSpecificFusions.VB_TABLE_SIZE) {
            log.info("Number of specific fusions on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimSpecificFusions.VB_TABLE_SIZE - specificFusionBlocks.size());
        }
        return builder.dimSpecificFusionBlocks(specificFusionBlocks).build();
    }

    SpriteData createSpriteData(SpriteData oldSpriteData, DimData dimData) {
        List<MonsterSlot> monsterSlots = dimData.getMonsterSlotList();
        SpriteData.SpriteDataBuilder builder = oldSpriteData.toBuilder();
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(dimData.getLogoSprite());
        sprites.add(dimData.getBackGroundSprite());
        for(SpriteData.Sprite eggSprite : dimData.getEggSprites()) {
            sprites.add(eggSprite);
        }
        for(MonsterSlot monsterSlot : monsterSlots) {
            // use correct number of sprites for monsters with lower level than stage 2
            int monsterStage = monsterSlot.getStatBlock().getStage();
            for(int i = 0; i < DimDataFactory.getSpriteCountForLevel(monsterStage); i++) {
                if(i >= monsterSlot.getSprites().size()) {
                    sprites.add(SpriteData.Sprite.builder().width(1).height(1).pixelData(createDummySprite(1, 1)).build());
                } else {
                    sprites.add(monsterSlot.getSprites().get(i));
                }
            }
        }
        return builder.sprites(sprites).build();
    }

    public static byte[] createDummySprite(int width, int height) {
        byte[] pixelData = new byte[width*height*2];
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                pixelData[(y*width + x)*2] = (byte) 0b11100000;
                pixelData[(y*width + x)*2 + 1] = (byte) 0b00000111;
            }
        }
        return pixelData;
    }
}
