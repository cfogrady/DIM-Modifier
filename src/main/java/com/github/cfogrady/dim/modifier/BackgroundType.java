package com.github.cfogrady.dim.modifier;

public enum BackgroundType {
    IMAGE,
    BLUE,
    GREEN,
    ORANGE;

    public static BackgroundType nextBackground(BackgroundType backgroundType) {
        int nextOrdinal = (backgroundType.ordinal() + 1) % BackgroundType.values().length;
        return BackgroundType.values()[nextOrdinal];
    }
}
