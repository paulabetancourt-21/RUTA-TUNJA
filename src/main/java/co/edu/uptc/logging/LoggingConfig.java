package co.edu.uptc.logging;

import co.edu.uptc.config.ConfigService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggingConfig {

    private static final Logger ROOT = Logger.getLogger("");
    private static final Logger LOGGER = Logger.getLogger(LoggingConfig.class.getName());

    private LoggingConfig() {
    }

    public static void configure(ConfigService config) {
        setupHandlers(config);
        applyLevel(config.getString("logs.level"));
        deleteOldLogs(config);
    }

    private static void setupHandlers(ConfigService config) {
        resetRootLogger();
        ROOT.addHandler(createConsoleHandler());
        try {
            ROOT.addHandler(createFileHandler(config));
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "No se pudo crear el archivo de log", exception);
        }
    }

    private static Handler createConsoleHandler() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        return handler;
    }

    private static Handler createFileHandler(ConfigService config) throws IOException {
        Path folder = Paths.get(config.getString("logs.directory"));
        Files.createDirectories(folder);
        String fileName = LogPaths.buildFileName(config.getString("logs.fileNamePattern"), LocalDate.now());
        FileHandler handler = new FileHandler(folder.resolve(fileName).toString(), true);
        handler.setFormatter(new SimpleFormatter());
        handler.setEncoding(config.getString("logs.encoding"));
        return handler;
    }

    private static void deleteOldLogs(ConfigService config) {
        File[] logs = listLogs(config);
        if (logs == null) {
            return;
        }
        long limit = expirationLimit(config);
        for (File log : logs) {
            deleteIfExpired(log, limit);
        }
    }

    private static File[] listLogs(ConfigService config) {
        File directory = new File(config.getString("logs.directory"));
        return directory.listFiles((dir, name) -> matchesLog(name, config));
    }

    private static boolean matchesLog(String name, ConfigService config) {
        String pattern = config.getString("logs.fileNamePattern");
        String extension = config.getString("logs.extension");
        return LogPaths.matches(name, pattern, extension);
    }

    private static long expirationLimit(ConfigService config) {
        int days = config.getInt("logs.rotationDays");
        int minimum = config.getInt("logs.minRotationDays");
        long millis = TimeUnit.DAYS.toMillis(Math.max(days, minimum));
        return System.currentTimeMillis() - millis;
    }

    private static void deleteIfExpired(File file, long limit) {
        if (file.lastModified() >= limit) {
            return;
        }
        if (file.delete()) {
            LOGGER.info("Log antiguo eliminado: " + file.getName());
        }
    }

    private static void resetRootLogger() {
        for (Handler handler : ROOT.getHandlers()) {
            ROOT.removeHandler(handler);
            handler.close();
        }
    }

    private static void applyLevel(String levelName) {
        Level level = parseLevel(levelName);
        ROOT.setLevel(level);
        for (Handler handler : ROOT.getHandlers()) {
            handler.setLevel(level);
        }
    }

    private static Level parseLevel(String levelName) {
        if (levelName == null || levelName.isBlank()) {
            return Level.INFO;
        }
        try {
            return Level.parse(levelName.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return Level.INFO;
        }
    }
}
