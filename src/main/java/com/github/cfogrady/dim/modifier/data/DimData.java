package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder(toBuilder = true)
public class DimData {
    private List<MonsterSlot> monsterSlotList;
    private Map<UUID, Integer> monsterSlotIndexById;
    private List<AdventureEntry> adventureEntries;
    private List<SpecificFusion> specificFusions;
    private SpriteData.Sprite logoSprite;
    private SpriteData.Sprite backGroundSprite;
    private List<SpriteData.Sprite> eggSprites;

    public int getMonsterSlotIndexForId(UUID id) {
        return monsterSlotIndexById.get(id);
    }

    public MonsterSlot getMonsterSlotForId(UUID id) {
        return monsterSlotList.get(getMonsterSlotIndexForId(id));
    }

    public UUID getFirstMonsterIdForLevel(int level) {
        for(MonsterSlot monsterSlot : monsterSlotList) {
            if(monsterSlot.getStatBlock().getStage() == level) {
                return monsterSlot.getId();
            }
        }
        return null;
    }

    public SpriteData.Sprite getMosnterSprite(UUID id, int spriteIndex) {
        return getMonsterSlotForId(id).getSprites().get(spriteIndex);
    }

    public void deleteEntry(UUID id) {
        for(AdventureEntry adventureEntry : adventureEntries) {
            if(id.equals(adventureEntry.getMonsterId())) {
                adventureEntry.setMonsterId(null);
            }
        }
        specificFusions = specificFusions.stream().filter(fusion -> !id.equals(fusion.getLocalMonsterId()) || !id.equals(fusion.getEvolveToMonsterId())).collect(Collectors.toList());
        for(MonsterSlot monsterSlot : monsterSlotList) {
            Fusions fusions = monsterSlot.getFusions();
            if(fusions != null) {
                if(id.equals(fusions.getType1FusionResult())) {
                    fusions.setType1FusionResult(null);
                }
                if(id.equals(fusions.getType2FusionResult())) {
                    fusions.setType2FusionResult(null);
                }
                if(id.equals(fusions.getType3FusionResult())) {
                    fusions.setType3FusionResult(null);
                }
                if(id.equals(fusions.getType4FusionResult())) {
                    fusions.setType4FusionResult(null);
                }
            }
            monsterSlot.setEvolutionEntries(monsterSlot.getEvolutionEntries().stream().filter(evolutionEntry -> !id.equals(evolutionEntry.getToMonster())).collect(Collectors.toList()));
        }

        monsterSlotList = monsterSlotList.stream().filter(monsterSlot -> !id.equals(monsterSlot.getId())).collect(Collectors.toList());

        monsterSlotIndexById.remove(id);
        recalculateSlotMapping();
    }

    public String getSlotIndexAsStringForId(UUID id, String defaultValue) {
        return id == null ? defaultValue : Integer.toString(getMonsterSlotIndexForId(id));
    }

    void recalculateSlotMapping() {
        monsterSlotIndexById.clear();
        int slotIndex = 0;
        for(MonsterSlot slot : monsterSlotList) {
            monsterSlotIndexById.put(slot.getId(), slotIndex);
            slotIndex++;
        }
    }

    public UUID addEntry(int index) {
        MonsterSlot newMonsterSlot = addMonsterSlot();
        return addEntry(newMonsterSlot, index);
    }

    public UUID addEntry(MonsterSlot monster, int index) {
        List<MonsterSlot> newMonsterSlotList = new ArrayList<>(monsterSlotList.size() + 1);
        int currentIndex = 0;
        for(MonsterSlot monsterSlot : monsterSlotList) {
            if(currentIndex == index) {
                newMonsterSlotList.add(monster);
            }
            newMonsterSlotList.add(monsterSlot);
            currentIndex++;
        }
        monsterSlotList = newMonsterSlotList;
        recalculateSlotMapping();
        return monster.getId();
    }

    private MonsterSlot addMonsterSlot() {
        DimStats.DimStatBlock dimStatBlock = DimStats.DimStatBlock.builder()
                .stage(0)
                .attribute(0)
                .disposition(2)
                .unlockRequired(false)
                .dp(LoadedScene.NONE_VALUE)
                .dpStars(LoadedScene.NONE_VALUE)
                .hp(LoadedScene.NONE_VALUE)
                .ap(LoadedScene.NONE_VALUE)
                .smallAttackId(LoadedScene.NONE_VALUE)
                .bigAttackId(LoadedScene.NONE_VALUE)
                .firstPoolBattleChance(LoadedScene.NONE_VALUE)
                .secondPoolBattleChance(LoadedScene.NONE_VALUE)
                .build();
        return MonsterSlot.builder()
                .hoursUntilEvolution(LoadedScene.NONE_VALUE)
                .id(UUID.randomUUID())
                .sprites(new ArrayList<>())
                .evolutionEntries(List.of(setupEmptyEvolution()))
                .statBlock(dimStatBlock)
                .build();
    }

    public static EvolutionEntry setupEmptyEvolution() {
        return EvolutionEntry.builder()
                .evolutionRequirementBlock(DimEvolutionRequirements.DimEvolutionRequirementBlock.builder()
                        .hoursUntilEvolution(LoadedScene.NONE_VALUE)
                        .evolveToStatIndex(LoadedScene.NONE_VALUE)
                        .winRatioRequirement(LoadedScene.NONE_VALUE)
                        .vitalRequirements(LoadedScene.NONE_VALUE)
                        .battleRequirement(LoadedScene.NONE_VALUE)
                        .trophyRequirement(LoadedScene.NONE_VALUE)
                        .build())
                .build();
    }
}
