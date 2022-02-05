package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder(toBuilder = true)
@Data
public class AdventureEntry {
    private int steps;
    private UUID monsterId;
    private int bossDp;
    private int bossHp;
    private int bossAp;
}
