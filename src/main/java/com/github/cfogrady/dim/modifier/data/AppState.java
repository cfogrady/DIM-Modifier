package com.github.cfogrady.dim.modifier.data;


import com.github.cfogrady.dim.modifier.data.card.CardData;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareData;
import com.github.cfogrady.dim.modifier.view.BemInfoView;
import com.github.cfogrady.vb.dim.card.Card;
import lombok.Data;

@Data
public class AppState {
    private FirmwareData firmwareData;
    private Card rawCard;
    private CardData<?, ?> cardData;
    private boolean safetyModeOn;
    private BemInfoView currentView;
}
