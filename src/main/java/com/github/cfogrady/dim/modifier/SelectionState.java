package com.github.cfogrady.dim.modifier;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class SelectionState {
    private final CurrentSelectionType selectionType;
    private final int slot;
    private final int spriteIndex;
    private final BackgroundType backgroundType;
}
