package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.reader.content.*;
import com.github.cfogrady.vb.dim.reader.reader.DimReader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DimDataFactory {
    private static final int BABY_I_SPRITE_COUNT = 6;
    private static final int BABY_II_SPRITE_COUNT = 7;
    private static final int NORMAL_SPRITE_COUNT = 14;

    public DimData fromDimContent(DimContent content) {
        int numberOfSlots = content.getDimStats().getStatBlocks().size();
        List<MonsterSlot> monsterSlotList = new ArrayList<>(numberOfSlots);
        List<UUID> idBySlot = IntStream.range(0, numberOfSlots).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toList());
        List<List<SpriteData.Sprite>> spritesBySlot = getSpritesForSlots(content);
        List<List<EvolutionEntry>> evolutionsBySlot = getEvolutionsForSlots(content, idBySlot);
        List<Fusions> fusionsBySlot = getFusionsForSlots(content, idBySlot);
        int slotIndex = 0;
        Map<UUID, Integer> monsterSlotIndexById = new HashMap<>(numberOfSlots);
        for(DimStats.DimStatBlock statsBlock : content.getDimStats().getStatBlocks()) {
            int hoursUntilEvolution = evolutionsBySlot.get(slotIndex).get(0).getEvolutionRequirementBlock().getHoursUntilEvolution();
            MonsterSlot monsterSlot = MonsterSlot.builder()
                    .id(idBySlot.get(slotIndex))
                    .statBlock(statsBlock)
                    .sprites(spritesBySlot.get(slotIndex))
                    .hoursUntilEvolution(hoursUntilEvolution)
                    .evolutionEntries(evolutionsBySlot.get(slotIndex))
                    .fusions(fusionsBySlot.get(slotIndex))
                    .build();
            monsterSlotIndexById.put(monsterSlot.getId(), slotIndex);
            monsterSlotList.add(monsterSlot);
            slotIndex++;
        }
        return DimData.builder().monsterSlotList(monsterSlotList)
                .adventureEntries(getAdventures(content, idBySlot))
                .specificFusions(getSpecificFusions(content, idBySlot))
                .monsterSlotIndexById(monsterSlotIndexById)
                .logoSprite(content.getSpriteData().getSprites().get(0))
                .backGroundSprite(content.getSpriteData().getSprites().get(1))
                .eggSprites(content.getSpriteData().getSprites().subList(2, 10))
                .build();
    }

    /**
     * Returns a list of list of sprites. The first list is corresponds to each slot. The second list corresponds to all
     * the sprites for that slot.
     * @param dimContent
     * @return
     */
    List<List<SpriteData.Sprite>> getSpritesForSlots(DimContent dimContent) {
        int numberOfSlots = dimContent.getDimStats().getStatBlocks().size();
        List<List<SpriteData.Sprite>> spritesBySlot = new ArrayList<>(numberOfSlots);
        int index = 2 + 8; //logo+background + egg sprites
        for(int monSlot = 0; monSlot < numberOfSlots; monSlot++) {
            int level = dimContent.getDimStats().getStatBlocks().get(monSlot).getStage();
            int spriteCount = getSpriteCountForLevel(level);
            List<SpriteData.Sprite> spritesForSlot = new ArrayList<>(spriteCount);
            for(int i = index; i < index + spriteCount; i++) {
                spritesForSlot.add(dimContent.getSpriteData().getSprites().get(i));
            }
            spritesBySlot.add(spritesForSlot);
            index += spriteCount;
        }
        return spritesBySlot;
    }

    public static int getSpriteCountForLevel(int level) {
        if(level == 0) {
            return BABY_I_SPRITE_COUNT;
        } else if(level == 1) {
            return BABY_II_SPRITE_COUNT;
        } else {
            return NORMAL_SPRITE_COUNT;
        }
    }

    /**
     * Returns a list of list of evolutions. The first list is corresponds to each slot. The second list corresponds to all
     * the evolutions for that slot.
     * @param dimContent
     * @return
     */
    List<List<EvolutionEntry>> getEvolutionsForSlots(DimContent dimContent, List<UUID> idsBySlot) {
        List<List<EvolutionEntry>> evolutionsBySlot = new ArrayList<>(idsBySlot.size());
        for(int i = 0; i < idsBySlot.size(); i++) {
            evolutionsBySlot.add(new ArrayList<>());
        }
        for(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirement : dimContent.getDimEvolutionRequirements().getEvolutionRequirementBlocks()) {
            int slot = evolutionRequirement.getEvolveFromStatIndex();
            UUID toMonsterId = evolutionRequirement.getEvolveToStatIndex() == DimReader.NONE_VALUE ? null : idsBySlot.get(evolutionRequirement.getEvolveToStatIndex());
            EvolutionEntry evolutionEntry = EvolutionEntry.builder().evolutionRequirementBlock(evolutionRequirement).toMonster(toMonsterId).build();
            evolutionsBySlot.get(slot).add(evolutionEntry);
        }
        return evolutionsBySlot;
    }

    /**
     * Returns a list of fusions. The list corresponds to an entry for each slot.
     * @param dimContent
     * @return
     */
    List<Fusions> getFusionsForSlots(DimContent dimContent, List<UUID> idsBySlot) {
        List<Fusions> fusionsBySlot = new ArrayList<>(idsBySlot.size());
        for(int i = 0; i < idsBySlot.size(); i++) {
            fusionsBySlot.add(null);
        }
        for(DimFusions.DimFusionBlock fusionBlock : dimContent.getDimFusions().getFusionBlocks()) {
            int type1FusionResult = fusionBlock.getStatsIndexForFusionWithType1();
            int type2FusionResult = fusionBlock.getStatsIndexForFusionWithType2();
            int type3FusionResult = fusionBlock.getStatsIndexForFusionWithType3();
            int type4FusionResult = fusionBlock.getStatsIndexForFusionWithType4();
            Fusions fusions = Fusions.builder()
                    .type1FusionResult(type1FusionResult == DimReader.NONE_VALUE ? null : idsBySlot.get(type1FusionResult))
                    .type2FusionResult(type2FusionResult == DimReader.NONE_VALUE ? null : idsBySlot.get(type2FusionResult))
                    .type3FusionResult(type3FusionResult == DimReader.NONE_VALUE ? null : idsBySlot.get(type3FusionResult))
                    .type4FusionResult(type4FusionResult == DimReader.NONE_VALUE ? null : idsBySlot.get(type4FusionResult))
                    .build();
            fusionsBySlot.set(fusionBlock.getStatsIndex(), fusions);
        }
        return fusionsBySlot;
    }

    List<AdventureEntry> getAdventures(DimContent dimContent, List<UUID> idsBySlot) {
        List<AdventureEntry> adventureEntries = new ArrayList<>(dimContent.getDimAdventures().getAdventureBlocks().size());
        for(DimAdventures.DimAdventureBlock adventure : dimContent.getDimAdventures().getAdventureBlocks()) {
            UUID bossId = idsBySlot.get(adventure.getBossStatsIndex());
            adventureEntries.add(AdventureEntry.builder()
                            .monsterId(bossId)
                            .steps(adventure.getSteps())
                            .bossDp(adventure.getBossDp())
                            .bossAp(adventure.getBossAp())
                            .bossHp(adventure.getBossHp())
                            .build());
        }
        return adventureEntries;
    }

    List<SpecificFusion> getSpecificFusions(DimContent dimContent, List<UUID> idsBySlot) {
        List<SpecificFusion> specificFusions = new ArrayList<>(dimContent.getDimSpecificFusion().getDimSpecificFusionBlocks().size());
        for(DimSpecificFusions.DimSpecificFusionBlock specificFusion : dimContent.getDimSpecificFusion().getDimSpecificFusionBlocks()) {
            specificFusions.add(SpecificFusion.builder()
                            .localMonsterId(idsBySlot.get(specificFusion.getStatsIndex()))
                            .evolveToMonsterId(idsBySlot.get(specificFusion.getStatsIndexForFusionResult()))
                            .partnerDimId(specificFusion.getFusionDimId())
                            .partnerDimSlotId(specificFusion.getFusionDimSlotId())
                            .build());
        }
        return specificFusions;
    }
}
