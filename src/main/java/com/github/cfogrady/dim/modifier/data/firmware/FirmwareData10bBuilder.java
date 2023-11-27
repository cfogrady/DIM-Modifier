package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;

public class FirmwareData10bBuilder extends FirmwareDataBuilder {

    public static final int SPRITE_DIMENSIONS_LOCATION = 0x90a4;

    public FirmwareData10bBuilder(BemSpriteReader bemSpriteReader) {
        super(bemSpriteReader);
    }

    @Override
    protected int getSpriteDimensionsLocation() {
        return SPRITE_DIMENSIONS_LOCATION;
    }

    @Override
    protected FirmwareData buildFromSprites(SpriteData spriteData) {
        return new FirmwareData10b(spriteData);
    }
}
