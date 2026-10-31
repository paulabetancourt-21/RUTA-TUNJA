package co.edu.uptc.i18n;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.util.AbstractYamlService;
import co.edu.uptc.util.YamlLoader;
import java.io.File;
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
        this.currentLanguage = configService.getString("i18n.defaultLanguage");
        this.externalLanguages = new HashMap<String, Map<String, Object>>();
        this.externalLanguageCodes = new ArrayList<String>();
        this.externalLoader = new YamlLoader();
    }

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

    public boolean loadExternalLanguage(File file) {
        if (!validFile(file)) {
            return false;
        }
        String code = extractCode(file);
        if (invalidCode(code)) {
            return false;
        }
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

    private boolean invalidCode(String code) {
        if (code == null) {
            return true;
        }
        return code.length() != 2;
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

    @Override
    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
