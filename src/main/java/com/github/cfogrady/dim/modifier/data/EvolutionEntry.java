package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.reader.content.DimEvolutionRequirements;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class EvolutionEntry {
    private final DimEvolutionRequirements.DimEvolutionRequirementBlock evolutionRequirementBlock;
    private final UUID toMonster;
}
