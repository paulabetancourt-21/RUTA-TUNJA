package co.edu.uptc.app;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.config.YamlConfigService;
import co.edu.uptc.logging.LoggingConfig;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ApplicationLauncher {

    private static final Logger LOGGER = Logger.getLogger(ApplicationLauncher.class.getName());

    private ApplicationLauncher() {
    }

    public static void launch() {
        SwingUtilities.invokeLater(ApplicationLauncher::start);
    }

    private static void start() {
        ConfigService config = new YamlConfigService();
        prepareEnvironment(config);
        startApplication(config);
    }

    private static void prepareEnvironment(ConfigService config) {
        LoggingConfig.configure(config);
        addShutdownLog();
        FlatLightLaf.setup();
    }

    private static void startApplication(ConfigService config) {
        try {
            new ApplicationContext(config).start();
            LOGGER.info("Aplicación iniciada correctamente");
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Error al iniciar la aplicación", exception);
            throw exception;
        }
    }

    private static void addShutdownLog() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("Cerrando aplicación")));
    }
}
