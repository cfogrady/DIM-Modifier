package com.github.cfogrady.dim.modifier.data;


import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.card.Character;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareData;
import com.github.cfogrady.vb.dim.card.Card;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppState {
    public static final int SELECTION_SPRITE_IDX = 1;

    private FirmwareData firmwareData;
    private Card rawCard;
    private CardData<?, ?> cardData;
    private boolean safetyModeOn;
    private File lastOpenedFilePath;
    private int selectedBackgroundIndex;

    public SpriteData.Sprite getSelectedBackground() {
        return cardData.getCardSprites().getBackgrounds().get(selectedBackgroundIndex);
    }

    public List<SpriteData.Sprite> getIdleForCharacters() {
        List<SpriteData.Sprite> idleSprites = new ArrayList<>();
        for(Character<?> character : getCardData().getCharacters()) {
            idleSprites.add(character.getSprites().get(SELECTION_SPRITE_IDX));
        }
        return idleSprites;
    }

    public Character<?> getCharacter(int characterIndex) {
        return cardData.getCharacters().get(characterIndex);
    }
}
