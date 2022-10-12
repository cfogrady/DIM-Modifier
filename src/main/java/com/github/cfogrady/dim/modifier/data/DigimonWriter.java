package com.github.cfogrady.dim.modifier.data;

import com.github.cfogrady.vb.dim.reader.ByteUtils;
import com.github.cfogrady.vb.dim.reader.content.DimStats;
import com.github.cfogrady.vb.dim.reader.content.SpriteData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class DigimonWriter {
    public void writeDigimon(String author, MonsterSlot monster, OutputStream outputStream) throws IOException {
        int version = 1;
        writeInt(version, outputStream);
        byte[] authorBytes = author.getBytes(StandardCharsets.UTF_16);
        writeInt(authorBytes.length, outputStream);
        outputStream.write(authorBytes);
        writeStats(monster.getStatBlock(), outputStream);
        writeSprites(monster.getSprites(), outputStream);
    }

    private void writeStats(DimStats.DimStatBlock stats, OutputStream outputStream) throws IOException {
        log.info("Stats exported: {}", stats);
        writeInt(stats.getStage(), outputStream);
        writeInt(stats.getDisposition(), outputStream);
        writeInt(stats.getAttribute(), outputStream);
        writeInt(stats.getAp(), outputStream);
        writeInt(stats.getHp(), outputStream);
        writeInt(stats.getDp(), outputStream);
        writeInt(stats.getDpStars(), outputStream);
        writeInt(stats.getBigAttackId(), outputStream);
        writeInt(stats.getSmallAttackId(), outputStream);
        //purposefully ignoring unlock, and battle chances because those should be DIM specific
    }

    private void writeSprites(List<SpriteData.Sprite> sprites, OutputStream outputStream) throws IOException {
        writeInt(sprites.size(), outputStream);
        for(SpriteData.Sprite sprite : sprites) {
            writeSprite(sprite, outputStream);
        }
    }

    private void writeSprite(SpriteData.Sprite sprite, OutputStream outputStream) throws IOException {
        writeInt(sprite.getWidth(), outputStream);
        writeInt(sprite.getHeight(), outputStream);
        outputStream.write(sprite.getPixelData());
    }

    private void writeInt(int value, OutputStream outputStream) throws IOException {
        outputStream.write(ByteUtils.convert32BitIntToBytes(value));
    }
}
