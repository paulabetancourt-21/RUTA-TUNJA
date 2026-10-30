package co.edu.uptc.i18n;

public interface LanguageService {
    String getText(String key);
    void setLanguage(String languageCode);
    String getCurrentLanguage();
}
