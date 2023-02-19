package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BemCharacter extends Character<BemTransformationEntry, BemCharacter> {
    private Integer thirdPoolBattleChance;
    private Integer minutesUntilTransformation;

    @Override
    public BemCharacter copyCharacter(SpriteImageTranslator spriteImageTranslator) {
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(spriteImageTranslator.getBlankNameSprite());
        sprites.addAll(spriteImageTranslator.createDummySprites(12, spriteImageTranslator.getBlankCharacterSprite()));
        sprites.add(spriteImageTranslator.getBlankBackgroundSprite());
        return this.toBuilder()
                .transformationEntries(new ArrayList<>())
                .specificFusions(new ArrayList<>())
                .sprites(sprites)
                .build();
    }
}
