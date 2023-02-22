package com.github.cfogrady.dim.modifier.data.card;

import java.util.ArrayList;
import java.util.List;

public abstract class CardValidator<CardDataType extends CardData<?, ?, ?>> {
    public List<String> valid(CardDataType cardData) {
        List<String> errors = new ArrayList<>();
        return errors;
    }

    protected abstract List<String> validateSpriteSizes(CardDataType cardDataType);

}
