package com.github.cfogrady.dim.modifier.data;


import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareData;
import com.github.cfogrady.dim.modifier.view.BemInfoView;
import com.github.cfogrady.vb.dim.card.Card;
import com.github.cfogrady.vb.dim.sprite.SpriteData;
import lombok.Data;

import java.io.File;

@Data
public class AppState {
    private FirmwareData firmwareData;
    private Card rawCard;
    private CardData<?, ?> cardData;
    private boolean safetyModeOn;
    private BemInfoView currentView;
    private File lastOpenedFilePath;
    private int selectedBackgroundIndex;

    public SpriteData.Sprite getSelectedBackground() {
        return cardData.getCardSprites().getBackgrounds().get(selectedBackgroundIndex);
    }
}
