package com.github.cfogrady.dim.modifier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SafetyValidator {
//    public boolean isValid(DimData dimData) {
//        for(AdventureEntry entry : dimData.getAdventureEntries()) {
//            if(!dimData.getMonsterSlotIndexById().containsKey(entry.getMonsterId())) {
//                log.info("Adventures refers to non-existent monster");
//                return false;
//            }
//        }
//        if(!areNonDigimonSpritesValid(dimData)) {
//            return false;
//        }
//        int slotIndex = 0;
//        int evolutions = 0;
//        for(MonsterSlot slot : dimData.getMonsterSlotList()) {
//            if(!isMonsterSlotValid(slot, slotIndex)) {
//                return false;
//            }
//            evolutions += slot.getEvolutionEntries().size();
//            slotIndex++;
//        }
//        if(slotIndex > DimStats.VB_TABLE_SIZE) {
//            log.info("Too many slots! DIM should have 17 or less.");
//            return false;
//        }
//        if(evolutions > DimEvolutionRequirements.VB_TABLE_SIZE) {
//            log.info("Too many evolutions! DIM should have 34 or less.");
//            return false;
//        }
//        if(dimData.getSpecificFusions().size() > 1) {
//            log.info("Too many specific fusions! DIM should have no more than 1");
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isMonsterSlotValid(MonsterSlot slot, int slotIndex) {
//        int level = slot.getStatBlock().getStage();
//        if(slotIndex < 2 && slotIndex != slot.getStatBlock().getStage()) {
//            log.info("First two slots must be stage 1 and 2 respectively.");
//            return false;
//        } else if(slotIndex >= 2 && slot.getStatBlock().getStage() < 2) {
//            log.info("Only first two slots can be stage 1 or 2.");
//            return false;
//        }
//        if(slot.getSprites().size() != DimDataFactory.getSpriteCountForLevel(level)) {
//            log.info("Not enough sprites for slot {}", slotIndex);
//            return false;
//        }
//        SpriteData.Sprite nameSprite = slot.getSprites().get(0);
//        if(nameSprite.getHeight() != 15 || nameSprite.getWidth() % 80 != 0) {
//            log.info("Name sprites must be multiple of 80 by 15");
//            return false;
//        }
//        int lastSprite = slot.getSprites().size();
//        if(level == 0) {
//            lastSprite -= 1;
//        } else {
//            SpriteData.Sprite bannerSprite = slot.getSprites().get(lastSprite-1);
//            if(bannerSprite.getWidth() != 80 || bannerSprite.getHeight() != 160) {
//                log.info("Banner sprites must be 80x160");
//                return false;
//            }
//        }
//        for(int i = 1; i < lastSprite-1; i++) {
//            if(!areDimensionsForLevelValid(level, slot.getSprites().get(i))) {
//                log.info("Monster sprite dimensions are bad for slot {} with sprite index {}", slotIndex, i);
//                return false;
//            }
//        }
//        DimStats.DimStatBlock stats = slot.getStatBlock();
//        if(level > 1) {
//            if(stats.getDpStars() > 10) {
//                log.info("Monster {} DP Stars must be 10 or less", slotIndex);
//                return false;
//            }
//            if(stats.getDp() > 75) {
//                log.info("Monster {} DP must be less than 75", slotIndex);
//                return false;
//            }
//            if(stats.getHp() > 22) {
//                log.info("Monster {} HP must be less than 22", slotIndex);
//                return false;
//            }
//            if(stats.getAp() > 9) {
//                log.info("Monster {} AP must be less than 9", slotIndex);
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean areDimensionsForLevelValid(int level, SpriteData.Sprite sprite) {
//        if(level < 2) {
//            return sprite.getWidth() == 32 && sprite.getHeight() == 24;
//        }
//        return sprite.getWidth() == 64 && sprite.getHeight() == 56;
//    }
//
//    private boolean areNonDigimonSpritesValid(DimData dimData) {
//        if(dimData.getLogoSprite().getWidth() != 42 || dimData.getLogoSprite().getHeight() != 42) {
//            log.info("Logo must be 42x42");
//            return false;
//        }
//        if(dimData.getBackGroundSprite().getWidth() != 80 || dimData.getBackGroundSprite().getHeight() != 160) {
//            log.info("Background must be 80x160");
//            return false;
//        }
//        for(SpriteData.Sprite eggSprite : dimData.getEggSprites()) {
//            if(eggSprite.getWidth() != 32 || eggSprite.getHeight() != 40) {
//                log.info("All eggs must 32x40");
//                return false;
//            }
//        }
//        return true;
//    }
}
