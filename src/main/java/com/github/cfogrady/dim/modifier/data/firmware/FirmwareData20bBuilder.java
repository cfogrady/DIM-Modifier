package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;

public class FirmwareData20bBuilder extends FirmwareDataBuilder {
    public static final int SPRITE_DIMENSIONS_LOCATION = 0x9d62;

    public FirmwareData20bBuilder(BemSpriteReader bemSpriteReader) {
        super(bemSpriteReader);
    }

    @Override
    protected int getSpriteDimensionsLocation() {
        return SPRITE_DIMENSIONS_LOCATION;
    }

    @Override
    protected FirmwareData buildFromSprites(SpriteData spriteData) {
        return new FimwareData20a(spriteData);
    }
}
