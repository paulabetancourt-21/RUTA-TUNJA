package co.edu.uptc.view.main;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.model.DijkstraService;
import co.edu.uptc.model.DijkstraServiceImpl;
import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.GraphService;
import co.edu.uptc.model.GraphServiceImpl;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.RouteService;
import co.edu.uptc.model.RouteServiceImpl;
import co.edu.uptc.model.SavedRoute;
import co.edu.uptc.persistence.CsvLoaderService;
import co.edu.uptc.persistence.CsvLoaderServiceImpl;
import co.edu.uptc.persistence.SavedRouteService;
import co.edu.uptc.persistence.SavedRouteServiceImpl;
import co.edu.uptc.util.GeoUtil;
import co.edu.uptc.view.MapView;
import co.edu.uptc.view.help.AboutDialog;
import co.edu.uptc.view.help.ConfigDialog;
import co.edu.uptc.view.help.HowItWorksDialog;
import co.edu.uptc.view.help.LoadLanguageDialog;
import co.edu.uptc.view.map.MapClickListener;
import co.edu.uptc.view.map.MapPanelImpl;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements MapView, MapClickListener {

    private static final double ZOOM_STEP = 1.2;

    private final LanguageService languageService;
    private final ConfigService configService;
    private final StatusBar statusBar;
    private final MapPanelImpl mapPanel;
    private final GraphService graphService = new GraphServiceImpl();
    private final CsvLoaderService csvLoader = new CsvLoaderServiceImpl(graphService);
    private final DijkstraService dijkstraService = new DijkstraServiceImpl(graphService);
    private final RouteService routeService = new RouteServiceImpl();
    private final SavedRouteService savedRouteService;
    private InteractionMode mode = InteractionMode.NONE;
    private Node pendingNode;
    private List<Node> currentRoute = new ArrayList<>();
    private double currentDistanceKm;

    public MainFrame(LanguageService languageService, ConfigService configService) {
        this.languageService = languageService;
        this.configService = configService;
        this.savedRouteService = new SavedRouteServiceImpl(configService);
        this.statusBar = new StatusBar(languageService);
        this.mapPanel = new MapPanelImpl(configService, languageService);
        initLayout();
        initInteraction();
        loadGraph();
    }

    private void initLayout() {
        configureWindow();
        setJMenuBar(new MainMenuBar(languageService, this));
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

    private void initInteraction() {
        mapPanel.setClickListener(this);
        registerEscapeKey();
    }

    private void registerEscapeKey() {
        JRootPane rootPane = getRootPane();
        String actionKey = "cancelInteraction";
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, actionKey);
        rootPane.getActionMap().put(actionKey, new CancelAction());
    }

    private void loadGraph() {
        try {
            csvLoader.loadNodes(configService.getString("graph.nodesCsv"));
            csvLoader.loadEdges(configService.getString("graph.edgesCsv"));
            mapPanel.setGraph(graphService.getGraph());
        } catch (IOException exception) {
            updateStatusMessage(languageService.getText("status.graphLoadError"));
        }
    }

    public void startCreateNode() {
        enterMode(InteractionMode.CREATE_NODE, "status.clickNodeHint");
    }

    public void startCreateEdge() {
        enterMode(InteractionMode.CREATE_EDGE, "status.clickOriginNode");
    }

    public void startCalculateRoute() {
        enterMode(InteractionMode.CALCULATE_ROUTE, "status.clickOriginNode");
    }

    private void enterMode(InteractionMode newMode, String hintKey) {
        resetMode();
        this.mode = newMode;
        updateStatusMessage(languageService.getText(hintKey));
    }

    @Override
    public void onMapClicked(double latitude, double longitude, Node node) {
        if (mode == InteractionMode.CREATE_NODE) {
            openCreateNodeDialog(latitude, longitude);
        } else if (mode == InteractionMode.CREATE_EDGE) {
            handleEdgeClick(node);
        } else if (mode == InteractionMode.CALCULATE_ROUTE) {
            handleRouteClick(node);
        }
    }

    private void openCreateNodeDialog(double latitude, double longitude) {
        CreateNodeDialog dialog = new CreateNodeDialog(this, languageService);
        dialog.setCoordinates(latitude, longitude);
        dialog.setVisible(true);
        finishCreateNode(dialog, latitude, longitude);
    }

    private void finishCreateNode(CreateNodeDialog dialog, double latitude, double longitude) {
        if (dialog.isConfirmed()) {
            addNode(latitude, longitude, dialog.getNodeName());
        } else {
            updateStatusMessage(languageService.getText("status.actionCancelled"));
        }
        resetMode();
    }

    private void addNode(double latitude, double longitude, String name) {
        Node node = new Node(graphService.nextNodeId(), name, latitude, longitude);
        graphService.addNode(node);
        mapPanel.repaint();
        updateStatusMessage(languageService.getText("status.nodeCreated"));
    }

    private void handleEdgeClick(Node node) {
        if (node == null) {
            showInvalidNode();
        } else if (pendingNode == null) {
            selectPendingNode(node);
        } else {
            completeEdgeSelection(node);
        }
    }

    private void completeEdgeSelection(Node node) {
        if (pendingNode.getId() == node.getId()) {
            showInvalidNode();
            return;
        }
        openCreateEdgeDialog(pendingNode, node);
    }

    private void openCreateEdgeDialog(Node source, Node destination) {
        CreateEdgeDialog dialog = new CreateEdgeDialog(this, languageService);
        double distance = GeoUtil.calculateDistance(source, destination);
        dialog.setEdgeData(describeNode(source), describeNode(destination), distance);
        dialog.setVisible(true);
        finishCreateEdge(dialog, source, destination);
    }

    private void finishCreateEdge(CreateEdgeDialog dialog, Node source, Node destination) {
        if (dialog.isConfirmed()) {
            graphService.addEdge(buildEdge(source, destination, dialog.isOneWay()));
            mapPanel.repaint();
            updateStatusMessage(languageService.getText("status.edgeCreated"));
        } else {
            updateStatusMessage(languageService.getText("status.actionCancelled"));
        }
        resetMode();
    }

    private Edge buildEdge(Node source, Node destination, boolean oneWay) {
        double weight = GeoUtil.calculateDistance(source, destination);
        return new Edge(source, destination, oneWay, weight);
    }

    private void handleRouteClick(Node node) {
        if (node == null) {
            showInvalidNode();
        } else if (pendingNode == null) {
            selectPendingNode(node);
        } else {
            completeRouteSelection(node);
        }
    }

    private void completeRouteSelection(Node node) {
        if (pendingNode.getId() == node.getId()) {
            showInvalidNode();
            return;
        }
        openCalculateRouteDialog(pendingNode, node);
    }

    private void selectPendingNode(Node node) {
        pendingNode = node;
        mapPanel.setSelectedNode(node);
        updateStatusMessage(languageService.getText("status.clickDestinationNode"));
    }

    private void showInvalidNode() {
        updateStatusMessage(languageService.getText("status.invalidNode"));
    }

    private void openCalculateRouteDialog(Node origin, Node destination) {
        CalculateRouteDialog dialog = new CalculateRouteDialog(this, languageService);
        dialog.setRouteEndpoints(describeNode(origin), describeNode(destination));
        dialog.setVisible(true);
        finishCalculateRoute(dialog, origin, destination);
    }

    private void finishCalculateRoute(CalculateRouteDialog dialog, Node origin, Node destination) {
        if (dialog.isConfirmed()) {
            calculateRoute(origin, destination);
        } else {
            updateStatusMessage(languageService.getText("status.actionCancelled"));
        }
        resetMode();
    }

    private void calculateRoute(Node origin, Node destination) {
        List<Node> route = dijkstraService.calculateShortestPath(origin, destination);
        if (route.isEmpty()) {
            updateStatusMessage(languageService.getText("message.noRoute"));
            return;
        }
        showRoute(route);
        rememberRoute(route);
        updateStatusMessage(buildRouteMessage(route));
    }

    private void rememberRoute(List<Node> route) {
        currentRoute = new ArrayList<>(route);
        currentDistanceKm = routeService.calculateTotalDistance(route);
    }

    private String buildRouteMessage(List<Node> route) {
        String template = languageService.getText("status.routeCalculated");
        return String.format(template, buildRouteSummary(route));
    }

    private String buildRouteSummary(List<Node> route) {
        String nodeIds = buildNodeSequence(route);
        double distance = routeService.calculateTotalDistance(route);
        return String.format(languageService.getText("status.routeSummary"), nodeIds, distance);
    }

    private String buildNodeSequence(List<Node> route) {
        StringBuilder sequence = new StringBuilder();
        route.forEach(node -> appendNodeId(sequence, node));
        return sequence.toString();
    }

    private void appendNodeId(StringBuilder sequence, Node node) {
        if (sequence.length() > 0) {
            sequence.append(languageService.getText("map.routeSeparator"));
        }
        sequence.append(describeNode(node));
    }

    private String describeNode(Node node) {
        return node.hasName() ? node.getName() : String.valueOf(node.getId());
    }

    public void cancelMode() {
        if (mode == InteractionMode.NONE) {
            return;
        }
        resetMode();
        updateStatusMessage(languageService.getText("status.actionCancelled"));
    }

    private void resetMode() {
        this.mode = InteractionMode.NONE;
        this.pendingNode = null;
        mapPanel.setSelectedNode(null);
    }

    public void openSaveRouteDialog() {
        if (currentRoute.size() < 2) {
            updateStatusMessage(languageService.getText("status.noRouteToSave"));
            return;
        }
        showSaveRouteDialog();
    }

    private void showSaveRouteDialog() {
        SaveRouteDialog dialog = createSaveRouteDialog();
        dialog.setVisible(true);
        saveRouteIfConfirmed(dialog);
    }

    private SaveRouteDialog createSaveRouteDialog() {
        return new SaveRouteDialog(this, languageService, buildRouteSummary(currentRoute));
    }

    private void saveRouteIfConfirmed(SaveRouteDialog dialog) {
        if (!dialog.isConfirmed()) {
            return;
        }
        savedRouteService.save(buildSavedRoute(dialog.getRouteName()));
        refreshMenuBar();
        updateStatusMessage(languageService.getText("status.routeSaved"));
    }

    private SavedRoute buildSavedRoute(String name) {
        List<Integer> nodeIds = currentRoute.stream().map(Node::getId).toList();
        return new SavedRoute(name, nodeIds, currentDistanceKm);
    }

    public List<SavedRoute> getSavedRoutes() {
        return savedRouteService.findAll();
    }

    public void showSavedRoute(SavedRoute savedRoute) {
        List<Node> route = toRouteNodes(savedRoute);
        if (route.size() < 2) {
            updateStatusMessage(languageService.getText("status.savedRouteUnavailable"));
            return;
        }
        showRoute(route);
        rememberRoute(route);
        updateStatusMessage(buildRouteMessage(route));
    }

    private List<Node> toRouteNodes(SavedRoute savedRoute) {
        List<Node> route = new ArrayList<>();
        for (Integer nodeId : savedRoute.getNodeIds()) {
            Node node = graphService.getGraph().getNodes().get(nodeId);
            if (node == null) {
                return new ArrayList<>();
            }
            route.add(node);
        }
        return route;
    }

    private void refreshMenuBar() {
        setJMenuBar(new MainMenuBar(languageService, this));
        revalidate();
        repaint();
    }

    public void openOpenGraphDialog() {
        new OpenGraphDialog(languageService).showOpenDialog(this);
    }

    public void openConfigDialog() {
        ConfigDialog dialog = new ConfigDialog(this, languageService, mapPanel.getStyle());
        dialog.setVisible(true);
        applyConfig(dialog);
    }

    private void applyConfig(ConfigDialog dialog) {
        if (!dialog.isConfirmed()) {
            return;
        }
        mapPanel.setStyle(dialog.buildStyle());
        updateStatusMessage(languageService.getText("status.configurationUpdated"));
    }

    public void openLoadLanguageDialog() {
        new LoadLanguageDialog(this, languageService).setVisible(true);
    }

    public void openHowItWorksDialog() {
        new HowItWorksDialog(this, languageService).setVisible(true);
    }

    public void openAboutDialog() {
        new AboutDialog(this, languageService).setVisible(true);
    }

    public void changeLanguage(String languageCode) {
        languageService.setLanguage(languageCode);
        refreshTexts();
    }

    private void refreshTexts() {
        setTitle(languageService.getText("app.title"));
        setJMenuBar(new MainMenuBar(languageService, this));
        statusBar.applyLanguage();
        mapPanel.repaint();
        updateStatusMessage(languageService.getText("status.languageChanged"));
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
        currentRoute = new ArrayList<>();
        currentDistanceKm = 0.0;
        mapPanel.setRoute(new ArrayList<>());
        updateStatusMessage(languageService.getText("status.routeCleared"));
    }

    public void resetGraph() {
        int option = JOptionPane.showConfirmDialog(this,
                languageService.getText("message.confirmNewGraph"),
                languageService.getText("message.newGraphTitle"),
                JOptionPane.YES_NO_OPTION);
        handleNewGraph(option);
    }

    private void handleNewGraph(int option) {
        if (option == JOptionPane.YES_OPTION) {
            updateStatusMessage(languageService.getText("status.newGraph"));
        }
    }

    public void saveGraph() {
        JOptionPane.showMessageDialog(this,
                languageService.getText("message.graphSavedMessage"),
                languageService.getText("message.graphSavedTitle"),
                JOptionPane.INFORMATION_MESSAGE);
        updateStatusMessage(languageService.getText("status.graphSaved"));
    }

    public void undo() {
        updateStatusMessage(languageService.getText("status.undo"));
    }

    public void zoomIn() {
        mapPanel.setZoomFactor(mapPanel.getZoomFactor() * ZOOM_STEP);
        reportZoom();
    }

    public void zoomOut() {
        mapPanel.setZoomFactor(mapPanel.getZoomFactor() / ZOOM_STEP);
        reportZoom();
    }

    public void resetZoom() {
        mapPanel.setZoomFactor(1.0);
        reportZoom();
    }

    private void reportZoom() {
        int percentage = (int) (mapPanel.getZoomFactor() * 100);
        updateStatusMessage(String.format(languageService.getText("status.zoomChanged"), percentage));
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
            cancelMode();
        }
    }
}
