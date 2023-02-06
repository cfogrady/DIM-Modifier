package com.github.cfogrady.dim.modifier.data.bem;

import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CardSprites {
    SpriteData.Sprite logo;
    List<SpriteData.Sprite> backgrounds;
    List<SpriteData.Sprite> egg;
    SpriteData.Sprite ready; // training
    SpriteData.Sprite go; // training
    SpriteData.Sprite win; // battle
    SpriteData.Sprite lose; // battle
    List<SpriteData.Sprite> hits;
    List<SpriteData.Sprite> types;
    List<SpriteData.Sprite> stages;
    List<SpriteData.Sprite> smallAttacks;
    List<SpriteData.Sprite> bigAttacks;

    public SpriteData.Sprite getDisplayBackground() {
        return backgrounds.get(0);
    }

    public SpriteData.Sprite getBattleBackground() { return backgrounds.get(1); }

    public List<SpriteData.Sprite> getGroupedAdventureBackgrounds() {return backgrounds.subList(2, backgrounds.size()); }

}
