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
        setupHandlers(config.getString("logs.directory"), config.getString("logs.fileNamePattern"));
        applyLevel(config.getString("logs.level"));
        deleteOldLogs(config.getString("logs.directory"), config.getString("logs.fileNamePattern"),
                config.getInt("logs.rotationDays"));
    }

    private static void setupHandlers(String directory, String pattern) {
        resetRootLogger();
        ROOT.addHandler(createConsoleHandler());
        try {
            ROOT.addHandler(createFileHandler(directory, pattern));
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "No se pudo crear el archivo de log en " + directory, exception);
        }
    }

    private static Handler createConsoleHandler() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        return handler;
    }

    private static Handler createFileHandler(String directory, String pattern) throws IOException {
        Path folder = Paths.get(directory);
        Files.createDirectories(folder);
        FileHandler handler = new FileHandler(folder.resolve(buildFileName(pattern)).toString(), true);
        handler.setFormatter(new SimpleFormatter());
        handler.setEncoding("UTF-8");
        return handler;
    }

    private static String buildFileName(String pattern) {
        LocalDate today = LocalDate.now();
        return pattern.replace("yyyy", String.valueOf(today.getYear()))
                .replace("MM", String.format("%02d", today.getMonthValue()))
                .replace("dd", String.format("%02d", today.getDayOfMonth()));
    }

    private static void deleteOldLogs(String directory, String pattern, int rotationDays) {
        File[] logs = new File(directory).listFiles((dir, name) -> matchLog(name, pattern));
        if (logs == null) {
            return;
        }
        long limit = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(Math.max(rotationDays, 1));
        for (File log : logs) {
            deleteIfExpired(log, limit);
        }
    }

    private static boolean matchLog(String name, String pattern) {
        return name.startsWith(logPrefix(pattern)) && name.endsWith(".log");
    }

    private static String logPrefix(String pattern) {
        int index = pattern.indexOf("yyyy");
        return index > 0 ? pattern.substring(0, index) : "";
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
