package com.github.cfogrady.dim.modifier.data.dim;

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
public class DimCharacter extends Character<DimTransformationEntity, DimCharacter> {
    public static final SpriteData.SpriteDimensions ALLOWED_ADULT_DIMENSIONS =
            SpriteData.SpriteDimensions.builder().width(64).height(56).build();

    public static final SpriteData.SpriteDimensions ALLOWED_BABY_DIMENSIONS =
            SpriteData.SpriteDimensions.builder().width(32).height(24).build();

    private Integer hoursUntilFusionCheck;
    private int stars;
    private boolean finishAdventureToUnlock;

    @Override
    public DimCharacter copyCharacter(SpriteImageTranslator spriteImageTranslator) {
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(spriteImageTranslator.getBlankNameSprite());
        if(getStage() < 2) {
            sprites.addAll(spriteImageTranslator.createDummySprites(5, spriteImageTranslator.getBlankBabyCharacterSprite()));
        } else {
            sprites.addAll(spriteImageTranslator.createDummySprites(12, spriteImageTranslator.getBlankCharacterSprite()));
        }
        if(getStage() > 0) {
            sprites.add(spriteImageTranslator.getBlankBackgroundSprite());
        }
        return this.toBuilder()
                .id(UUID.randomUUID())
                .transformationEntries(new ArrayList<>())
                .specificFusions(new ArrayList<>())
                .sprites(sprites)
                .build();
    }

    @Override
    public boolean isSpriteSizeValid(SpriteData.SpriteDimensions spriteDimensions) {
        if(getStage() < 2) {
            return spriteDimensions.equals(ALLOWED_BABY_DIMENSIONS);
        } else {
            return spriteDimensions.equals(ALLOWED_ADULT_DIMENSIONS);
        }
    }

    @Override
    public List<SpriteData.SpriteDimensions> getValidDimensions() {
        if(getStage() < 2) {
            return List.of(ALLOWED_BABY_DIMENSIONS);
        } else {
            return List.of(ALLOWED_ADULT_DIMENSIONS);
        }
    }

    public void handleSpriteChange(int oldValue, int newValue, SpriteImageTranslator spriteImageTranslator) {
        if(oldValue < 2 && newValue < 2) {
            if(newValue == 0) {
                getSprites().remove(getSprites().size()-1);
            } else {
                getSprites().add(spriteImageTranslator.getBlankBackgroundSprite());
            }
        } else {
            List<SpriteData.Sprite> newSprites = new ArrayList<>();
            newSprites.add(getSprites().get(0));
            if(newValue < 2) {
                newSprites.addAll(spriteImageTranslator.createDummySprites(5, spriteImageTranslator.getBlankBabyCharacterSprite()));
            } else {
                newSprites.addAll(spriteImageTranslator.createDummySprites(12, spriteImageTranslator.getBlankCharacterSprite()));
            }
            if(newValue > 0) {
                newSprites.add(spriteImageTranslator.getBlankBackgroundSprite());
            }
            setSprites(newSprites);
        }
    }
}
