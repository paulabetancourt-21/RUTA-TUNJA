package co.edu.uptc.i18n;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.util.AbstractYamlService;
import co.edu.uptc.util.YamlLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlLanguageService extends AbstractYamlService implements LanguageService {

    private static final Logger LOGGER = Logger.getLogger(YamlLanguageService.class.getName());
    private ConfigService configService;
    private String currentLanguage;
    private Map<String, Map<String, Object>> externalLanguages;
    private List<String> externalLanguageCodes;
    private Map<String, String> languageNames;
    private YamlLoader externalLoader;

    public YamlLanguageService(ConfigService configService) {
        super();
        this.configService = configService;
        initCollections();
        loadExternalDirectory();
    }

    private void initCollections() {
        currentLanguage = configService.getString("i18n.defaultLanguage");
        externalLanguages = new HashMap<>();
        externalLanguageCodes = new ArrayList<>();
        languageNames = new HashMap<>();
        externalLoader = new YamlLoader();
    }

    private void loadExternalDirectory() {
        String ext = configService.getString("files.i18nExtension");
        File[] files = externalDirectory().listFiles((d, n) -> n.endsWith(ext));
        if (files != null) for (File f : files) loadExternalLanguage(f);
    }

    private File externalDirectory() {
        return new File(configService.getString("files.externalI18nDir"));
    }

    @Override
    public List<String> getAvailableLanguages() {
        List<String> list = new ArrayList<>(configService.getList("i18n.availableLanguages"));
        externalLanguageCodes.stream().filter(c -> !list.contains(c)).forEach(list::add);
        return list;
    }

    @Override
    public boolean isLanguageAvailable(String code) {
        return getAvailableLanguages().contains(code);
    }

    private String buildFileName() {
        return configService.getString("files.i18nPrefix") + currentLanguage + configService.getString("files.i18nExtension");
    }

    private void ensureLoaded() {
        if (externalLanguages.containsKey(currentLanguage)) {
            dataMap = externalLanguages.get(currentLanguage);
            loaded = true;
            return;
        }
        loadIfNeeded(buildFileName());
    }

    @Override
    public String getText(String key) {
        ensureLoaded();
        Object val = getValue(key);
        return val == null ? key : String.valueOf(val);
    }

    @Override
    public void setLanguage(String code) {
        if (!isLanguageAvailable(code)) {
            logInvalidLanguage(code);
            return;
        }
        applyLanguage(code);
    }

    private void applyLanguage(String code) {
        currentLanguage = code;
        if (externalLanguages.containsKey(code)) {
            dataMap = externalLanguages.get(code);
            loaded = true;
            return;
        }
        resetAndLoad();
    }

    private void logInvalidLanguage(String code) {
        LOGGER.warning("Idioma no disponible: " + code);
    }

    private void resetAndLoad() {
        loaded = false;
        dataMap = new HashMap<>();
        ensureLoaded();
    }

    @Override
    public boolean loadExternalLanguage(String languageCode, File file) {
        String code = normalizeCode(languageCode);
        if (!validFile(file) || !validCode(code) || !storeFromFile(code, file)) {
            LOGGER.warning("Idioma externo inválido: " + code + " (" + file + ")");
            return false;
        }
        persist(file, code);
        LOGGER.info("Idioma externo cargado: " + code + " desde " + file.getAbsolutePath());
        return true;
    }

    public boolean loadExternalLanguage(File file) {
        if (!validFile(file)) {
            LOGGER.warning("Archivo de idioma inválido: " + file);
            return false;
        }
        return loadExternalLanguage(extractCode(file), file);
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toLowerCase();
    }

    private boolean storeFromFile(String code, File file) {
        Map<String, Object> map = loadMap(file);
        if (emptyMap(map)) return false;
        store(code, map);
        return true;
    }

    private boolean validFile(File file) {
        return file != null && file.getName().endsWith(configService.getString("files.i18nExtension"));
    }

    private String extractCode(File file) {
        String ext = configService.getString("files.i18nExtension");
        String name = file.getName();
        return name.substring(0, name.length() - ext.length());
    }

    private boolean validCode(String code) {
        return code.matches("[a-z]{2}");
    }

    private Map<String, Object> loadMap(File file) {
        return externalLoader.loadFromFile(file);
    }

    private boolean emptyMap(Map<String, Object> map) {
        return map == null || map.isEmpty();
    }

    private void store(String code, Map<String, Object> map) {
        externalLanguages.put(code, map);
        if (!externalLanguageCodes.contains(code)) externalLanguageCodes.add(code);
        extractAndStoreName(code, map);
    }

    private void extractAndStoreName(String code, Map<String, Object> map) {
        Object lang = map.get("language");
        Object name = lang instanceof Map ? ((Map<?, ?>) lang).get("name") : null;
        if (name != null) languageNames.put(code, String.valueOf(name));
    }

    private void persist(File source, String code) {
        try {
            Path target = buildExternalPath(code);
            if (!isSameFile(source, target)) copyFile(source, target);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error al copiar el idioma " + code + " a " + buildExternalPath(code), exception);
        }
    }

    private Path buildExternalPath(String code) {
        String dir = configService.getString("files.externalI18nDir");
        String ext = configService.getString("files.i18nExtension");
        return Paths.get(dir, code + ext);
    }

    private boolean isSameFile(File source, Path target) {
        return source.toPath().toAbsolutePath().normalize().equals(target.toAbsolutePath().normalize());
    }

    private void copyFile(File source, Path target) throws IOException {
        Files.createDirectories(target.toAbsolutePath().getParent());
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public String getLanguageName(String code) {
        String name = languageNames.get(code);
        return name != null ? name : builtInName(code);
    }

    private String builtInName(String code) {
        String key = "language." + code;
        String text = getText(key);
        return key.equals(text) ? code.toUpperCase() : text;
    }
}