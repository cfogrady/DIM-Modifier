package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.SpriteData;

public class FirmwareData10b extends SpriteArrayFirmware {

    public static int BIG_ATTACK_START_INDEX = 288;
    public static int BIG_ATTACK_END_INDEX = 309;
    public static int SMALL_ATTACK_START_INDEX = 310;
    public static int SMALL_ATTACK_END_INDEX = 348; //Omit the test attack

    public FirmwareData10b(SpriteData spriteData) {
        super(spriteData);
    }

    @Override
    protected int getSmallAttackStartIdx() {
        return SMALL_ATTACK_START_INDEX;
    }

    @Override
    protected int getSmallAttackEndIdx() {
        return SMALL_ATTACK_END_INDEX;
    }

    @Override
    protected int getBigAttackStartIdx() {
        return BIG_ATTACK_START_INDEX;
    }

    @Override
    protected int getBigAttackEndIdx() {
        return BIG_ATTACK_END_INDEX;
    }

    @Override
    protected int getTypesStartIdx() {
        return 232;
    }

    @Override
    protected int getTypesEndIdx() {
        return 235;
    }
}
