package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.SpriteData;

public class FimwareData20a extends SpriteArrayFirmware {

    private static final int BIG_ATTACK_START_INDEX = 283;
    private static final int BIG_ATTACK_END_INDEX = 304;
    private static final int SMALL_ATTACK_START_INDEX = 305;
    private static final int SMALL_ATTACK_END_INDEX = 343; //Omit the test attack
    private static final int TYPES_START_INDEX = 232;
    private static final int TYPES_END_INDEX = 235;

    public FimwareData20a(SpriteData spriteData) {
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
        return TYPES_START_INDEX;
    }

    @Override
    protected int getTypesEndIdx() {
        return TYPES_END_INDEX;
    }
}
