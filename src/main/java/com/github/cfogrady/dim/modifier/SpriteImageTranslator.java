package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@RequiredArgsConstructor
@Slf4j
public class SpriteImageTranslator {

    public WritableImage loadImageFromSprite(SpriteData.Sprite sprite) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(sprite.getBGRA());
        PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<ByteBuffer>(sprite.getWidth(), sprite.getHeight(), byteBuffer, PixelFormat.getByteBgraPreInstance());

        return new WritableImage(pixelBuffer);
    }

    public SpriteData.Sprite loadSprite(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        Image image = new Image(inputStream);
        inputStream.close();
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
