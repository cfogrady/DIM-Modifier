package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    public SpriteData.Sprite getMosnterSprite(UUID id, int spriteIndex) {
        return getMonsterSlotForId(id).getSprites().get(spriteIndex);
    }

    public void deleteEntry(UUID id) {
        for(AdventureEntry adventureEntry : adventureEntries) {
            if(id.equals(adventureEntry.getMonsterId())) {
                adventureEntry.setMonsterId(null);
            }
        }
        specificFusions = specificFusions.stream().filter(fusion -> id.equals(fusion.getLocalMonsterId()) && id.equals(fusion.getEvolveToMonsterId())).collect(Collectors.toList());
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
            monsterSlot.setEvolutionEntries(monsterSlot.getEvolutionEntries().stream().filter(evolutionEntry -> id.equals(evolutionEntry.getToMonster())).collect(Collectors.toList()));
        }

        monsterSlotList = monsterSlotList.stream().filter(monsterSlot -> id.equals(monsterSlot.getId())).collect(Collectors.toList());

        monsterSlotIndexById.remove(id);
    }

    public UUID addEntry() {
        return null;
    }
}
