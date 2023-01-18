package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class EvolutionEntry {
    private DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock;
    private UUID toMonster;
}
