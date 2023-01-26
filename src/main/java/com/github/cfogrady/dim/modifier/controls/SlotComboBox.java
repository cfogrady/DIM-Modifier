package com.github.cfogrady.dim.modifier.controls;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.data.dim.DimData;
import com.github.cfogrady.dim.modifier.data.dim.MonsterSlot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public class SlotComboBox extends ComboBox<SlotComboBox.StringUUIDLabel> {
    public SlotComboBox(DimData dimData, UUID currentValue, Runnable sceneRefresher, Consumer<UUID> valueSetter) {
        this(dimData, currentValue, true, LoadedScene.NONE_LABEL, 0, sceneRefresher, valueSetter);
    }

    public SlotComboBox(DimData dimData, UUID currentValue, String noneValue, Runnable sceneRefresher, Consumer<UUID> valueSetter) {
        this(dimData, currentValue, true, noneValue, 0, sceneRefresher, valueSetter);
    }

    public SlotComboBox(DimData dimData, UUID currentValue, boolean includeNone, Runnable sceneRefresher, Consumer<UUID> valueSetter) {
        this(dimData, currentValue, includeNone, LoadedScene.NONE_LABEL, 0, sceneRefresher, valueSetter);
    }

    public SlotComboBox(DimData dimData, UUID currentValue, boolean includeNone, String noneValue, int minLevel, Runnable sceneRefresher, Consumer<UUID> valueSetter) {
        super();
        ObservableList<StringUUIDLabel> items = FXCollections.observableArrayList(getComboBoxLabelsForSlots(dimData, includeNone, noneValue, minLevel));
        this.setItems(items);
        this.setValue(getItemForValue(currentValue));
        this.setOnAction(e -> {
            UUID newValue = this.getValue().getValue();
            valueSetter.accept(newValue);
            sceneRefresher.run();
        });
        this.setCellFactory(lv -> new StringUUIDCell());
        this.setButtonCell(new StringUUIDCell());
    }

    private StringUUIDLabel getItemForValue(UUID uuid) {
        if(uuid == null) {
            return getItems().get(getItems().size()-1);
        }
        for(StringUUIDLabel label : getItems()) {
            if(uuid.equals(label.getValue())) {
                return label;
            }
        }
        return null;
    }

    @Data
    @Builder
    public static class StringUUIDLabel {
        private final String label;
        private final UUID value;
    }

    public static class StringUUIDCell extends ListCell<StringUUIDLabel> {
        @Override
        protected void updateItem(StringUUIDLabel label, boolean empty) {
            super.updateItem(label, empty) ;
            if (empty) {
                setText(null);
            } else {
                setText(label.getLabel());
            }
        }
    }

    Collection<StringUUIDLabel> getComboBoxLabelsForSlots(DimData dimData, boolean includeNone, String noneValue, int minLevel) {
        ArrayList<StringUUIDLabel> list = new ArrayList<>(dimData.getMonsterSlotList().size());
        int index = 0;
        for(MonsterSlot monsterSlot : dimData.getMonsterSlotList()) {
            if(monsterSlot.getStatBlock().getStage() >= minLevel) {
                list.add(StringUUIDLabel.builder().label(Integer.toString(index)).value(monsterSlot.getId()).build());
            }
            index++;
        }
        if(includeNone) {
            list.add(StringUUIDLabel.builder().label(noneValue).value(null).build());
        }
        return list;
    }
}
