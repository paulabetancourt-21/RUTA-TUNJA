package co.edu.uptc;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.config.YamlConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.i18n.YamlLanguageService;
import co.edu.uptc.model.MapModelService;
import co.edu.uptc.model.MapModelServiceImpl;
import co.edu.uptc.presenter.MapPresenter;
import co.edu.uptc.presenter.MapPresenterImpl;
import co.edu.uptc.view.main.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::startApplication);
    }

    private static void startApplication() {
        FlatLightLaf.setup();
        ConfigService configService = new YamlConfigService();
        LanguageService languageService = new YamlLanguageService(configService);
        MainFrame mainFrame = new MainFrame(languageService, configService);
        MapModelService model = new MapModelServiceImpl(configService, languageService);
        MapPresenter presenter = new MapPresenterImpl(mainFrame, model);
        mainFrame.setPresenter(presenter);
        presenter.start();
    }
}
