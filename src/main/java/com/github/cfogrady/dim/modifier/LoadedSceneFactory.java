package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.data.dim.DigimonReader;
import com.github.cfogrady.dim.modifier.data.dim.DigimonWriter;
import com.github.cfogrady.dim.modifier.data.dim.DimData;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareData;
import com.github.cfogrady.vb.dim.card.DimCard;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoadedSceneFactory {
    private final DigimonWriter digimonWriter;
    private final DigimonReader digimonReader;
    private final Stage stage;
    public LoadedScene createLoadedScene(FirmwareData firmwareData, DimCard dimContent, DimData dimData) {
        return new LoadedScene(firmwareData, dimContent, dimData, stage, digimonWriter, digimonReader);
    }
}
