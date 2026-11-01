package co.edu.uptc.view.main;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.GraphLoadResult;
import co.edu.uptc.model.GraphResetResult;
import co.edu.uptc.model.InteractionResult;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.RouteResult;
import co.edu.uptc.model.SaveRouteInfo;
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
import java.io.File;
import java.util.List;
import java.util.Optional;

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
    public void showGraphLoad(GraphLoadResult result) {
        showGraph(result.graph(), result.message());
    }

    @Override
    public void showGraphReset(GraphResetResult result) {
        showGraph(result.graph(), result.message());
    }

    private void showGraph(Graph graph, String message) {
        mapPanel.setGraph(graph);
        updateStatusMessage(message);
    }

    @Override
    public void showInteraction(InteractionResult result) {
        switch (result.type()) {
            case CREATE_NODE -> showCreateNodeDialog(result.latitude(), result.longitude());
            case CREATE_EDGE -> showCreateEdgeDialog(result.origin(), result.destination(), result.distanceKm());
            case CALCULATE_ROUTE -> showCalculateRouteDialog(result.origin(), result.destination());
            case SELECT_NODE -> showSelectedNode(result);
            case MESSAGE -> updateStatusMessage(result.message());
            default -> { }
        }
    }

    private void showSelectedNode(InteractionResult result) {
        mapPanel.setSelectedNode(result.selectedNode());
        updateStatusMessage(result.message());
    }

    @Override
    public void showGraphChanged(String message) {
        mapPanel.repaint();
        updateStatusMessage(message);
    }

    @Override
    public void showRouteResult(RouteResult result) {
        mapPanel.setRoute(result.route());
        updateStatusMessage(result.message());
    }

    @Override
    public void showOptionalMessage(Optional<String> message) {
        message.ifPresent(this::updateStatusMessage);
    }

    @Override
    public void showSaveRouteInfo(SaveRouteInfo info) {
        if (info.available()) {
            showSaveRouteDialog(info.summary());
        } else {
            updateStatusMessage(info.message());
        }
    }

    @Override
    public void showSavedRoute(String message) {
        refreshMenuBar();
        updateStatusMessage(message);
    }

    @Override
    public void showGraphSaved() {
        showMessage(languageService.getText("message.graphSavedMessage"),
                languageService.getText("message.graphSavedTitle"));
        updateStatusMessage(languageService.getText("status.graphSaved"));
    }

    @Override
    public void showUndo() {
        updateStatusMessage(languageService.getText("status.undo"));
    }

    @Override
    public void clearRoute(String message) {
        mapPanel.setRoute(List.of());
        updateStatusMessage(message);
    }

    @Override
    public void confirmNewGraph() {
        int option = JOptionPane.showConfirmDialog(this,
                languageService.getText("message.confirmNewGraph"),
                languageService.getText("message.newGraphTitle"),
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            presenter.createNewGraph();
        }
    }

    private void showCreateNodeDialog(double latitude, double longitude) {
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

    private void showCreateEdgeDialog(Node source, Node destination, double distanceKm) {
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

    private void showCalculateRouteDialog(Node origin, Node destination) {
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

    private void showSaveRouteDialog(String routeSummary) {
        SaveRouteDialog dialog = new SaveRouteDialog(this, languageService, routeSummary);
        dialog.setVisible(true);
        forwardSaveRoute(dialog);
    }

    private void forwardSaveRoute(SaveRouteDialog dialog) {
        if (dialog.isConfirmed()) {
            presenter.saveCurrentRoute(dialog.getRouteName());
        }
    }

    private void showLanguageChanged() {
        updateStatusMessage(languageService.getText("status.languageChanged"));
    }

    private String describeNode(Node node) {
        return node.hasName() ? node.getName() : String.valueOf(node.getId());
    }

    @Override
    public void showConfigDialog() {
        ConfigDialog dialog = new ConfigDialog(this, languageService, mapPanel.getStyle());
        dialog.setVisible(true);
        forwardConfig(dialog);
    }

    private void forwardConfig(ConfigDialog dialog) {
        if (dialog.isConfirmed()) {
            presenter.applyStyle(dialog.buildStyle());
        }
    }

    @Override
    public void applyStyle(MapStyle style) {
        mapPanel.setStyle(style);
        updateStatusMessage(languageService.getText("status.configurationUpdated"));
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
        File selectedFolder = new OpenGraphDialog(languageService).showOpenDialog(this);
        if (selectedFolder != null) {
            presenter.loadGraphFromFolder(selectedFolder);
        }
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
    public void changeLanguage(String languageCode) {
        languageService.setLanguage(languageCode);
        refreshTexts();
    }

    private void refreshTexts() {
        setTitle(languageService.getText("app.title"));
        refreshMenuBar();
        statusBar.applyLanguage();
        mapPanel.repaint();
        showLanguageChanged();
    }

    private void refreshMenuBar() {
        setJMenuBar(new MainMenuBar(languageService, presenter));
        revalidate();
        repaint();
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

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
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
