package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SpriteImageTranslator {
    @Getter
    private final SpriteData.Sprite blankCharacterSprite;
    @Getter
    private final SpriteData.Sprite blankBabyCharacterSprite;
    @Getter
    private final SpriteData.Sprite blankNameSprite;
    @Getter
    private final SpriteData.Sprite blankBackgroundSprite;

    public List<SpriteData.Sprite> createDummySprites(int number, SpriteData.Sprite sprite) {
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        for(int i= 0; i < number; i++) {
            sprites.add(sprite);
        }
        return sprites;
    }

    public SpriteImageTranslator() {
        blankCharacterSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankCharacter.png"));
        blankBabyCharacterSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankBabyCharacter.png"));
        blankNameSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankName.png"));
        blankBackgroundSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankBackground.png"));
    }

    public ObservableList<ImageIntComboBox.ImageIntPair> createImageValuePairs(List<SpriteData.Sprite> sprites) {
        ObservableList<ImageIntComboBox.ImageIntPair> items = FXCollections.observableArrayList();
        for(int i = 0; i < sprites.size(); i++) {
            SpriteData.Sprite sprite = sprites.get(i);
            Image image = loadImageFromSprite(sprite);
            items.add(new ImageIntComboBox.ImageIntPair(image, i));
        }
        return items;
    }

    public WritableImage loadImageFromSprite(SpriteData.Sprite sprite) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(sprite.getBGRA());
        PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<ByteBuffer>(sprite.getWidth(), sprite.getHeight(), byteBuffer, PixelFormat.getByteBgraPreInstance());

        return new WritableImage(pixelBuffer);
    }

    public SpriteData.Sprite loadSprite(File file) {
        try(FileInputStream inputStream = new FileInputStream(file)) {
            return loadSprite(inputStream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private SpriteData.Sprite loadSprite(InputStream inputStream) {
        Image image = new Image(inputStream);
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] pixelData = convertToR5G6B5(image.getPixelReader(), width, height);
        SpriteData.Sprite sprite = SpriteData.Sprite.builder().width(width).height(height).pixelData(pixelData).build();
        return sprite;
    }

    private byte[] convertToR5G6B5(PixelReader pixelReader, int width, int height) {
        byte[] bytes = new byte[width*height*2];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                int red;
                int green;
                int blue;
                if(color.getOpacity() == 0.0) {
                    red = 0;
                    blue = 0;
                    green = 63;
                } else {
                    red = (int) Math.floor(color.getRed() * 31.0);
                    green = (int) Math.floor(color.getGreen() * 63.0);
                    blue = (int) Math.floor(color.getBlue() * 31.0);
                }
                // RRRRRGGG GGGBBBBB
                byte byte0 = (byte) (((red & 0xFF) << 3) | ((green & 0xFF) >> 3));
                byte byte1 = (byte) (((green & 0xFF) << 5) | (blue & 0xFF));
                int index = (y * width + x) * 2;
                bytes[index] = byte1;
                bytes[index + 1] = byte0;
            }
        }
        return bytes;
    }
}
