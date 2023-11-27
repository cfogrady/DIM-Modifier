package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.BemSpriteReader;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.util.RelativeByteOffsetInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

@RequiredArgsConstructor
@Slf4j
public class FirmwareManager {
    public static final String FIRMWARE_LOCATION = "FIRMWARE_LOCATION";

    private final FirmwareData10bBuilder firmwareData10bBuilder;
    private final FirmwareData20bBuilder firmwareData20bBuilder;
    private final Preferences preferences;

    public void setFirmwareLocation(File file) {
        preferences.put(FIRMWARE_LOCATION, file.getAbsolutePath());
    }

    public void clearFirmwareLocation() {
        preferences.put(FIRMWARE_LOCATION, null);
    }

    public boolean isValidFirmwareLocationSet() {
        String firmwareFile = preferences.get(FIRMWARE_LOCATION, null);
        if(firmwareFile == null) {
            return false;
        }
        File file = new File(firmwareFile);
        return isValidFirmwareLocation(file);
    }

    public static boolean isValidFirmwareLocation(File file) {
        return file != null && file.exists() && file.isFile() && file.canRead();
    }

    public FirmwareData loadFirmware() {
        String firmwareLocation = preferences.get(FIRMWARE_LOCATION, null);
        File file = new File(firmwareLocation);
        if(file.getName().endsWith("VBBE_20A.vb2")) {
            log.info("Using 2.0.A firmware");
            return firmwareData20bBuilder.buildFirmwareData(file);
        } else if(file.getName().endsWith("VBBE_10B.vb2")) {
            log.info("Using 1.0.B firmware");
            return firmwareData10bBuilder.buildFirmwareData(file);
        } else {
            throw new IllegalArgumentException("Expecting VBBE_10B.vb2 or VBBE_20A.vb2 firmware file.");
        }
    }
}
