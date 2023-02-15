package com.github.cfogrady.dim.modifier;

import com.github.cfogrady.dim.modifier.controllers.*;
import com.github.cfogrady.dim.modifier.data.AppState;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataReader;
import com.github.cfogrady.dim.modifier.data.bem.BemCardDataWriter;
import com.github.cfogrady.dim.modifier.data.card.CardDataIO;
import com.github.cfogrady.dim.modifier.data.dim.DimCardDataReader;
import com.github.cfogrady.dim.modifier.data.dim.DimCardDataWriter;
import com.github.cfogrady.dim.modifier.data.firmware.FirmwareManager;
import com.github.cfogrady.vb.dim.card.DimReader;
import com.github.cfogrady.vb.dim.card.DimWriter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.util.Timer;
import java.util.prefs.Preferences;

@Builder(access = AccessLevel.PRIVATE)
@Data
public class ApplicationOrchestrator {
    private final AppState appState;
    private final FirmwareLoadScene firmwareLoadScene;
    private final FirmwareManager firmwareManager;
    private final FirstLoadScene firstLoadScene;
    private final Timer timer;

    public static ApplicationOrchestrator buildOrchestration(Stage stage) throws IOException {
        DimReader dimReader = new DimReader();
        DimWriter dimWriter = new DimWriter();
        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        FirmwareManager firmwareManager = new FirmwareManager(preferences);
        DimCardDataReader dimCardDataReader = new DimCardDataReader();
        DimCardDataWriter dimCardDataWriter = new DimCardDataWriter(dimWriter);
        BemCardDataReader bemCardDataReader = new BemCardDataReader();
        BemCardDataWriter bemCardDataWriter = new BemCardDataWriter(dimWriter);
        CardDataIO cardDataIO = new CardDataIO(dimReader, dimCardDataWriter, dimCardDataReader, bemCardDataWriter, bemCardDataReader);
        SpriteImageTranslator spriteImageTranslator = new SpriteImageTranslator();
        AppState appState = new AppState();
        DimIOController dimReaderController = new DimIOController(stage, cardDataIO, appState);
        GridPane transformationGrid = new GridPane();
        transformationGrid.setGridLinesVisible(true);
        RegularTransformationsGridController regularTransformationGridController = new RegularTransformationsGridController(spriteImageTranslator, appState);
        Timer timer = new Timer();
        SpriteReplacer spriteReplacer = new SpriteReplacer(appState, stage, spriteImageTranslator);
        StatsGridController statsGridController = new StatsGridController(appState, spriteImageTranslator);
        StatsViewController statsViewController = new StatsViewController(appState, spriteImageTranslator, spriteReplacer, statsGridController);
        FXMLLoader loader = new FXMLLoader(ApplicationOrchestrator.class.getResource("/StatsView.fxml"));
        loader.setControllerFactory(p -> statsViewController);
        Node statsView = loader.load();
        SpecificFusionGridController specificFusionGridController = new SpecificFusionGridController(spriteImageTranslator, appState);
        AttributeFusionGridController attributeFusionGridController = new AttributeFusionGridController(appState, spriteImageTranslator);
        TransformationViewController transformationViewController = new TransformationViewController(regularTransformationGridController, specificFusionGridController, attributeFusionGridController);
        loader = new FXMLLoader(ApplicationOrchestrator.class.getResource("/TransformationView.fxml"));
        loader.setControllerFactory(p -> transformationViewController);
        CharacterViewController characterViewController = new CharacterViewController(timer, appState, spriteImageTranslator, spriteReplacer, statsView, statsViewController, transformationViewController, loader.load());
        loader = new FXMLLoader(ApplicationOrchestrator.class.getResource("/CharacterView.fxml"));
        loader.setControllerFactory(p -> characterViewController);
        Node charactersView = loader.load();
        NFCGridController nfcGridController = new NFCGridController(spriteImageTranslator, appState);
        AdventureGridController adventureGridController = new AdventureGridController(spriteImageTranslator, appState);
        BattlesViewController battlesViewController = new BattlesViewController(nfcGridController, adventureGridController);
        loader = new FXMLLoader(ApplicationOrchestrator.class.getResource("/BattlesView.fxml"));
        loader.setControllerFactory(p -> battlesViewController);
        Node battlesView = loader.load();
        SystemViewController systemViewController = new SystemViewController(appState, spriteImageTranslator, spriteReplacer);
        loader = new FXMLLoader(ApplicationOrchestrator.class.getResource("/SystemView.fxml"));
        loader.setControllerFactory(p -> systemViewController);
        Node systemView = loader.load();
        LoadedViewController loadedViewController = new LoadedViewController(appState, charactersView, characterViewController, battlesViewController, battlesView, systemViewController, systemView, dimReaderController);
        FirstLoadScene firstLoadScene = new FirstLoadScene(appState, stage, cardDataIO, loadedViewController);
        FirmwareLoadScene firmwareLoadScene = new FirmwareLoadScene(stage, firmwareManager, firstLoadScene, appState);
        return ApplicationOrchestrator.builder()
                .appState(appState)
                .firmwareLoadScene(firmwareLoadScene)
                .firmwareManager(firmwareManager)
                .firstLoadScene(firstLoadScene)
                .timer(timer)
                .build();
    }
}
