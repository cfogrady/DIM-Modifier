package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.view.InfoView;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Builder(toBuilder = true)
@Data
public class SelectionState {
    private InfoView currentView;
    private CurrentSelectionType selectionType;
    private int slot;
    private int spriteIndex;
    private BackgroundType backgroundType;
    private boolean safetyModeOn;
    private File lastFileOpenPath;

    public boolean isOnBabySlot() {
        return selectionType == CurrentSelectionType.SLOT && slot < 2;
    }

    public SpriteData.Sprite getSprite(DimData dimData) {
        if(getSelectionType() == CurrentSelectionType.LOGO) {
            return dimData.getLogoSprite();
        } else if(getSelectionType() == CurrentSelectionType.EGG) {
            return dimData.getEggSprites().get(getSpriteIndex());
        } else {
            return dimData.getMonsterSlotList().get(getSlot()).getSprites().get(getSpriteIndex());
        }
    }
}
