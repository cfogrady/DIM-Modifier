package com.github.cfogrady.dim.modifier;

public enum CurrentSelectionType {
    LOGO,
    EGG,
    SLOT;

    public static int getNumberOfSprites(CurrentSelectionType selectionType, int levelIfApplicable) {
        switch(selectionType) {
            case LOGO:
                return 1;
            case EGG:
                return 8;
            case SLOT:
                if(levelIfApplicable == 0) {
                    return 6;
                } else if(levelIfApplicable == 1) {
                    return 7;
                } else {
                    return 14;
                }
            default:
                throw new IllegalArgumentException("Invalid CurrentSelectionType");
        }
    }
}
