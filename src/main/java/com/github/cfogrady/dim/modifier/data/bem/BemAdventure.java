package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.data.card.Adventure;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BemAdventure extends Adventure {
    private boolean showBossIdentiy;
    private Integer smallAttackId;
    private Integer bigAttackId;
    private int walkingBackground;
    private int battleBackground;
    private UUID giftCharacter;
}
