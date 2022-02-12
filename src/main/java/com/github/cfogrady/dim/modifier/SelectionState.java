package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.view.InfoView;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Builder(toBuilder = true)
@Data
public class SelectionState {
    private InfoView currentView;
    private CurrentSelectionType selectionType;
    private int slot;
    private int spriteIndex;
    private BackgroundType backgroundType;
    private boolean safetyModeOn;
    private File lastFileOpenPath;

    public boolean isOnBabySlot() {
        return selectionType == CurrentSelectionType.SLOT && slot < 2;
    }
}
