package co.edu.uptc.view.main;

import co.edu.uptc.config.ConfigService;
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
    private final ConfigService configService;

    public MainMenuBar(LanguageService languageService, MapPresenter presenter, ConfigService configService) {
        this.languageService = languageService;
        this.presenter = presenter;
        this.configService = configService;
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
        addItem(menu, entry("menu.createNode").mnemonic(KeyEvent.VK_N).accelerator(ctrl(KeyEvent.VK_N)).action(event -> presenter.startCreateNode()));
        addItem(menu, entry("menu.createEdge").mnemonic(KeyEvent.VK_E).accelerator(ctrl(KeyEvent.VK_E)).action(event -> presenter.startCreateEdge()));
        add(menu);
    }

    private void buildRouteMenu() {
        JMenu menu = createMenu("menu.route", KeyEvent.VK_R);
        addItem(menu, entry("menu.calculateRoute").mnemonic(KeyEvent.VK_C).accelerator(ctrl(KeyEvent.VK_R)).action(event -> presenter.startCalculateRoute()));
        addItem(menu, entry("menu.saveRoute").mnemonic(KeyEvent.VK_G).accelerator(ctrl(KeyEvent.VK_G)).action(event -> presenter.openSaveRouteDialog()));
        addItem(menu, entry("menu.clearRoute").mnemonic(KeyEvent.VK_L).accelerator(ctrl(KeyEvent.VK_L)).action(event -> presenter.clearRoute()));
        add(menu);
    }

    private void buildOptionsMenu() {
        JMenu menu = createMenu("menu.options", KeyEvent.VK_O);
        addBasicOptionsItems(menu);
        menu.add(buildZoomSubMenu());
        menu.add(buildSavedRoutesMenu());
        addItem(menu, entry("menu.exit").mnemonic(KeyEvent.VK_Q).accelerator(ctrl(KeyEvent.VK_Q)).action(event -> presenter.exitApplication()));
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
        addItem(menu, entry("menu.newGraph").mnemonic(KeyEvent.VK_U).accelerator(ctrlShift(KeyEvent.VK_N)).action(event -> presenter.resetGraph()));
        addItem(menu, entry("menu.open").mnemonic(KeyEvent.VK_A).accelerator(ctrl(KeyEvent.VK_O)).action(event -> presenter.openGraphDialog()));
        addItem(menu, entry("menu.save").mnemonic(KeyEvent.VK_S).accelerator(ctrl(KeyEvent.VK_S)).action(event -> presenter.saveGraph()));
        addItem(menu, entry("menu.undo").mnemonic(KeyEvent.VK_Z).accelerator(ctrl(KeyEvent.VK_Z)).action(event -> presenter.undo()));
    }

    private JMenu buildZoomSubMenu() {
        JMenu zoomMenu = createMenu("menu.zoom", KeyEvent.VK_Z);
        addItem(zoomMenu, entry("menu.zoomIn").mnemonic(KeyEvent.VK_A).accelerator(ctrl(KeyEvent.VK_PLUS)).action(event -> presenter.zoomIn()));
        addItem(zoomMenu, entry("menu.zoomOut").mnemonic(KeyEvent.VK_L).accelerator(ctrl(KeyEvent.VK_MINUS)).action(event -> presenter.zoomOut()));
        addItem(zoomMenu, entry("menu.originalSize").mnemonic(KeyEvent.VK_T).accelerator(ctrl(KeyEvent.VK_0)).action(event -> presenter.resetZoom()));
        return zoomMenu;
    }

    private void buildHelpMenu() {
        JMenu menu = createMenu("menu.help", KeyEvent.VK_A);
        addItem(menu, entry("menu.configuration").mnemonic(KeyEvent.VK_C).accelerator(ctrlAlt(KeyEvent.VK_S)).action(event -> presenter.openConfigDialog()));
        menu.add(buildLanguageSubMenu());
        addItem(menu, entry("menu.howItWorks").mnemonic(KeyEvent.VK_H).accelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)).action(event -> presenter.openHowItWorksDialog()));
        addItem(menu, entry("menu.about").mnemonic(KeyEvent.VK_I).accelerator(ctrl(KeyEvent.VK_I)).action(event -> presenter.openAboutDialog()));
        add(menu);
    }

    private JMenu buildLanguageSubMenu() {
        JMenu langMenu = createMenu("menu.language", KeyEvent.VK_I);
        List<String> languages = languageService.getAvailableLanguages();
        for (int index = 0; index < languages.size(); index++) {
            addLanguageItem(langMenu, languages.get(index), index);
        }
        addItem(langMenu, entry("menu.loadLanguage").mnemonic(KeyEvent.VK_C).accelerator(ctrlShift(KeyEvent.VK_I)).action(event -> presenter.openLoadLanguageDialog()));
        return langMenu;
    }

    private void addLanguageItem(JMenu menu, String languageCode, int index) {
        JMenuItem item = new JMenuItem(languageService.getLanguageName(languageCode));
        item.setAccelerator(languageAccelerator(index));
        item.addActionListener(event -> presenter.changeLanguage(languageCode));
        menu.add(item);
    }

    private KeyStroke languageAccelerator(int index) {
        int limit = configService.getInt("ui.menu.maxLanguageAccelerators");
        if (index < 0 || index > limit) {
            return null;
        }
        return ctrlAlt(KeyEvent.VK_1 + index);
    }

    private JMenu createMenu(String titleKey, int mnemonic) {
        JMenu menu = new JMenu(languageService.getText(titleKey));
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private void addItem(JMenu parent, MenuEntry entry) {
        JMenuItem item = new JMenuItem(languageService.getText(entry.textKey));
        item.setMnemonic(entry.mnemonic);
        item.setAccelerator(entry.accelerator);
        item.addActionListener(entry.listener);
        parent.add(item);
    }

    private MenuEntry entry(String textKey) {
        return new MenuEntry(textKey);
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

    private static final class MenuEntry {
        private final String textKey;
        private int mnemonic;
        private KeyStroke accelerator;
        private ActionListener listener;

        private MenuEntry(String textKey) {
            this.textKey = textKey;
        }

        private MenuEntry mnemonic(int value) {
            this.mnemonic = value;
            return this;
        }

        private MenuEntry accelerator(KeyStroke value) {
            this.accelerator = value;
            return this;
        }

        private MenuEntry action(ActionListener value) {
            this.listener = value;
            return this;
        }
    }
}
