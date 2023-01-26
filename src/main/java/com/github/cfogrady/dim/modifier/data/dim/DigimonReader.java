package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.LoadedScene;
import com.github.cfogrady.dim.modifier.data.MonsterSlotWithAuthor;
import com.github.cfogrady.dim.modifier.data.dim.DimData;
import com.github.cfogrady.dim.modifier.data.dim.MonsterSlot;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.util.ByteUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class DigimonReader {

    public MonsterSlotWithAuthor readDigimon(InputStream inputStream) throws IOException {
        int version = readInt(inputStream);
        if(version != 1) {
            throw new IOException("This program current only recognizes version 1 of the mon format.");
        }
        int authorNameSize = readInt(inputStream);
        byte[] authorNameBytes = inputStream.readNBytes(authorNameSize);
        String author = new String(authorNameBytes, StandardCharsets.UTF_16);
        log.info("Brought to you by: {}", author);
        DimStats.DimStatBlock stats = readStats(inputStream);
        List<SpriteData.Sprite> sprites = readSprites(inputStream);
        MonsterSlot monsterSlot = MonsterSlot.builder()
                .hoursUntilEvolution(LoadedScene.NONE_VALUE)
                .id(UUID.randomUUID())
                .statBlock(stats)
                .sprites(sprites)
                .evolutionEntries(List.of(DimData.setupEmptyEvolution()))
                .build();
        return MonsterSlotWithAuthor.builder().author(author).monsterSlot(monsterSlot).build();
    }

    private DimStats.DimStatBlock readStats(InputStream inputStream) throws IOException {
        DimStats.DimStatBlock.DimStatBlockBuilder statsBuilder = DimStats.DimStatBlock.builder();
        statsBuilder.stage(readInt(inputStream));
        statsBuilder.disposition(readInt(inputStream));
        statsBuilder.attribute(readInt(inputStream));
        statsBuilder.ap(readInt(inputStream));
        statsBuilder.hp(readInt(inputStream));
        statsBuilder.dp(readInt(inputStream));
        statsBuilder.dpStars(readInt(inputStream));
        statsBuilder.bigAttackId(readInt(inputStream));
        statsBuilder.smallAttackId(readInt(inputStream));
        //purposefully ignoring unlock, and battle chances because those should be DIM specific
        DimStats.DimStatBlock stats = statsBuilder.build();
        log.info("Stats imported: {}", stats);
        return stats;
    }

    private List<SpriteData.Sprite> readSprites(InputStream inputStream) throws IOException {
        int numberOfSprites = readInt(inputStream);
        List<SpriteData.Sprite> sprites = new ArrayList<>(numberOfSprites);
        for(int i = 0; i < numberOfSprites; i++ ) {
            sprites.add(readSprite(inputStream));
        }
        return sprites;
    }

    private SpriteData.Sprite readSprite(InputStream inputStream) throws IOException {
        int width = readInt(inputStream);
        int height = readInt(inputStream);
        byte[] pixelData = inputStream.readNBytes(width * height * 2); //2 bytes per pixel (16-bit)
        return SpriteData.Sprite.builder().width(width).height(height).pixelData(pixelData).build();
    }

    private int readInt(InputStream inputStream) throws IOException {
        return ByteUtils.getIntsFromBytes(inputStream.readNBytes(4))[0];
    }
}
