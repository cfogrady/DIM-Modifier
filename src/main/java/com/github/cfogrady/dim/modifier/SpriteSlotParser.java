package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.vb.dim.reader.content.DimContent;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
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
