package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MonsterSlotWithAuthor {
    private final String author;
    private final MonsterSlot monsterSlot;
}
