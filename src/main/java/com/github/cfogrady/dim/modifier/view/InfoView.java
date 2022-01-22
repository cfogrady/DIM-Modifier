package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.SelectionState;
import javafx.scene.Node;

public interface InfoView {
    Node setupView(SelectionState selectionState);

    double getPrefWidth();
}
