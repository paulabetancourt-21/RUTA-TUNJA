package co.edu.uptc.view.main;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import co.edu.uptc.presenter.MapPresenter;
import co.edu.uptc.view.MapView;
import co.edu.uptc.view.help.AboutDialog;
import co.edu.uptc.view.help.ConfigDialog;
import co.edu.uptc.view.help.HowItWorksDialog;
import co.edu.uptc.view.help.LoadLanguageDialog;
import co.edu.uptc.view.map.MapPanelImpl;
import co.edu.uptc.view.map.MapStyle;
import co.edu.uptc.view.options.OpenGraphDialog;
import co.edu.uptc.view.route.CalculateRouteDialog;
import co.edu.uptc.view.route.SaveRouteDialog;
import co.edu.uptc.view.via.CreateEdgeDialog;
import co.edu.uptc.view.via.CreateNodeDialog;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainFrame extends JFrame implements MapView {

    private static final double ZOOM_STEP = 1.2;

    private final LanguageService languageService;
    private final StatusBar statusBar;
    private final MapPanelImpl mapPanel;
    private MapPresenter presenter;

    public MainFrame(LanguageService languageService, ConfigService configService) {
        this.languageService = languageService;
        this.statusBar = new StatusBar(languageService);
        this.mapPanel = new MapPanelImpl(configService, languageService);
        initLayout();
        registerEscapeKey();
    }

    public void setPresenter(MapPresenter presenter) {
        this.presenter = presenter;
        this.mapPanel.setClickListener(presenter);
        refreshMenuBar();
    }

    private void initLayout() {
        configureWindow();
        add(createScrollPane(), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane((JPanel) mapPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private void configureWindow() {
        setTitle(languageService.getText("app.title"));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void registerEscapeKey() {
        JRootPane rootPane = getRootPane();
        String actionKey = "cancelInteraction";
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, actionKey);
        rootPane.getActionMap().put(actionKey, new CancelAction());
    }

    @Override
    public void showCreateNodeDialog(double latitude, double longitude) {
        CreateNodeDialog dialog = new CreateNodeDialog(this, languageService);
        dialog.setCoordinates(latitude, longitude);
        dialog.setVisible(true);
        forwardCreateNode(dialog, latitude, longitude);
    }

    private void forwardCreateNode(CreateNodeDialog dialog, double latitude, double longitude) {
        if (dialog.isConfirmed()) {
            presenter.createNode(latitude, longitude, dialog.getNodeName());
        } else {
            presenter.notifyCancelled();
        }
    }

    @Override
    public void showCreateEdgeDialog(Node source, Node destination, double distanceKm) {
        CreateEdgeDialog dialog = new CreateEdgeDialog(this, languageService);
        dialog.setEdgeData(describeNode(source), describeNode(destination), distanceKm);
        dialog.setVisible(true);
        forwardCreateEdge(dialog, source, destination);
    }

    private void forwardCreateEdge(CreateEdgeDialog dialog, Node source, Node destination) {
        if (dialog.isConfirmed()) {
            presenter.createEdge(source, destination, dialog.isOneWay());
        } else {
            presenter.notifyCancelled();
        }
    }

    @Override
    public void showCalculateRouteDialog(Node origin, Node destination) {
        CalculateRouteDialog dialog = new CalculateRouteDialog(this, languageService);
        dialog.setRouteEndpoints(describeNode(origin), describeNode(destination));
        dialog.setVisible(true);
        forwardCalculateRoute(dialog, origin, destination);
    }

    private void forwardCalculateRoute(CalculateRouteDialog dialog, Node origin, Node destination) {
        if (dialog.isConfirmed()) {
            presenter.calculateRoute(origin, destination);
        } else {
            presenter.notifyCancelled();
        }
    }

    @Override
    public void showSaveRouteDialog(String routeSummary) {
        SaveRouteDialog dialog = new SaveRouteDialog(this, languageService, routeSummary);
        dialog.setVisible(true);
        forwardSaveRoute(dialog);
    }

    private void forwardSaveRoute(SaveRouteDialog dialog) {
        if (dialog.isConfirmed()) {
            presenter.saveCurrentRoute(dialog.getRouteName());
        }
    }

    @Override
    public void showConfigDialog(MapStyle style) {
        ConfigDialog dialog = new ConfigDialog(this, languageService, style);
        dialog.setVisible(true);
        forwardConfig(dialog);
    }

    private void forwardConfig(ConfigDialog dialog) {
        if (dialog.isConfirmed()) {
            presenter.applyStyle(dialog.buildStyle());
        }
    }

    @Override
    public void showLoadLanguageDialog() {
        LoadLanguageDialog dialog = new LoadLanguageDialog(this, languageService);
        dialog.setVisible(true);
        forwardLoadedLanguage(dialog);
    }

    private void forwardLoadedLanguage(LoadLanguageDialog dialog) {
        String languageCode = dialog.getLoadedLanguageCode();
        if (languageCode != null) {
            presenter.changeLanguage(languageCode);
        }
    }

    @Override
    public void showOpenGraphDialog() {
        new OpenGraphDialog(languageService).showOpenDialog(this);
    }

    @Override
    public void showHowItWorksDialog() {
        new HowItWorksDialog(this, languageService).setVisible(true);
    }

    @Override
    public void showAboutDialog() {
        new AboutDialog(this, languageService).setVisible(true);
    }

    @Override
    public void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public boolean confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @Override
    public void refreshTexts() {
        setTitle(languageService.getText("app.title"));
        refreshMenuBar();
        statusBar.applyLanguage();
        mapPanel.repaint();
        showLanguageChanged();
    }

    private void showLanguageChanged() {
        updateStatusMessage(languageService.getText("status.languageChanged"));
    }

    @Override
    public void refreshMenuBar() {
        setJMenuBar(new MainMenuBar(languageService, presenter));
        revalidate();
        repaint();
    }

    private String describeNode(Node node) {
        return node.hasName() ? node.getName() : String.valueOf(node.getId());
    }

    @Override
    public void showGraph(Graph graph) {
        mapPanel.setGraph(graph);
    }

    @Override
    public void showRoute(List<Node> route) {
        mapPanel.setRoute(route);
    }

    @Override
    public void clearRoute() {
        mapPanel.setRoute(List.of());
    }

    @Override
    public void setSelectedNode(Node node) {
        mapPanel.setSelectedNode(node);
    }

    @Override
    public void setStyle(MapStyle style) {
        mapPanel.setStyle(style);
    }

    @Override
    public MapStyle getStyle() {
        return mapPanel.getStyle();
    }

    @Override
    public void zoomIn() {
        applyZoom(mapPanel.getZoomFactor() * ZOOM_STEP);
    }

    @Override
    public void zoomOut() {
        applyZoom(mapPanel.getZoomFactor() / ZOOM_STEP);
    }

    @Override
    public void resetZoom() {
        applyZoom(1.0);
    }

    private void applyZoom(double zoomFactor) {
        mapPanel.setZoomFactor(zoomFactor);
        reportZoom();
    }

    private void reportZoom() {
        int percentage = (int) (mapPanel.getZoomFactor() * 100);
        updateStatusMessage(String.format(languageService.getText("status.zoomChanged"), percentage));
    }

    @Override
    public void repaintMap() {
        mapPanel.repaint();
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

    private class CancelAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent event) {
            presenter.cancelInteraction();
        }
    }
}
