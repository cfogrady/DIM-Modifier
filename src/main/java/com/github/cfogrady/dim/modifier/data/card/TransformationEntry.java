package com.github.cfogrady.dim.modifier.data.card;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
public class TransformationEntry {
    private UUID toCharacter;
    private int vitalRequirements;
    private int trophyRequirement;
    private int battleRequirement;
    private int winRatioRequirement;
    private int timeToTransform;
}
