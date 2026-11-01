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

public class YamlLanguageService extends AbstractYamlService implements LanguageService {
    private ConfigService configService;
    private String currentLanguage;
    private Map<String, Map<String, Object>> externalLanguages;
    private List<String> externalLanguageCodes;
    private YamlLoader externalLoader;

    public YamlLanguageService(ConfigService configService) {
        super();
        this.configService = configService;
        initCollections();
        loadExternalDirectory();
    }

    private void initCollections() {
        currentLanguage = configService.getString("i18n.defaultLanguage");
        externalLanguages = new HashMap<String, Map<String, Object>>();
        externalLanguageCodes = new ArrayList<String>();
        externalLoader = new YamlLoader();
    }

    private void loadExternalDirectory() {
        String extension = configService.getString("files.i18nExtension");
        File[] files = externalDirectory().listFiles((dir, name) -> name.endsWith(extension));
        if (files != null) {
            for (File file : files) {
                loadExternalLanguage(file);
            }
        }
    }

    private File externalDirectory() {
        return new File(configService.getString("files.externalI18nDir"));
    }

    @Override
    public List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<String>();
        List<String> base = configService.getList("i18n.availableLanguages");
        if (base != null) {
            languages.addAll(base);
        }
        for (String code : externalLanguageCodes) {
            if (!languages.contains(code)) {
                languages.add(code);
            }
        }
        return languages;
    }

    @Override
    public boolean isLanguageAvailable(String languageCode) {
        return getAvailableLanguages().contains(languageCode);
    }

    private String buildFileName() {
        String prefix = configService.getString("files.i18nPrefix");
        String extension = configService.getString("files.i18nExtension");
        return prefix + currentLanguage + extension;
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
        Object value = getValue(key);
        if (value == null) {
            return key;
        }
        return String.valueOf(value);
    }

    @Override
    public void setLanguage(String languageCode) {
        if (!isLanguageAvailable(languageCode)) {
            return;
        }
        currentLanguage = languageCode;
        if (externalLanguages.containsKey(currentLanguage)) {
            dataMap = externalLanguages.get(currentLanguage);
            loaded = true;
            return;
        }
        loaded = false;
        dataMap = new HashMap<String, Object>();
        ensureLoaded();
    }

    @Override
    public boolean loadExternalLanguage(String languageCode, File file) {
        String code = normalizeCode(languageCode);
        if (!validFile(file) || !validCode(code)) {
            return false;
        }
        if (!storeFromFile(code, file)) {
            return false;
        }
        persist(file, code);
        return true;
    }

    public boolean loadExternalLanguage(File file) {
        if (!validFile(file)) {
            return false;
        }
        return loadExternalLanguage(extractCode(file), file);
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toLowerCase();
    }

    private boolean storeFromFile(String code, File file) {
        Map<String, Object> map = loadMap(file);
        if (emptyMap(map)) {
            return false;
        }
        store(code, map);
        return true;
    }

    private boolean validFile(File file) {
        if (file == null) {
            return false;
        }
        String ext = configService.getString("files.i18nExtension");
        return file.getName().endsWith(ext);
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
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

    private void store(String code, Map<String, Object> map) {
        externalLanguages.put(code, map);
        if (!externalLanguageCodes.contains(code)) {
            externalLanguageCodes.add(code);
        }
    }

    private void persist(File source, String code) {
        try {
            Path target = buildExternalPath(code);
            if (!isSameFile(source, target)) {
                copyFile(source, target);
            }
        } catch (IOException exception) {
            // El idioma sigue disponible en memoria.
        }
    }

    private Path buildExternalPath(String code) {
        String directory = configService.getString("files.externalI18nDir");
        String extension = configService.getString("files.i18nExtension");
        return Paths.get(directory, code + extension);
    }

    private boolean isSameFile(File source, Path target) {
        Path normalizedSource = source.toPath().toAbsolutePath().normalize();
        return normalizedSource.equals(target.toAbsolutePath().normalize());
    }

    private void copyFile(File source, Path target) throws IOException {
        Files.createDirectories(target.toAbsolutePath().getParent());
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
