package com.github.cfogrady.dim.modifier.view;

import javafx.scene.Node;

import java.util.function.Consumer;

public class BemStatsView implements BemInfoView {

    public static class StatsViewState extends BemCharactersView.CharacterViewState {
    }

    public static StatsViewState fromCharacterViewState(BemCharactersView.CharacterViewState original) {
        StatsViewState newState = new StatsViewState();
        newState.copyFrom(original);
        return newState;
    }

    @Override
    public Node setupView(ViewState state, Consumer<ViewState> refresher) {
        //show stats
        return null;
    }
}
