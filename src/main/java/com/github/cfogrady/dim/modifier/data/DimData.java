package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class DimData {
    private List<MonsterSlot> monsterSlotList;
    private Map<UUID, MonsterSlot> monsterSlotsById;
    private List<AdventureEntry> adventureEntries;
    private List<SpecificFusion> specificFusions;
}
