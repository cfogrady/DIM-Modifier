package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class DimData {
    private final List<MonsterSlot> monsterSlotList;
    private final Map<UUID, MonsterSlot> monsterSlotsById;
    private final List<AdventureEntry> adventureEntries;
    private final List<SpecificFusion> specificFusions;
}
