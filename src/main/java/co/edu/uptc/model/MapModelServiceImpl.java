package co.edu.uptc.model;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.persistence.CsvLoaderService;
import co.edu.uptc.persistence.CsvLoaderServiceImpl;
import co.edu.uptc.persistence.SavedRouteService;
import co.edu.uptc.persistence.SavedRouteServiceImpl;
import co.edu.uptc.util.GeoUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapModelServiceImpl implements MapModelService {

    private final ConfigService configService;
    private final LanguageService languageService;
    private final GraphService graphService = new GraphServiceImpl();
    private final CsvLoaderService csvLoader = new CsvLoaderServiceImpl(graphService);
    private final DijkstraService dijkstraService = new DijkstraServiceImpl(graphService);
    private final RouteService routeService = new RouteServiceImpl();
    private final SavedRouteService savedRouteService;
    private InteractionMode mode = InteractionMode.NONE;
    private Node pendingNode;
    private List<Node> currentRoute = new ArrayList<>();
    private double currentDistanceKm;

    public MapModelServiceImpl(ConfigService configService, LanguageService languageService) {
        this.configService = configService;
        this.languageService = languageService;
        this.savedRouteService = new SavedRouteServiceImpl(configService);
    }

    @Override
    public GraphLoadResult loadGraph() {
        try {
            loadCsvGraph();
            return new GraphLoadResult(graphService.getGraph(), languageService.getText("status.ready"));
        } catch (IOException exception) {
            String message = languageService.getText("status.graphLoadError");
            return new GraphLoadResult(graphService.getGraph(), message);
        }
    }

    private void loadCsvGraph() throws IOException {
        csvLoader.loadNodes(configService.getString("graph.nodesCsv"));
        csvLoader.loadEdges(configService.getString("graph.edgesCsv"));
    }

    @Override
    public GraphResetResult resetGraph() {
        graphService.clearGraph();
        resetInteraction();
        clearRouteState();
        return new GraphResetResult(graphService.getGraph(), languageService.getText("status.newGraph"));
    }

    @Override
    public String createNode(double latitude, double longitude, String name) {
        Node node = new Node(graphService.nextNodeId(), name, latitude, longitude);
        graphService.addNode(node);
        resetInteraction();
        return languageService.getText("status.nodeCreated");
    }

    @Override
    public String createEdge(Node source, Node destination, boolean oneWay) {
        graphService.addEdge(buildEdge(source, destination, oneWay));
        resetInteraction();
        return languageService.getText("status.edgeCreated");
    }

    private Edge buildEdge(Node source, Node destination, boolean oneWay) {
        return new Edge(source, destination, oneWay, distanceTo(source, destination));
    }

    private double distanceTo(Node source, Node destination) {
        return GeoUtil.calculateDistance(source, destination);
    }

    @Override
    public String startCreateNode() {
        return enterMode(InteractionMode.CREATE_NODE, "status.clickNodeHint");
    }

    @Override
    public String startCreateEdge() {
        return enterMode(InteractionMode.CREATE_EDGE, "status.clickOriginNode");
    }

    @Override
    public String startCalculateRoute() {
        return enterMode(InteractionMode.CALCULATE_ROUTE, "status.clickOriginNode");
    }

    private String enterMode(InteractionMode newMode, String hintKey) {
        resetInteraction();
        this.mode = newMode;
        return languageService.getText(hintKey);
    }

    @Override
    public InteractionResult handleClick(double latitude, double longitude, Node node) {
        if (mode == InteractionMode.CREATE_NODE) {
            return InteractionResult.createNode(latitude, longitude);
        }
        return handleNodeClick(node);
    }

    private InteractionResult handleNodeClick(Node node) {
        if (mode == InteractionMode.CREATE_EDGE) {
            return handleEdgeClick(node);
        }
        if (mode == InteractionMode.CALCULATE_ROUTE) {
            return handleRouteClick(node);
        }
        return InteractionResult.none();
    }

    private InteractionResult handleEdgeClick(Node node) {
        if (node == null) {
            return invalidNode();
        }
        if (pendingNode == null) {
            return selectPendingNode(node);
        }
        return isSameAsPending(node)
                ? invalidNode()
                : InteractionResult.createEdge(pendingNode, node, distanceTo(pendingNode, node));
    }

    private InteractionResult handleRouteClick(Node node) {
        if (node == null) {
            return invalidNode();
        }
        if (pendingNode == null) {
            return selectPendingNode(node);
        }
        return isSameAsPending(node)
                ? invalidNode()
                : InteractionResult.calculateRoute(pendingNode, node);
    }

    private InteractionResult selectPendingNode(Node node) {
        pendingNode = node;
        return InteractionResult.selectNode(node, languageService.getText("status.clickDestinationNode"));
    }

    private InteractionResult invalidNode() {
        return InteractionResult.message(languageService.getText("status.invalidNode"));
    }

    private boolean isSameAsPending(Node node) {
        return pendingNode != null && pendingNode.getId() == node.getId();
    }

    @Override
    public Optional<String> cancelInteraction() {
        if (mode == InteractionMode.NONE) {
            return Optional.empty();
        }
        resetInteraction();
        return Optional.of(languageService.getText("status.actionCancelled"));
    }

    @Override
    public String cancelDialog() {
        resetInteraction();
        return languageService.getText("status.actionCancelled");
    }

    private void resetInteraction() {
        resetMode();
        pendingNode = null;
    }

    private void resetMode() {
        this.mode = InteractionMode.NONE;
    }

    @Override
    public RouteResult calculateRoute(Node origin, Node destination) {
        List<Node> route = dijkstraService.calculateShortestPath(origin, destination);
        resetInteraction();
        if (route.isEmpty()) {
            return RouteResult.empty(languageService.getText("message.noRoute"));
        }
        rememberRoute(route);
        return RouteResult.route(route, buildRouteMessage(route));
    }

    @Override
    public RouteResult resolveSavedRoute(SavedRoute savedRoute) {
        List<Node> route = toRouteNodes(savedRoute);
        if (route.size() < 2) {
            return RouteResult.empty(languageService.getText("status.savedRouteUnavailable"));
        }
        rememberRoute(route);
        return RouteResult.route(route, buildRouteMessage(route));
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

    private List<Node> toRouteNodes(SavedRoute savedRoute) {
        List<Node> route = new ArrayList<>();
        for (Integer nodeId : savedRoute.getNodeIds()) {
            appendNode(route, nodeId);
        }
        return isComplete(route, savedRoute) ? route : new ArrayList<>();
    }

    private void appendNode(List<Node> route, Integer nodeId) {
        Node node = graphService.getGraph().getNodes().get(nodeId);
        if (node != null) {
            route.add(node);
        }
    }

    private boolean isComplete(List<Node> route, SavedRoute savedRoute) {
        return route.size() == savedRoute.getNodeIds().size();
    }

    @Override
    public SaveRouteInfo getSaveInfo() {
        if (currentRoute.size() < 2) {
            return SaveRouteInfo.unavailable(languageService.getText("status.noRouteToSave"));
        }
        return SaveRouteInfo.available(buildRouteSummary(currentRoute));
    }

    @Override
    public String saveRoute(String name) {
        savedRouteService.save(buildSavedRoute(name));
        return languageService.getText("status.routeSaved");
    }

    private SavedRoute buildSavedRoute(String name) {
        List<Integer> nodeIds = currentRoute.stream().map(Node::getId).toList();
        return new SavedRoute(name, nodeIds, currentDistanceKm);
    }

    @Override
    public List<SavedRoute> getSavedRoutes() {
        return savedRouteService.findAll();
    }

    @Override
    public String clearRoute() {
        clearRouteState();
        return languageService.getText("status.routeCleared");
    }

    private void clearRouteState() {
        currentRoute = new ArrayList<>();
        currentDistanceKm = 0.0;
    }
}
