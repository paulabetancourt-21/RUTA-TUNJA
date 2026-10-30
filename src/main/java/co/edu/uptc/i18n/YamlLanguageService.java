package co.edu.uptc.i18n;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.util.AbstractYamlService;

public class YamlLanguageService extends AbstractYamlService implements LanguageService {
    private ConfigService configService;
    private String currentLanguage;

    public YamlLanguageService(ConfigService configService) {
        super();
        this.configService = configService;
        this.currentLanguage = configService.getString("i18n.defaultLanguage");
    }

    private String buildFileName() {
        String prefix = configService.getString("files.i18nPrefix");
        String extension = configService.getString("files.i18nExtension");
        return prefix + currentLanguage + extension;
    }

    private void ensureLoaded() {
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
        currentLanguage = languageCode;
        reload(buildFileName());
    }

    @Override
    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
