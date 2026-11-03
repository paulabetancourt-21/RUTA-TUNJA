package co.edu.uptc.app;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.i18n.YamlLanguageService;
import co.edu.uptc.model.MapModelService;
import co.edu.uptc.model.MapModelServiceImpl;
import co.edu.uptc.model.ModelDependencies;
import co.edu.uptc.presenter.MapPresenter;
import co.edu.uptc.presenter.MapPresenterImpl;
import co.edu.uptc.view.main.MainFrame;

public final class ApplicationContext {

    private final ConfigService configService;
    private final LanguageService languageService;
    private final MapModelService model;
    private final MainFrame view;
    private final MapPresenter presenter;

    public ApplicationContext(ConfigService configService) {
        this.configService = configService;
        this.languageService = new YamlLanguageService(configService);
        this.model = new MapModelServiceImpl(configService, languageService, ModelDependencies.createDefault(configService));
        this.view = new MainFrame(languageService, configService);
        this.presenter = new MapPresenterImpl(view, model, languageService);
        view.setPresenter(presenter);
    }

    public void start() {
        presenter.start();
    }

    public ConfigService configService() {
        return configService;
    }

    public LanguageService languageService() {
        return languageService;
    }

    public MapModelService model() {
        return model;
    }

    public MainFrame view() {
        return view;
    }

    public MapPresenter presenter() {
        return presenter;
    }
}
