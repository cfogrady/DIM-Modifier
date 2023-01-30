package com.github.cfogrady.dim.modifier.view;

import com.github.cfogrady.dim.modifier.data.AppState;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class BemEvolutionsView implements BemInfoView {
    private final AppState appState;

    public static class EvolutionsViewState extends BemCharactersView.CharacterViewState {
    }

    public static EvolutionsViewState fromCharacterViewState(BemCharactersView.CharacterViewState original) {
        EvolutionsViewState newState = new EvolutionsViewState();
        newState.copyFrom(original);
        return newState;
    }

    @Override
    public Node setupView(ViewState state, Consumer<ViewState> refresher) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().add(new TextField("Evolutions"));
//        HBox hbox = new HBox();
//        vbox.getChildren().add(hbox);
//        hbox.getChildren().add(setupSpriteArea(selectionState));
//        hbox.getChildren().add(setupStatArea(selectionState));
        return vbox;
    }

    void resetView() {

    }
}
