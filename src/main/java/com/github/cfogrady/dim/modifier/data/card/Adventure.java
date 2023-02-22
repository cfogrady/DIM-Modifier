package com.github.cfogrady.dim.modifier.data.card;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
public class Adventure {
    private int steps;
    private UUID bossId;
    private int bossBp;
    private int bossHp;
    private int bossAp;
}
