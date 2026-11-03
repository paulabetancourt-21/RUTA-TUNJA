package co.edu.uptc.i18n;

import java.io.File;
import java.util.List;

public interface LanguageService {
    String getText(String key);
    void setLanguage(String languageCode);
    String getCurrentLanguage();
    List<String> getAvailableLanguages();
    boolean isLanguageAvailable(String languageCode);
    boolean loadExternalLanguage(String languageCode, File file);
    String getLanguageName(String languageCode);
}
