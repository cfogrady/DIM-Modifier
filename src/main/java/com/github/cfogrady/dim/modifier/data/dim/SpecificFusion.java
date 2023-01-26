package com.github.cfogrady.dim.modifier.data.dim;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class SpecificFusion {
    UUID localMonsterId;
    UUID evolveToMonsterId;
    int partnerDimId;
    int partnerDimSlotId;
}
