package com.github.cfogrady.dim.modifier.data.card;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
public class SpecificFusion {
    private UUID evolveToCharacterId;
    private UUID sameBemPartnerCharacter;
    private int partnerDimId;
    private Integer partnerDimSlotId;
}
