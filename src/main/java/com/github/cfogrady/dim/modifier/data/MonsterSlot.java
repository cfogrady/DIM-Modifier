package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class MonsterSlot {
    private UUID id; //This is transient. Used to keep track of evolutions while slot indexes are being changed.
    private DimStats.DimStatBlock statBlock;
    private List<SpriteData.Sprite> sprites;
    private List<EvolutionEntry> evolutionEntries;
    private int hoursUntilEvolution;
    private Fusions fusions;
}
