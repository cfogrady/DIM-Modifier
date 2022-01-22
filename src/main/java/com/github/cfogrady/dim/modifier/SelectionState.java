package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.view.InfoView;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class SelectionState {
    private final InfoView currentView;
    private final CurrentSelectionType selectionType;
    private final int slot;
    private final int spriteIndex;
    private final BackgroundType backgroundType;
}
