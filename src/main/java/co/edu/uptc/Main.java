package co.edu.uptc;

import co.edu.uptc.app.ApplicationContext;
import co.edu.uptc.config.ConfigService;
import co.edu.uptc.config.YamlConfigService;
import co.edu.uptc.logging.LoggingConfig;
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
        launch(configService);
    }

    private static void launch(ConfigService configService) {
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
        new ApplicationContext(configService).start();
    }

    private static void addShutdownLog() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("Cerrando aplicación")));
    }
}
