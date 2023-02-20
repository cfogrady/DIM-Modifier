package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.controls.ImageIntComboBox;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SpriteImageTranslator {
    private final AppState appState;
    private final Stage stage;

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

    public SpriteImageTranslator(AppState appState, Stage stage) {
        blankCharacterSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankCharacter.png"));
        blankBabyCharacterSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankBabyCharacter.png"));
        blankNameSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankName.png"));
        blankBackgroundSprite = loadSprite(this.getClass().getClassLoader().getResourceAsStream("BlankBackground.png"));
        this.appState = appState;
        this.stage = stage;
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

    public void importSpriteSheet(Character<?, ?> character) {

    }

    public void exportCharacterSpriteSheet(Character<?, ?> character) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save spritesheet as:");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image format", "*.png"));
        if(appState.getLastOpenedFilePath() != null) {
            fileChooser.setInitialDirectory(appState.getLastOpenedFilePath());
        }
        File file = fileChooser.showSaveDialog(stage);
        if(file != null) {
            List<SpriteData.Sprite> sprites = character.getSprites();
            if(sprites.size() == 14) {
                exportCharacterSpriteSheet(file, sprites.subList(1, sprites.size()));
            } else {
                exportBabySpriteSheet(file, character);
            }
        }
    }

    private void exportBabySpriteSheet(File file, Character<?, ?> character) {
        if(character.getSprites().size() > 7) {
            throw new IllegalArgumentException("Meant only for baby characters");
        }
        List<BufferedImage> images = new ArrayList<>();
        for(SpriteData.Sprite sprite : character.getSprites()) {
            images.add(createBufferedImage(sprite));
        }
        BufferedImage backgroundImage = createPinkBackground();
        Graphics background = backgroundImage.getGraphics();
        addBlanks(background);
        drawNormalSpriteImage(images.get(1), background, 1, 1);
        drawNormalSpriteImage(images.get(2), background, 66, 1);
        drawNormalSpriteImage(images.get(3), background, 131, 1);
        drawNormalSpriteImage(images.get(4), background, 1, 115);
        drawNormalSpriteImage(images.get(5), background, 66, 115);
        if(character.getSprites().size() == 7) {
            background.drawImage(images.get(6), 261, 1, null);
        }
        writeImageToFile(backgroundImage, file);
    }

    private void addBlanks(Graphics background) {
        BufferedImage blank = createBlankSprite();
        drawNormalSpriteImage(blank, background, 1, 1);
        drawNormalSpriteImage(blank, background, 66, 1);
        drawNormalSpriteImage(blank, background, 131, 1);
        drawNormalSpriteImage(blank, background, 196, 1);
        drawNormalSpriteImage(blank, background, 1, 58);
        drawNormalSpriteImage(blank, background, 66, 58);
        drawNormalSpriteImage(blank, background, 131, 58);
        drawNormalSpriteImage(blank, background, 196, 58);
        drawNormalSpriteImage(blank, background, 1, 115);
        drawNormalSpriteImage(blank, background, 66, 115);
        drawNormalSpriteImage(blank, background, 131, 115);
        drawNormalSpriteImage(blank, background, 196, 115);
    }

    private void exportCharacterSpriteSheet(File file, List<SpriteData.Sprite> sprites) {
        if(sprites.size() != 13) {
            throw new IllegalArgumentException("Need 13 sprites for export");
        }
        List<BufferedImage> images = new ArrayList<>();
        for(SpriteData.Sprite sprite : sprites) {
            images.add(createBufferedImage(sprite));
        }
        BufferedImage backgroundImage = createPinkBackground();
        Graphics background = backgroundImage.getGraphics();
        drawNormalSpriteImage(images.get(0), background, 1, 1);
        drawNormalSpriteImage(images.get(1), background, 66, 1);
        drawNormalSpriteImage(images.get(2), background, 131, 1);
        drawNormalSpriteImage(images.get(3), background, 196, 1);
        drawNormalSpriteImage(images.get(4), background, 1, 58);
        drawNormalSpriteImage(images.get(5), background, 66, 58);
        drawNormalSpriteImage(images.get(6), background, 131, 58);
        drawNormalSpriteImage(images.get(7), background, 196, 58);
        drawNormalSpriteImage(images.get(8), background, 1, 115);
        drawNormalSpriteImage(images.get(9), background, 66, 115);
        drawNormalSpriteImage(images.get(10), background, 131, 115);
        drawNormalSpriteImage(images.get(11), background, 196, 115);
        background.drawImage(images.get(12), 261, 1, null);
        writeImageToFile(backgroundImage, file);
    }

    private void writeImageToFile(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void drawNormalSpriteImage(BufferedImage from, Graphics background, int x, int y) {
        int relativeX = (64 - from.getWidth())/2;
        int relativeY = 56 - from.getHeight();
        background.drawImage(from, relativeX + x, relativeY + y, null);
    }

    private BufferedImage createPinkBackground() {
        BufferedImage background = new BufferedImage(342, 172, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < background.getWidth(); i++) {
            for(int j = 0; j < background.getHeight(); j++) {
                background.setRGB(i, j, 0x00FF00FF);
            }
        }
        return background;
    }

    private BufferedImage createBlankSprite() {
        BufferedImage background = new BufferedImage(64, 56, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < background.getWidth(); i++) {
            for(int j = 0; j < background.getHeight(); j++) {
                background.setRGB(i, j, 0x0000FF00);
            }
        }
        return background;
    }

    public static BufferedImage createBufferedImage(SpriteData.Sprite sprite) {
        BufferedImage img = new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_RGB);
        byte[] rgbBytes = sprite.get24BitRGB();
        for(int y = 0; y < sprite.getHeight(); y++) {
            for(int x = 0; x < sprite.getWidth(); x++) {
                int pixel = x + y*sprite.getWidth();
                int location = pixel * 3;
                byte red = rgbBytes[location];
                byte green = rgbBytes[location+1];
                byte blue = rgbBytes[location+2];
                int rgb = ((red&0xFF) << 16) | ((green&0xFF) << 8) | (blue&0xFF);
                //log.info("{}x{}:{}", x, y, Integer.toHexString(rgb));
                img.setRGB(x, y, rgb);
            }
        }
        //renderASCII(img);
        return img;
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
