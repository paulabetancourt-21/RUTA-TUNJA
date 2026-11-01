package co.edu.uptc.view.main;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.view.MapView;
import co.edu.uptc.view.help.AboutDialog;
import co.edu.uptc.view.help.ConfigDialog;
import co.edu.uptc.view.help.HowItWorksDialog;
import co.edu.uptc.view.help.LoadLanguageDialog;
import co.edu.uptc.view.map.MapPanelImpl;
import co.edu.uptc.view.options.OpenGraphDialog;
import co.edu.uptc.view.route.CalculateRouteDialog;
import co.edu.uptc.view.route.SaveRouteDialog;
import co.edu.uptc.view.via.CreateEdgeDialog;
import co.edu.uptc.view.via.CreateNodeDialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class MainFrame extends JFrame implements MapView {

    private final LanguageService languageService;
    private final ConfigService configService;
    private final StatusBar statusBar;
    private final MapPanelImpl mapPanel;

    public MainFrame(LanguageService languageService, ConfigService configService) {
        this.languageService = languageService;
        this.configService = configService;
        this.statusBar = new StatusBar();
        this.mapPanel = new MapPanelImpl(configService);
        initLayout();
    }

    private void initLayout() {
        configureWindow();
        setJMenuBar(new MainMenuBar(languageService, this));
        
        JScrollPane scrollPane = new JScrollPane((JPanel) mapPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void configureWindow() {
        setTitle("Mapa Interactivo Tunja");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    public void openCreateNodeDialog() {
        updateStatusMessage("Haz clic en el mapa donde va el nodo. (Esc para cancelar)");
        new CreateNodeDialog(this, languageService, 5.536200, -73.360600).setVisible(true);
    }

    public void openCreateEdgeDialog() {
        new CreateEdgeDialog(this, languageService, "Nodo 9", "Nodo 10", 0.354).setVisible(true);
    }

    public void openCalculateRouteDialog() {
        new CalculateRouteDialog(this, languageService, "Nodo 16", "Terminal").setVisible(true);
    }

    public void openSaveRouteDialog() {
        new SaveRouteDialog(this, languageService, "Nodo 16 -> Terminal (2.13 km)").setVisible(true);
    }

    public void openOpenGraphDialog() {
        new OpenGraphDialog().showOpenDialog(this);
    }

    public void openConfigDialog() {
        new ConfigDialog(this, languageService, configService).setVisible(true);
    }

    public void openLoadLanguageDialog() {
        new LoadLanguageDialog(this).setVisible(true);
    }

    public void openHowItWorksDialog() {
        new HowItWorksDialog(this).setVisible(true);
    }

    public void openAboutDialog() {
        new AboutDialog(this).setVisible(true);
    }

    public void clearRoute() {
        updateStatusMessage("Ruta limpiada.");
    }

    public void resetGraph() {
        int opt = JOptionPane.showConfirmDialog(this, 
                "¿Desea crear un nuevo grafo? Los cambios no guardados se perderán.", 
                "Nuevo grafo", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            updateStatusMessage("Nuevo grafo iniciado.");
        }
    }

    public void saveGraph() {
        JOptionPane.showMessageDialog(this, 
                "Grafo guardado exitosamente en nodes.csv y edges.csv.", 
                "Guardar Grafo", JOptionPane.INFORMATION_MESSAGE);
        updateStatusMessage("Grafo guardado.");
    }

    public void undo() {
        updateStatusMessage("Última acción deshecha.");
    }

    public void zoomIn() {
        mapPanel.setZoomFactor(mapPanel.getZoomFactor() * 1.2);
        updateStatusMessage("Zoom " + (int)(mapPanel.getZoomFactor() * 100) + "%");
    }

    public void zoomOut() {
        mapPanel.setZoomFactor(mapPanel.getZoomFactor() / 1.2);
        updateStatusMessage("Zoom " + (int)(mapPanel.getZoomFactor() * 100) + "%");
    }

    public void resetZoom() {
        mapPanel.setZoomFactor(1.0);
        updateStatusMessage("Zoom 100%");
    }

    @Override
    public void start() {
        setVisible(true);
    }

    @Override
    public void updateStatusMessage(String message) {
        statusBar.setStatusMessage(message);
    }

    @Override
    public void updateCoordinates(double lat, double lon, int zoomPercentage) {
        statusBar.setCoordinates(lat, lon, zoomPercentage);
    }
}