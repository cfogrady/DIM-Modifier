package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder(toBuilder = true)
@Data
public class AdventureEntry {
    private final int steps;
    private final UUID monsterId;
    private final int bossDp;
    private final int bossHp;
    private final int bossAp;
}
