package com.github.cfogrady.dim.modifier.data.dim;

import com.github.cfogrady.dim.modifier.data.bem.BemTransformationEntry;
import com.github.cfogrady.dim.modifier.data.card.Adventure;
import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.vb.dim.card.DimCard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DimCardData extends CardData<DimCharacter, Adventure, DimCard> {
    private final DimCard originalCard;

    public static Integer[] DIM_PHASES = {1, 2, 3, 4, 5, 6};

    @Override
    public Integer[] getTotalAvailableStages() {
        return DIM_PHASES;
    }

    @Override
    public List<String> checkForErrors() {
        List<String> errors = new ArrayList<>();
        errors.addAll(validateAllTransformationsExist());
        errors.addAll(validateAllTransformationsHaveTime());
        errors.addAll(validateSpecificFusionExist());
        errors.addAll(validateSingleSpecificFusionExist());
        errors.addAll(validateAllAdventureCharactersExist());
        errors.addAll(validateNFCBattlesTotal100());
        errors.addAll(validateBabySlots());
        return errors;
    }

    @Override
    public int getNumberOfAvailableCharacterSlots() {
        return 17;
    }

    public List<String> validateBabySlots() {
        List<String> errors = new ArrayList<>();
        boolean mismatch = false;
        for(int i = 0; i < getCharacters().size(); i++) {
            DimCharacter character = getCharacters().get(i);
            if(i < 2 && i != character.getStage()) {
                mismatch = true;
            } else if(i >= 2 && character.getStage() < 2) {
                mismatch = true;
            }
        }
        if(mismatch) {
            errors.add("Characters in the first two slotsSlots 1-2 must have characters" + System.lineSeparator() +
                    "  at phase 1 and 2 respectively. Phase 1 and 2 characters can only" + System.lineSeparator() +
                    "  be in these slots.");
        }
        return errors;
    }

    private List<String> validateAllTransformationsHaveTime() {
        List<String> errors = new ArrayList<>();
        for(int i = 0; i < getCharacters().size(); i++) {
            DimCharacter character = getCharacters().get(i);
            if(character.hasTransformations()) {
                errors.addAll(validatorCharacterTransformations(i, character));
            }
        }
        return errors;
    }

    private static List<String> validatorCharacterTransformations(int characterIndex, DimCharacter character) {
        List<String> errors = new ArrayList<>();
        if (!character.getSpecificFusions().isEmpty()) {
            errors.add("Character " + characterIndex + " has specific fusions," + System.lineSeparator() +
                    "  but time until fusion checks is not set.");
        }
        for (int i = 0; i < character.getTransformationEntries().size(); i++) {
            DimTransformationEntity transformationEntry = character.getTransformationEntries().get(i);
            if (transformationEntry.getHoursUntilTransformation() == null) {
                errors.add("Character " + characterIndex + " transformation " + (i+1) + " does not have" + System.lineSeparator() +
                        "hours until transformations set.");
            }
        }
        return errors;
    }

    private List<String> validateNFCBattlesTotal100() {
        List<String> errors = new ArrayList<>();
        int firstPoolTotal = CardData.getBattleChanceTotal(getCharacters(), DimCharacter::getFirstPoolBattleChance);
        if(firstPoolTotal != 100) {
            errors.add("Phase 3/4 Battle Pool Chances do not total 100%");
        }
        int secondPoolTotal = CardData.getBattleChanceTotal(getCharacters(), DimCharacter::getSecondPoolBattleChance);
        if(secondPoolTotal != 100) {
            errors.add("Phase 5/6 Battle Pool Chances do not total 100%");
        }
        return errors;
    }

    private List<String> validateSingleSpecificFusionExist() {
        List<String> errors = new ArrayList<>();
        int total = 0;
        for(DimCharacter character : getCharacters()) {
            total += character.getSpecificFusions().size();
        }
        if(total > 1) {
            errors.add("Only a single specific fusion can exist on a DIM." + System.lineSeparator()
                    + "  " + total + " specific fusions detected.");
        }
        return errors;
    }
}
