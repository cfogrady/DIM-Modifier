package com.github.cfogrady.dim.modifier.controls;

import com.github.cfogrady.dim.modifier.SpriteImageTranslator;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ImageIntComboBoxFactory {
    private final SpriteImageTranslator spriteImageTranslator;

    public ImageIntComboBox createImageIntComboBox(int currentValue, List<SpriteData.Sprite> sprites, Consumer<Integer> valueSetter) {
        return new ImageIntComboBox(currentValue, createImageValuePairs(sprites), valueSetter);
    }

    private ObservableList<ImageIntComboBox.ImageIntPair> createImageValuePairs(List<SpriteData.Sprite> sprites) {
        ObservableList<ImageIntComboBox.ImageIntPair> items = FXCollections.observableArrayList();
        for(int i = 0; i < sprites.size(); i++) {
            SpriteData.Sprite sprite = sprites.get(i);
            Image image = spriteImageTranslator.loadImageFromSprite(sprite);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(sprite.getWidth() * 2.0);
            imageView.setFitHeight(sprite.getHeight() * 2.0);
            VBox.setMargin(imageView, new Insets(10));
            items.add(new ImageIntComboBox.ImageIntPair(imageView, i));
        }
        return items;
    }
}
