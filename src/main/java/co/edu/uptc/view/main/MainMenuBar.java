package co.edu.uptc.view.main;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainMenuBar extends JMenuBar {

    private final LanguageService languageService;
    private final MainFrame mainFrame;

    public MainMenuBar(LanguageService languageService, MainFrame mainFrame) {
        this.languageService = languageService;
        this.mainFrame = mainFrame;
        initMenus();
    }

    private void initMenus() {
        buildViaMenu();
        buildRouteMenu();
        buildOptionsMenu();
        buildHelpMenu();
    }

    private void buildViaMenu() {
        JMenu menu = createMenu("Via", KeyEvent.VK_V);
        addItem(menu, "Crear nuevo nodo", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.openCreateNodeDialog());
        addItem(menu, "Crear nueva arista", KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.openCreateEdgeDialog());
        add(menu);
    }

    private void buildRouteMenu() {
        JMenu menu = createMenu("Ruta", KeyEvent.VK_R);
        addItem(menu, "Calcular ruta", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.openCalculateRouteDialog());
        addItem(menu, "Guardar ruta", KeyEvent.VK_G, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.openSaveRouteDialog());
        addItem(menu, "Limpiar ruta", KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.clearRoute());
        add(menu);
    }

    private void buildOptionsMenu() {
        JMenu menu = createMenu("Opciones", KeyEvent.VK_O);
        addBasicOptionsItems(menu);
        menu.add(buildZoomSubMenu());
        menu.add(createMenu("Rutas guardadas", KeyEvent.VK_R));
        addItem(menu, "Salir", KeyEvent.VK_Q, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK), e -> System.exit(0));
        add(menu);
    }

    private void addBasicOptionsItems(JMenu menu) {
        addItem(menu, "Nuevo grafo", KeyEvent.VK_U, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), e -> mainFrame.resetGraph());
        addItem(menu, "Abrir...", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.openOpenGraphDialog());
        addItem(menu, "Guardar", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.saveGraph());
        addItem(menu, "Volver", KeyEvent.VK_Z, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.undo());
    }

    private JMenu buildZoomSubMenu() {
        JMenu zoomMenu = createMenu("Zoom", KeyEvent.VK_Z);
        addItem(zoomMenu, "Acercar", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.zoomIn());
        addItem(zoomMenu, "Alejar", KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.zoomOut());
        addItem(zoomMenu, "Tamaño original", KeyEvent.VK_T, KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.resetZoom());
        return zoomMenu;
    }

    private void buildHelpMenu() {
        JMenu menu = createMenu("Ayuda", KeyEvent.VK_A);
        addItem(menu, "Configuración", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), e -> mainFrame.openConfigDialog());
        menu.add(buildLanguageSubMenu());
        addItem(menu, "¿Cómo funciona?", KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), e -> mainFrame.openHowItWorksDialog());
        addItem(menu, "Acerca de...", KeyEvent.VK_I, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), e -> mainFrame.openAboutDialog());
        add(menu);
    }

    private JMenu buildLanguageSubMenu() {
        JMenu langMenu = createMenu("Idioma", KeyEvent.VK_I);
        addItem(langMenu, "Español", KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), e -> mainFrame.updateStatusMessage("Idioma cambiado a Español"));
        addItem(langMenu, "English", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), e -> mainFrame.updateStatusMessage("Language changed to English"));
        addItem(langMenu, "Cargar idioma...", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), e -> mainFrame.openLoadLanguageDialog());
        return langMenu;
    }

    private JMenu createMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private void addItem(JMenu parent, String text, int mnemonic, KeyStroke accel, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        item.setAccelerator(accel);
        item.addActionListener(listener);
        parent.add(item);
    }
}