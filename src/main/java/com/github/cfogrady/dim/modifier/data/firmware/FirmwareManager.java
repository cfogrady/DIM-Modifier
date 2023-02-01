package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.prefs.Preferences;

@RequiredArgsConstructor
@Slf4j
public class FirmwareManager {
    public static final String FIRMWARE_LOCATION = "FIRMWARE_LOCATION";

    public static final int SPRITE_DIMENSIONS_LOCATION = 0x90a4;

    public static final int SPRITE_PACKAGE_LOCATION = 0x80000;

    private final BemSpriteReader bemSpriteReader = new BemSpriteReader();
    private final Preferences preferences;

    public void setFirmwareLocation(File file) {
        preferences.put(FIRMWARE_LOCATION, file.getAbsolutePath());
    }

    public boolean isValidFirmwareLocationSet() {
        String firmwareFile = preferences.get(FIRMWARE_LOCATION, null);
        if(firmwareFile == null) {
            return false;
        }
        File file = new File(firmwareFile);
        log.info("What is happening. File: {}. Valid: {}", file.getAbsolutePath(), isValidFirmwareLocation(file));
        return isValidFirmwareLocation(file);
    }

    public static boolean isValidFirmwareLocation(File file) {
        log.info("File exists: {}", file.exists());
        return file != null && file.exists() && file.isFile() && file.canRead();
    }

    public FirmwareData loadFirmware() {
        String firmwareLocation = preferences.get(FIRMWARE_LOCATION, null);
        File file = new File(firmwareLocation);
        log.info("File: {}, exists: {}", file.getAbsolutePath(), file.exists());
        try (FileInputStream fileInput = new FileInputStream(file)) {
            RelativeByteOffsetInputStream input = new RelativeByteOffsetInputStream(fileInput);
            input.readToOffset(SPRITE_DIMENSIONS_LOCATION);
            List<SpriteData.SpriteDimensions> dimensionsList = bemSpriteReader.readSpriteDimensions(input);
            input.readToOffset(SPRITE_PACKAGE_LOCATION);
            return new FirmwareData(bemSpriteReader.getSpriteData(input, dimensionsList));
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
