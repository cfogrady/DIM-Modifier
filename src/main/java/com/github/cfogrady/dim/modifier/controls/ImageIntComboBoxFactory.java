package com.github.cfogrady.dim.modifier.controls;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ImageIntComboBoxFactory {
    private final SpriteImageTranslator spriteImageTranslator;

    public ImageIntComboBox createImageIntComboBox(int currentValue, List<SpriteData.Sprite> sprites, Consumer<Integer> valueSetter) {
        return new ImageIntComboBox(currentValue, createImageValuePairs(sprites), valueSetter);
    }

    public ImageIntComboBox createImageIntComboBox(int currentValue, double scaler, List<SpriteData.Sprite> sprites, Consumer<Integer> valueSetter) {
        return new ImageIntComboBox(currentValue, createImageValuePairs(sprites), valueSetter, scaler);
    }

    private ObservableList<ImageIntComboBox.ImageIntPair> createImageValuePairs(List<SpriteData.Sprite> sprites) {
        ObservableList<ImageIntComboBox.ImageIntPair> items = FXCollections.observableArrayList();
        for(int i = 0; i < sprites.size(); i++) {
            SpriteData.Sprite sprite = sprites.get(i);
            Image image = spriteImageTranslator.loadImageFromSprite(sprite);
            items.add(new ImageIntComboBox.ImageIntPair(image, i));
        }
        return items;
    }
}
