package com.github.cfogrady.dim.modifier.view;

import javafx.scene.Node;

import java.util.function.Consumer;

public interface BemInfoView {
    Node setupView(ViewState state, Consumer<ViewState> refresher);
}
