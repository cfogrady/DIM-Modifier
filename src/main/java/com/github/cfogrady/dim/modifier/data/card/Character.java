package com.github.cfogrady.dim.modifier.data.card;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
public abstract class Character<T extends TransformationEntry, T2 extends Character<T, T2>> {
    private UUID id; //This is transient. Used to keep track of evolutions while slot indexes are being changed.
    private int stage;
    private int attribute;
    private int activityType;
    private int smallAttack;
    private int bigAttack;
    private int bp;
    private int hp;
    private int ap;
    private Integer firstPoolBattleChance;
    private Integer secondPoolBattleChance;
    private List<SpriteData.Sprite> sprites; // for dim, 6 and 7 for stages 0 and 1 respectively. Everything else 14
    private List<T> transformationEntries;
    private Fusions fusions;
    private List<SpecificFusion> specificFusions;

    public abstract T2 copyCharacter(SpriteImageTranslator spriteImageTranslator);

    public abstract boolean isSpriteSizeValid(SpriteData.SpriteDimensions spriteDimensions);

    public abstract List<SpriteData.SpriteDimensions> getValidDimensions();

    public boolean hasTransformations() {
        return !transformationEntries.isEmpty() || !fusions.isEmpty() || !specificFusions.isEmpty();
    }

}
