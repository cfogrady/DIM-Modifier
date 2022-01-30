package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.reader.content.DimStats;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class MonsterSlot {
    private final UUID id; //This is transient. Used to keep track of evolutions while slot indexes are being changed.
    private final DimStats.DimStatBlock statBlock;
    private final List<SpriteData.Sprite> sprites;
    private final List<EvolutionEntry> evolutionEntries;
    private final Fusions fusions;
}
