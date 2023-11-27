package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.SpriteData;

import java.util.List;

public abstract class SpriteArrayFirmware implements FirmwareData {
    private final SpriteData spriteData;

    protected SpriteArrayFirmware(SpriteData spriteData) {
        this.spriteData = spriteData;
    }

    protected abstract int getSmallAttackStartIdx();
    protected abstract int getSmallAttackEndIdx();
    protected abstract int getBigAttackStartIdx();
    protected abstract int getBigAttackEndIdx();
    protected abstract int getTypesStartIdx();
    protected abstract int getTypesEndIdx();

    public List<SpriteData.Sprite> getSmallAttacks() {
        return spriteData.getSprites().subList(getSmallAttackStartIdx(), getSmallAttackEndIdx() + 1);
    }

    public List<SpriteData.Sprite> getBigAttacks() {
        return spriteData.getSprites().subList(getBigAttackStartIdx(), getBigAttackEndIdx() + 1);
    }

    public List<SpriteData.Sprite> getTypes() {
        return spriteData.getSprites().subList(getTypesStartIdx(), getTypesEndIdx() + 1);
    }
}
