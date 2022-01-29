package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class DimData {
    private final List<MonsterSlot> monsterSlotList;
}
