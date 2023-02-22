package com.github.cfogrady.dim.modifier.data.firmware;

import com.github.cfogrady.vb.dim.sprite.SpriteData;

import java.util.ArrayList;
import java.util.List;

public class FirmwareData {
    public static int BACKGROUND_0_INDEX = 0;
    public static int BACKGROUND_1_INDEX = 6;
    public static int BACKGROUND_2_INDEX = 7;
    public static int[] BACKGROUND_INDEXES = {0, 6, 7};

    public static int BIG_ATTACK_START_INDEX = 288;
    public static int BIG_ATTACK_END_INDEX = 309;
    public static int SMALL_ATTACK_START_INDEX = 310;
    public static int SMALL_ATTACK_END_INDEX = 349;

    private final SpriteData spriteData;
    private final int[] bigAttackIndexes;
    private final int[] smallAttackIndexes;

    public FirmwareData(SpriteData spriteData) {
        this.spriteData = spriteData;
        bigAttackIndexes = new int[1 + BIG_ATTACK_END_INDEX-BIG_ATTACK_START_INDEX];
        populateFromStart(bigAttackIndexes, BIG_ATTACK_START_INDEX);
        smallAttackIndexes = new int[1 + SMALL_ATTACK_END_INDEX - SMALL_ATTACK_START_INDEX];
        populateFromStart(smallAttackIndexes, SMALL_ATTACK_START_INDEX);
    }

    private static void populateFromStart(int[] array, int startingValue) {
        for(int i = 0; i < array.length; i++) {
            array[i] = startingValue + i;
        }
    }

    public SpriteData.Sprite getBackground0() {
        return getSpriteByIndex(BACKGROUND_0_INDEX);
    }

    public SpriteData.Sprite getBackground1() {
        return getSpriteByIndex(BACKGROUND_1_INDEX);
    }

    public SpriteData.Sprite getBackground2() {
        return getSpriteByIndex(BACKGROUND_2_INDEX);
    }

    public SpriteData.Sprite getBackground(int backgroundIndex) {
        return getSpriteByIndex(BACKGROUND_INDEXES[backgroundIndex]);
    }

    public SpriteData.Sprite getSmallAttack(int attackIndex) {
        return getSpriteByIndex(smallAttackIndexes[attackIndex]);
    }

    public List<SpriteData.Sprite> getSmallAttacks() {
        List<SpriteData.Sprite> smallAttackSprites = new ArrayList<>(smallAttackIndexes.length);
        for(int index : smallAttackIndexes) {
            smallAttackSprites.add(getSpriteByIndex(index));
        }
        return smallAttackSprites;
    }

    public int getNumberOfSmallAttacks() {
        return smallAttackIndexes.length;
    }

    public SpriteData.Sprite getBigAttack(int attackIndex) {
        return getSpriteByIndex(bigAttackIndexes[attackIndex]);
    }

    public List<SpriteData.Sprite> getBigAttacks() {
        List<SpriteData.Sprite> bigAttackIndexes = new ArrayList<>(this.bigAttackIndexes.length);
        for(int index : this.bigAttackIndexes) {
            bigAttackIndexes.add(getSpriteByIndex(index));
        }
        return bigAttackIndexes;
    }

    public int getNumberOfBigAttacks() {
        return bigAttackIndexes.length;
    }

    public List<SpriteData.Sprite> getTypes() {
        return spriteData.getSprites().subList(232, 236);
    }

    public SpriteData.Sprite getSpriteByIndex(int index) {
        return spriteData.getSprites().get(index);
    }
}
