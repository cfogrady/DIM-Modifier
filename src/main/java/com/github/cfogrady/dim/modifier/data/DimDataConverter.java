package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.reader.content.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DimDataConverter {
    private static final int BABY_I_SPRITE_COUNT = 6;
    private static final int BABY_II_SPRITE_COUNT = 7;
    private static final int NORMAL_SPRITE_COUNT = 14;

    private static DimData fromDimContent(DimContent content) {
        int numberOfSlots = content.getDimStats().getStatBlocks().size();
        List<MonsterSlot> monsterSlotList = new ArrayList<>(numberOfSlots);
        List<UUID> idBySlot = IntStream.range(0, numberOfSlots).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toList());
        List<List<SpriteData.Sprite>> spritesBySlot = getSpritesForSlots(content);
        List<List<EvolutionEntry>> evolutionsBySlot = getEvolutionsForSlots(content, idBySlot);
        List<Fusions> fusionsBySlot = getFusionsForSlots(content, idBySlot);
        int slotIndex = 0;
        for(DimStats.DimStatBlock statsBlock : content.getDimStats().getStatBlocks()) {
            MonsterSlot monsterSlot = MonsterSlot.builder()
                    .id(idBySlot.get(slotIndex))
                    .statBlock(statsBlock)
                    .sprites(spritesBySlot.get(slotIndex))
                    .evolutionEntries(evolutionsBySlot.get(slotIndex))
                    .fusions(fusionsBySlot.get(slotIndex))
                    .build();
            monsterSlotList.add(monsterSlot);
            slotIndex++;
        }
        return DimData.builder().monsterSlotList(monsterSlotList).build();
    }

    /**
     * Returns a list of list of sprites. The first list is corresponds to each slot. The second list corresponds to all
     * the sprites for that slot.
     * @param dimContent
     * @return
     */
    private static List<List<SpriteData.Sprite>> getSpritesForSlots(DimContent dimContent) {
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

    private static int getSpriteCountForLevel(int level) {
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
    private static List<List<EvolutionEntry>> getEvolutionsForSlots(DimContent dimContent, List<UUID> idsBySlot) {
        List<List<EvolutionEntry>> evolutionsBySlot = new ArrayList<>(idsBySlot.size());
        for(int i = 0; i < idsBySlot.size(); i++) {
            evolutionsBySlot.add(new ArrayList<>());
        }
        for(DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirement : dimContent.getDimEvolutionRequirements().getEvolutionRequirementBlocks()) {
            int slot = evolutionRequirement.getEvolveFromStatIndex();
            UUID toMonsterId = idsBySlot.get(evolutionRequirement.getEvolveToStatIndex());
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
    private static List<Fusions> getFusionsForSlots(DimContent dimContent, List<UUID> idsBySlot) {
        List<Fusions> fusionsBySlot = new ArrayList<>(idsBySlot.size());
        for(int i = 0; i < idsBySlot.size(); i++) {
            fusionsBySlot.add(null);
        }
        for(DimFusions.DimFusionBlock fusionBlock : dimContent.getDimFusions().getFusionBlocks()) {
            Fusions fusions = Fusions.builder()
                    .type1FusionResult(idsBySlot.get(fusionBlock.getStatsIndexForFusionWithType1()))
                    .type2FusionResult(idsBySlot.get(fusionBlock.getStatsIndexForFusionWithType2()))
                    .type3FusionResult(idsBySlot.get(fusionBlock.getStatsIndexForFusionWithType3()))
                    .type4FusionResult(idsBySlot.get(fusionBlock.getStatsIndexForFusionWithType4()))
                    .build();
            fusionsBySlot.set(fusionBlock.getStatsIndex(), fusions);
        }
        return fusionsBySlot;
    }
}
