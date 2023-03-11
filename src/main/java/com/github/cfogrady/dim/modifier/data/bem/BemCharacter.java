package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemCharacter extends Character<BemTransformationEntry, BemCharacter> {
    public static final SpriteData.SpriteDimensions NORMAL_SPRITE_DIMENSIONS = SpriteData.SpriteDimensions.builder().width(64).height(56).build();
    public static final SpriteData.SpriteDimensions BABY_SPRITE_DIMENSIONS = SpriteData.SpriteDimensions.builder().width(32).height(24).build();
    public static final List<SpriteData.SpriteDimensions> ALLOWED_DIMENSIONS = List.of(NORMAL_SPRITE_DIMENSIONS, BABY_SPRITE_DIMENSIONS);

    private Integer thirdPoolBattleChance;
    private Integer minutesUntilTransformation;

    @Override
    public BemCharacter copyCharacter(SpriteImageTranslator spriteImageTranslator) {
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(spriteImageTranslator.getBlankNameSprite());
        sprites.addAll(spriteImageTranslator.createDummySprites(12, spriteImageTranslator.getBlankCharacterSprite()));
        sprites.add(spriteImageTranslator.getBlankBackgroundSprite());
        return this.toBuilder()
                .id(UUID.randomUUID())
                .transformationEntries(new ArrayList<>())
                .specificFusions(new ArrayList<>())
                .sprites(sprites)
                .build();
    }

    @Override
    public boolean isSpriteSizeValid(SpriteData.SpriteDimensions spriteDimensions) {
        for(SpriteData.SpriteDimensions allowedSpriteDimension : ALLOWED_DIMENSIONS) {
            if(allowedSpriteDimension.equals(spriteDimensions)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SpriteData.SpriteDimensions> getValidDimensions() {
        return ALLOWED_DIMENSIONS;
    }
}
