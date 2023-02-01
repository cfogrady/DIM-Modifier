package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.controls.ImageIntComboBoxFactory;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataReader;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataWriter;
import com.github.cfogrady.dim.modifier.data.dim.DigimonReader;
import com.github.cfogrady.dim.modifier.data.dim.DigimonWriter;
import com.github.cfogrady.dim.modifier.data.dim.DimData;
import com.github.cfogrady.dim.modifier.data.dim.DimDataFactory;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareData;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareManager;
import com.github.cfogrady.dim.modifier.view.BemCharactersView;
import com.github.cfogrady.dim.modifier.view.BemEvolutionsView;
import com.github.cfogrady.dim.modifier.view.BemStatsView;
import com.github.cfogrady.dim.modifier.view.BemStatsViewFactory;
import com.github.cfogrady.vb.dim.card.BemCardReader;
import com.github.cfogrady.vb.dim.card.BemCardWriter;
import com.github.cfogrady.vb.dim.card.DimCard;
import com.github.cfogrady.vb.dim.card.DimReader;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.util.Timer;
import java.util.prefs.Preferences;

@Builder(access = AccessLevel.PRIVATE)
@Data
public class ApplicationOrchestrator {
    private final AppState appState;
    private final BemCardReader bemCardReader;
    private final BemCardWriter bemCardWriter;
    private final BemCardDataWriter bemCardDataWriter;
    private final BemCardDataReader bemCardDataReader;
    private final DimReader dimReader;
    private final LoadedSceneFactory loadedSceneFactory;
    private final LoadedCardDataScene loadedCardDataScene;
    private final FirmwareLoadScene firmwareLoadScene;
    private final FirmwareManager firmwareManager;
    private final FirstLoadScene firstLoadScene;
    private final Timer timer;

    public static ApplicationOrchestrator buildOrchestration(Stage stage) {
        DimReader dimReader = new DimReader();
        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        FirmwareManager firmwareManager = new FirmwareManager(preferences);
        BemCardReader bemCardReader = new BemCardReader();
        BemCardWriter bemCardWriter = new BemCardWriter();
        BemCardDataReader bemCardDataReader = new BemCardDataReader();
        BemCardDataWriter bemCardDataWriter = new BemCardDataWriter(bemCardWriter);
        AppState appState = new AppState();
        DigimonWriter digimonWriter = new DigimonWriter();
        DigimonReader digimonReader = new DigimonReader();
        SpriteImageTranslator spriteImageTranslator = new SpriteImageTranslator();
        ImageIntComboBoxFactory imageIntComboBoxFactory = new ImageIntComboBoxFactory(spriteImageTranslator);
        LoadedSceneFactory loadedSceneFactory = new LoadedSceneFactory(digimonWriter, digimonReader, stage);
        GridPane transformationGrid = new GridPane();
        transformationGrid.setGridLinesVisible(true);
        BemEvolutionsView bemEvolutionsView = new BemEvolutionsView(imageIntComboBoxFactory, appState, transformationGrid);
        Timer timer = new Timer();
        SpriteReplacer spriteReplacer = new SpriteReplacer(appState, stage, spriteImageTranslator);
        BemStatsViewFactory bemStatsViewFactory = new BemStatsViewFactory(appState, spriteImageTranslator, spriteReplacer, imageIntComboBoxFactory);
        BemStatsView bemStatsView = bemStatsViewFactory.buildBemStatsView();
        BemCharactersView bemCharactersView = new BemCharactersView(appState, imageIntComboBoxFactory, spriteImageTranslator, spriteReplacer, timer, bemStatsView, bemEvolutionsView);
        LoadedCardDataScene loadedCardDataScene = new LoadedCardDataScene(dimReader, bemCardDataReader, bemCardDataWriter, bemCharactersView, stage, appState);
        FirstLoadScene firstLoadScene = new FirstLoadScene(appState, stage, dimReader, new DimDataFactory(), bemCardDataReader, loadedSceneFactory, loadedCardDataScene);
        FirmwareLoadScene firmwareLoadScene = new FirmwareLoadScene(stage, firmwareManager, firstLoadScene, appState);
        return ApplicationOrchestrator.builder()
                .appState(appState)
                .dimReader(dimReader)
                .bemCardReader(bemCardReader)
                .bemCardWriter(bemCardWriter)
                .bemCardDataReader(bemCardDataReader)
                .bemCardDataWriter(bemCardDataWriter)
                .firmwareLoadScene(firmwareLoadScene)
                .firmwareManager(firmwareManager)
                .firstLoadScene(firstLoadScene)
                .loadedCardDataScene(loadedCardDataScene)
                .loadedSceneFactory(loadedSceneFactory)
                .timer(timer)
                .build();
    }
}
