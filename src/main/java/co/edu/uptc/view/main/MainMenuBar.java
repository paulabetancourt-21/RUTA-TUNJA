package co.edu.uptc.view.main;

import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.model.SavedRoute;
import co.edu.uptc.presenter.MapPresenter;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainMenuBar extends JMenuBar {

    private final LanguageService languageService;
    private final MapPresenter presenter;

    public MainMenuBar(LanguageService languageService, MapPresenter presenter) {
        this.languageService = languageService;
        this.presenter = presenter;
        initMenus();
    }

    private void initMenus() {
        buildViaMenu();
        buildRouteMenu();
        buildOptionsMenu();
        buildHelpMenu();
    }

    private void buildViaMenu() {
        JMenu menu = createMenu("menu.via", KeyEvent.VK_V);
        addItem(menu, "menu.createNode", KeyEvent.VK_N, ctrl(KeyEvent.VK_N), event -> presenter.startCreateNode());
        addItem(menu, "menu.createEdge", KeyEvent.VK_E, ctrl(KeyEvent.VK_E), event -> presenter.startCreateEdge());
        add(menu);
    }

    private void buildRouteMenu() {
        JMenu menu = createMenu("menu.route", KeyEvent.VK_R);
        addItem(menu, "menu.calculateRoute", KeyEvent.VK_C, ctrl(KeyEvent.VK_R), event -> presenter.startCalculateRoute());
        addItem(menu, "menu.saveRoute", KeyEvent.VK_G, ctrl(KeyEvent.VK_G), event -> presenter.openSaveRouteDialog());
        addItem(menu, "menu.clearRoute", KeyEvent.VK_L, ctrl(KeyEvent.VK_L), event -> presenter.clearRoute());
        add(menu);
    }

    private void buildOptionsMenu() {
        JMenu menu = createMenu("menu.options", KeyEvent.VK_O);
        addBasicOptionsItems(menu);
        menu.add(buildZoomSubMenu());
        menu.add(buildSavedRoutesMenu());
        addItem(menu, "menu.exit", KeyEvent.VK_Q, ctrl(KeyEvent.VK_Q), event -> System.exit(0));
        add(menu);
    }

    private JMenu buildSavedRoutesMenu() {
        JMenu menu = createMenu("menu.savedRoutes", KeyEvent.VK_R);
        List<SavedRoute> routes = presenter.getSavedRoutes();
        if (routes.isEmpty()) {
            addDisabledItem(menu, "menu.noSavedRoutes");
            return menu;
        }
        routes.forEach(route -> addSavedRouteItem(menu, route));
        return menu;
    }

    private void addSavedRouteItem(JMenu menu, SavedRoute route) {
        String template = languageService.getText("menu.savedRouteItem");
        JMenuItem item = new JMenuItem(String.format(template, route.getName(), route.getDistanceKm()));
        item.addActionListener(event -> presenter.showSavedRoute(route));
        menu.add(item);
    }

    private void addDisabledItem(JMenu menu, String textKey) {
        JMenuItem item = new JMenuItem(languageService.getText(textKey));
        item.setEnabled(false);
        menu.add(item);
    }

    private void addBasicOptionsItems(JMenu menu) {
        addItem(menu, "menu.newGraph", KeyEvent.VK_U, ctrlShift(KeyEvent.VK_N), event -> presenter.resetGraph());
        addItem(menu, "menu.open", KeyEvent.VK_A, ctrl(KeyEvent.VK_O), event -> presenter.openGraphDialog());
        addItem(menu, "menu.save", KeyEvent.VK_S, ctrl(KeyEvent.VK_S), event -> presenter.saveGraph());
        addItem(menu, "menu.undo", KeyEvent.VK_Z, ctrl(KeyEvent.VK_Z), event -> presenter.undo());
    }

    private JMenu buildZoomSubMenu() {
        JMenu zoomMenu = createMenu("menu.zoom", KeyEvent.VK_Z);
        addItem(zoomMenu, "menu.zoomIn", KeyEvent.VK_A, ctrl(KeyEvent.VK_PLUS), event -> presenter.zoomIn());
        addItem(zoomMenu, "menu.zoomOut", KeyEvent.VK_L, ctrl(KeyEvent.VK_MINUS), event -> presenter.zoomOut());
        addItem(zoomMenu, "menu.originalSize", KeyEvent.VK_T, ctrl(KeyEvent.VK_0), event -> presenter.resetZoom());
        return zoomMenu;
    }

    private void buildHelpMenu() {
        JMenu menu = createMenu("menu.help", KeyEvent.VK_A);
        addItem(menu, "menu.configuration", KeyEvent.VK_C, ctrlAlt(KeyEvent.VK_S), event -> presenter.openConfigDialog());
        menu.add(buildLanguageSubMenu());
        addItem(menu, "menu.howItWorks", KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), event -> presenter.openHowItWorksDialog());
        addItem(menu, "menu.about", KeyEvent.VK_I, ctrl(KeyEvent.VK_I), event -> presenter.openAboutDialog());
        add(menu);
    }

    private JMenu buildLanguageSubMenu() {
        JMenu langMenu = createMenu("menu.language", KeyEvent.VK_I);
        List<String> languages = languageService.getAvailableLanguages();
        for (int index = 0; index < languages.size(); index++) {
            addLanguageItem(langMenu, languages.get(index), index);
        }
        addItem(langMenu, "menu.loadLanguage", KeyEvent.VK_C, ctrlShift(KeyEvent.VK_I), event -> presenter.openLoadLanguageDialog());
        return langMenu;
    }

    private void addLanguageItem(JMenu menu, String languageCode, int index) {
        JMenuItem item = new JMenuItem(languageLabel(languageCode));
        item.setAccelerator(languageAccelerator(index));
        item.addActionListener(event -> presenter.changeLanguage(languageCode));
        menu.add(item);
    }

    private String languageLabel(String languageCode) {
        String key = "language." + languageCode;
        String label = languageService.getText(key);
        return key.equals(label) ? languageCode.toUpperCase() : label;
    }

    private KeyStroke languageAccelerator(int index) {
        if (index < 0 || index > 8) {
            return null;
        }
        return ctrlAlt(KeyEvent.VK_1 + index);
    }

    private JMenu createMenu(String titleKey, int mnemonic) {
        JMenu menu = new JMenu(languageService.getText(titleKey));
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private void addItem(JMenu parent, String textKey, int mnemonic, KeyStroke accelerator, ActionListener listener) {
        JMenuItem item = new JMenuItem(languageService.getText(textKey));
        item.setMnemonic(mnemonic);
        item.setAccelerator(accelerator);
        item.addActionListener(listener);
        parent.add(item);
    }

    private KeyStroke ctrl(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode, KeyEvent.CTRL_DOWN_MASK);
    }

    private KeyStroke ctrlShift(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
    }

    private KeyStroke ctrlAlt(int keyCode) {
        return KeyStroke.getKeyStroke(keyCode, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK);
    }
}
