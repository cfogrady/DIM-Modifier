package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.AdventureEntry;
import com.github.cfogrady.dim.modifier.data.DimData;
import com.github.cfogrady.dim.modifier.data.MonsterSlot;
import com.github.cfogrady.vb.dim.reader.content.DimStats;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SafetyValidator {
    public boolean isValid(DimData dimData) {
        for(AdventureEntry entry : dimData.getAdventureEntries()) {
            if(!dimData.getMonsterSlotIndexById().containsKey(entry.getMonsterId())) {
                log.info("Adventures refers to non-existent monster");
                return false;
            }
        }
        if(!areNonDigimonSpritesValid(dimData)) {
            return false;
        }
        int slotIndex = 0;
        for(MonsterSlot slot : dimData.getMonsterSlotList()) {
            if(!isMonsterSlotValid(slot, slotIndex)) {
                return false;
            }
            slotIndex++;
        }
        return true;
    }

    private boolean isMonsterSlotValid(MonsterSlot slot, int slotIndex) {
        int level = slot.getStatBlock().getStage();
        SpriteData.Sprite nameSprite = slot.getSprites().get(0);
        if(nameSprite.getHeight() != 15 || nameSprite.getWidth() % 80 != 0) {
            log.info("Name sprites must be multiple of 80 by 15");
            return false;
        }
        int lastSprite = slot.getSprites().size();
        if(level == 0) {
            lastSprite -= 1;
        } else {
            SpriteData.Sprite bannerSprite = slot.getSprites().get(lastSprite-1);
            if(bannerSprite.getWidth() != 80 || bannerSprite.getHeight() != 160) {
                log.info("Banner sprites must be 80x160");
                return false;
            }
        }
        for(int i = 1; i < lastSprite; i++) {
            if(!areDimensionsForLevelValid(level, slot.getSprites().get(i))) {
                log.info("Monster sprite dimensions are bad for slot {} with sprite index {}", slotIndex, i);
                return false;
            }
        }
        DimStats.DimStatBlock stats = slot.getStatBlock();
        if(stats.getStage() > 2) {
            if(stats.getDpStars() > 10) {
                log.info("Monster {} DP Stars must be 10 or less", slotIndex);
                return false;
            }
            if(stats.getDp() > 75) {
                log.info("Monster {} DP must be less than 75", slotIndex);
                return false;
            }
            if(stats.getAp() > 22) {
                log.info("Monster {} HP must be less than 22", slotIndex);
                return false;
            }
            if(stats.getHp() > 9) {
                log.info("Monster {} AP must be less than 9", slotIndex);
                return false;
            }
        }
        return true;
    }

    private boolean areDimensionsForLevelValid(int level, SpriteData.Sprite sprite) {
        if(level < 2) {
            return sprite.getWidth() == 32 && sprite.getHeight() == 24;
        }
        return sprite.getWidth() == 64 && sprite.getHeight() == 56;
    }

    private boolean areNonDigimonSpritesValid(DimData dimData) {
        if(dimData.getLogoSprite().getWidth() != 42 || dimData.getLogoSprite().getHeight() != 42) {
            log.info("Logo must be 42x42");
            return false;
        }
        if(dimData.getBackGroundSprite().getWidth() != 80 || dimData.getBackGroundSprite().getHeight() != 160) {
            log.info("Background must be 80x160");
            return false;
        }
        for(SpriteData.Sprite eggSprite : dimData.getEggSprites()) {
            if(eggSprite.getWidth() != 32 || eggSprite.getHeight() != 40) {
                log.info("All eggs must 32x40");
                return false;
            }
        }
        return true;
    }
}
