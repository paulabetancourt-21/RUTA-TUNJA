package co.edu.uptc.view.help;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;

import javax.swing.JFrame;

public final class DialogContext {

    private final JFrame parent;
    private final LanguageService languageService;
    private final ConfigService configService;
    private final DialogTheme theme;

    public DialogContext(JFrame parent, LanguageService languageService, ConfigService configService) {
        this.parent = parent;
        this.languageService = languageService;
        this.configService = configService;
        this.theme = new DialogTheme(configService);
    }

    public JFrame parent() {
        return parent;
    }

    public LanguageService languageService() {
        return languageService;
    }

    public ConfigService configService() {
        return configService;
    }

    public DialogTheme theme() {
        return theme;
    }
}
