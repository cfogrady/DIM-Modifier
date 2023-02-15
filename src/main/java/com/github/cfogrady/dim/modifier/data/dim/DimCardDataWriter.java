package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.card.Adventure;
import com.github.cfogrady.dim.modifier.data.card.CardDataWriter;
import com.github.cfogrady.dim.modifier.data.card.SpecificFusion;
import com.github.cfogrady.dim.modifier.data.card.TransformationEntry;
import com.github.cfogrady.vb.dim.adventure.AdventureLevels;
import com.github.cfogrady.vb.dim.adventure.DimAdventures;
import com.github.cfogrady.vb.dim.card.DimCard;
import com.github.cfogrady.vb.dim.card.DimWriter;
import com.github.cfogrady.vb.dim.character.CharacterStats;
import com.github.cfogrady.vb.dim.character.DimStats;
import com.github.cfogrady.vb.dim.fusion.AttributeFusions;
import com.github.cfogrady.vb.dim.fusion.DimFusions;
import com.github.cfogrady.vb.dim.fusion.DimSpecificFusions;
import com.github.cfogrady.vb.dim.fusion.SpecificFusions;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import com.github.cfogrady.vb.dim.transformation.DimEvolutionRequirements;
import com.github.cfogrady.vb.dim.transformation.TransformationRequirements;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class DimCardDataWriter extends CardDataWriter<
        DimStats.DimStatBlock,
        DimStats,
        DimEvolutionRequirements.DimEvolutionRequirementBlock,
        DimEvolutionRequirements,
        AdventureLevels.AdventureLevel,
        DimAdventures,
        DimFusions,
        SpecificFusions.SpecificFusionEntry,
        DimSpecificFusions,
        DimCard,
        TransformationEntry,
        DimCharacter,
        Adventure,
        DimCardData
        > {

    public DimCardDataWriter(DimWriter dimWriter) {
        super(dimWriter);
    }

    @Override
    protected DimCard internalMergeBack(DimStats stats, DimEvolutionRequirements transformations, DimAdventures adventures, DimFusions attributeFusions, DimSpecificFusions dimSpecificFusions, SpriteData spriteData) {
        return DimCard.builder()
                .characterStats(stats)
                .transformationRequirements(transformations)
                .adventureLevels(adventures)
                .attributeFusions(attributeFusions)
                .specificFusions(dimSpecificFusions)
                .spriteData(spriteData)
                .build();
    }

    @Override
    protected DimStats mergeNewStatEntries(DimStats old, List<DimStats.DimStatBlock> entries) {
        DimStats.DimStatsBuilder<?, ?> builder = old.toBuilder();
        if(old.getCharacterEntries().size() != entries.size() && entries.size() < DimStats.VB_TABLE_SIZE) {
            log.info("Number of digimon on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimStats.VB_TABLE_SIZE - entries.size());
        }
        return builder.characterEntries(entries).build();
    }

    @Override
    protected DimStats.DimStatBlock finalizeCharacterStats(CharacterStats.CharacterStatsEntry.CharacterStatsEntryBuilder<? extends DimStats.DimStatBlock, ?> builder, DimCharacter character) {
        return builder.build();
    }

    @Override
    protected CharacterStats.CharacterStatsEntry.CharacterStatsEntryBuilder<? extends DimStats.DimStatBlock, ?> getStatsBuilder(DimCharacter character) {
        var builder = DimStats.DimStatBlock.builder();
        if(character.getStage() < 2) {
            builder = builder.dpStars(NONE_VALUE)
                    .unlockRequired(false);
        } else {
            builder = builder.dpStars(character.getStars())
                    .unlockRequired(character.isFinishAdventureToUnlock());
        }
        return builder;
    }

    @Override
    protected DimEvolutionRequirements mergeNewTransformationEntries(DimEvolutionRequirements old, List<DimEvolutionRequirements.DimEvolutionRequirementBlock> entries) {
        DimEvolutionRequirements.DimEvolutionRequirementsBuilder<?, ?> builder = old.toBuilder().transformationEntries(entries);
        if(entries.size() != old.getTransformationEntries().size() && entries.size() < DimEvolutionRequirements.VB_TABLE_SIZE) {
            log.info("Number of evolutions on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimEvolutionRequirements.VB_TABLE_SIZE - entries.size());
        }
        return builder.build();
    }

    @Override
    protected TransformationRequirements.TransformationRequirementsEntry.TransformationRequirementsEntryBuilder<? extends DimEvolutionRequirements.DimEvolutionRequirementBlock, ?> getTransformationEntryBuilder(DimCharacter character, TransformationEntry transformation) {
        if(character.getHoursUntilTransformation() == null) {
            throw new IllegalStateException("Somehow have no hours until transformation even though we have evolution requirements!");
        }
        int hoursUntileTransformation = character.getHoursUntilTransformation();
        return DimEvolutionRequirements.DimEvolutionRequirementBlock.builder()
                .hoursUntilEvolution(hoursUntileTransformation);
    }

    @Override
    protected TransformationRequirements.TransformationRequirementsEntry.TransformationRequirementsEntryBuilder<? extends DimEvolutionRequirements.DimEvolutionRequirementBlock, ?> getTransformationEntryFromFusionBuilder(DimCharacter character) {
        if(character.getHoursUntilTransformation() == null) {
            throw new IllegalStateException("Somehow have no minutes until transformation even though we have evolution requirements!");
        }
        return DimEvolutionRequirements.DimEvolutionRequirementBlock.builder()
                .hoursUntilEvolution(character.getHoursUntilTransformation());
    }

    @Override
    protected AdventureLevels.AdventureLevel.AdventureLevelBuilder<? extends AdventureLevels.AdventureLevel, ?> getAdventureLevelBuilder(Adventure adventureEntry, List<DimCharacter> characters, Map<UUID, Integer> characterIdToSlot) {
        return AdventureLevels.AdventureLevel.builder();
    }

    @Override
    protected DimAdventures mergeNewAdventureEntries(DimAdventures old, List<AdventureLevels.AdventureLevel> entries) {
        if(entries.size() != old.getLevels().size()) {
            log.warn("Number of adventures on this dim has changed. All known valid DIMs have 15 adventures.");
        }
        return old.toBuilder().levels(entries).build();
    }

    @Override
    protected DimFusions mergeAttributeFusions(DimFusions oldFusions, List<AttributeFusions.AttributeFusionEntry> entries) {
        DimFusions.DimFusionsBuilder<?, ?> builder = oldFusions.toBuilder().entries(entries);
        if(entries.size() != oldFusions.getEntries().size() && entries.size() < DimFusions.VB_TABLE_SIZE) {
            log.info("Number of fusions on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimFusions.VB_TABLE_SIZE - entries.size());
        }
        return builder.build();
    }

    @Override
    protected boolean includeBlankFusionRow(DimCharacter character) {
        return false;
    }

    @Override
    protected SpecificFusions.SpecificFusionEntry.SpecificFusionEntryBuilder<? extends SpecificFusions.SpecificFusionEntry, ?> getSpecificFusionBuilder(SpecificFusion specificFusion, DimCardData cardData) {
        return SpecificFusions.SpecificFusionEntry.builder();
    }

    @Override
    protected DimSpecificFusions mergeSpecificFusions(DimSpecificFusions old, List<SpecificFusions.SpecificFusionEntry> entries) {
        DimSpecificFusions.DimSpecificFusionsBuilder<?, ?> builder = old.toBuilder();
        if(entries.size() != old.getEntries().size() && entries.size() < DimSpecificFusions.VB_TABLE_SIZE) {
            log.info("Number of specific fusions on this dim has changed and the new value is less than table size. Using full table dummy rows");
            builder = builder.dummyRows(DimSpecificFusions.VB_TABLE_SIZE - entries.size());
        }
        return builder.entries(entries).build();
    }

    @Override
    protected SpriteData createSpriteData(SpriteData oldSpriteData, DimCardData dimData) {
        SpriteData.SpriteDataBuilder builder = oldSpriteData.toBuilder();
        List<SpriteData.Sprite> sprites = new ArrayList<>();
        sprites.add(dimData.getCardSprites().getLogo());
        sprites.add(dimData.getCardSprites().getDisplayBackground());
        sprites.addAll(dimData.getCardSprites().getEgg());
        for(DimCharacter character : dimData.getCharacters()) {
            // use correct number of sprites for monsters with lower level than stage 2
            int stage = character.getStage();
            for(int i = 0; i < DimCardDataReader.numberOfSpritesForStage(stage); i++) {
                if(i >= character.getSprites().size()) {
                    sprites.add(SpriteData.Sprite.builder().width(1).height(1).pixelData(createDummySprite(1, 1)).build());
                } else {
                    sprites.add(character.getSprites().get(i));
                }
            }
        }
        return builder.sprites(sprites).build();
    }

    public static byte[] createDummySprite(int width, int height) {
        byte[] pixelData = new byte[width*height*2];
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                pixelData[(y*width + x)*2] = (byte) 0b11100000;
                pixelData[(y*width + x)*2 + 1] = (byte) 0b00000111;
            }
        }
        return pixelData;
    }
}
