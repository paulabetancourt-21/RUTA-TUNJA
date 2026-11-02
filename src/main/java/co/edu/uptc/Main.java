package co.edu.uptc;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.config.YamlConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.i18n.YamlLanguageService;
import co.edu.uptc.logging.LoggingConfig;
import co.edu.uptc.model.MapModelService;
import co.edu.uptc.model.MapModelServiceImpl;
import co.edu.uptc.presenter.MapPresenter;
import co.edu.uptc.presenter.MapPresenterImpl;
import co.edu.uptc.view.main.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    private static void startApplication() {
        ConfigService configService = new YamlConfigService();
        LoggingConfig.configure(configService);
        addShutdownLog();
        try {
            startUi(configService);
            LOGGER.info("Aplicación iniciada correctamente");
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Error al iniciar la aplicación", exception);
            throw exception;
        }
    }

    private static void startUi(ConfigService configService) {
        FlatLightLaf.setup();
        LanguageService languageService = new YamlLanguageService(configService);
        MainFrame mainFrame = new MainFrame(languageService, configService);
        MapModelService model = new MapModelServiceImpl(configService, languageService);
        MapPresenter presenter = new MapPresenterImpl(mainFrame, model);
        mainFrame.setPresenter(presenter);
        presenter.start();
    }

    private static void addShutdownLog() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("Cerrando aplicación")));
    }
}
