package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.vb.dim.reader.content.DimContent;
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
public class SpriteSlotParser {
    private final DimContent dimContent;

    public WritableImage loadImageFromSpriteIndex(int index) {
        SpriteData.Sprite sprite = dimContent.getSpriteData().getSprites().get(index);
        return loadImageFromSprite(sprite);
    }

    public WritableImage loadImageFromSprite(SpriteData.Sprite sprite) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(sprite.getBGRA());
        PixelBuffer<ByteBuffer> pixelBuffer = new PixelBuffer<ByteBuffer>(sprite.getWidth(), sprite.getHeight(), byteBuffer, PixelFormat.getByteBgraPreInstance());

        return new WritableImage(pixelBuffer);
    }

    public WritableImage getImageForSlotAndIndex(CurrentSelectionType selectionType, int slot, int image) {
        return loadImageFromSpriteIndex(getImageIndex(selectionType, slot, image));
    }

    public SpriteData.Sprite getSpriteForSlotAndIndex(CurrentSelectionType selectionType, int slot, int image) {
        int spriteIndex = getImageIndex(selectionType, slot, image);
        return dimContent.getSpriteData().getSprites().get(spriteIndex);
    }

    public void loadReplacementSprite(File file, CurrentSelectionType selectionType, int slot, int imageIndex) throws IOException {
        int rawImageIndex = getImageIndex(selectionType, slot, imageIndex);
        loadReplacementSprite(file, rawImageIndex);
    }

    public void loadReplacementSprite(File file, int spriteIndex) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        Image image = new Image(inputStream);
        inputStream.close();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] pixelData = convertToR5G6B5(image.getPixelReader(), width, height);
        SpriteData.Sprite sprite = SpriteData.Sprite.builder().width(width).height(height).pixelData(pixelData).build();
        log.debug("Loading sprite at slot {}", spriteIndex);
        dimContent.getSpriteData().getSprites().set(spriteIndex, sprite);
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
                if(x == 0 && y == 0) {
                    log.info("Original Image - Red: {}, Green: {}, Blue: {}, Opacity: {}", color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
                    log.info("New Image - Red: {}, Green: {}, Blue: {}", red, green, blue);
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

    private int getImageIndex(CurrentSelectionType selectionType, int slot, int image) {
        if(selectionType == CurrentSelectionType.LOGO) {
            return 0;
        } else if(selectionType == CurrentSelectionType.EGG) {
            return image + 2; // logo + background
        } else {
            int index = 2 + 8; //logo+background + egg sprites
            for(int monSlot = 0; monSlot < slot; monSlot++) {
                int level = dimContent.getDimStats().getStatBlocks().get(monSlot).getStage();
                if(level == 0) {
                    index += 6;
                } else if(level == 1) {
                    index += 7;
                } else {
                    index += 14;
                }
            }
            return index + image;
        }
    }
}
