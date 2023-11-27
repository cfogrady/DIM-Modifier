package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public abstract class FirmwareDataBuilder {

    // Make abstract in the future if necessary.
    public static final int SPRITE_PACKAGE_LOCATION = 0x80000;

    private final BemSpriteReader bemSpriteReader;

    public FirmwareDataBuilder(BemSpriteReader bemSpriteReader) {
        this.bemSpriteReader = bemSpriteReader;
    }

    public FirmwareData buildFirmwareData(File file) {
        try (FileInputStream fileInput = new FileInputStream(file)) {
            RelativeByteOffsetInputStream input = new RelativeByteOffsetInputStream(fileInput);
            input.readToOffset(getSpriteDimensionsLocation());
            List<SpriteData.SpriteDimensions> dimensionsList = bemSpriteReader.readSpriteDimensions(input);
            input.readToOffset(SPRITE_PACKAGE_LOCATION);
            return buildFromSprites(bemSpriteReader.getSpriteData(input, dimensionsList));
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    protected abstract int getSpriteDimensionsLocation();

    protected abstract FirmwareData buildFromSprites(SpriteData spriteData);
}
