package co.edu.uptc.model;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.persistence.CsvLoaderService;
import co.edu.uptc.persistence.CsvLoaderServiceImpl;
import co.edu.uptc.persistence.SavedRouteService;
import co.edu.uptc.persistence.SavedRouteServiceImpl;
import co.edu.uptc.util.GeoUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapModelServiceImpl implements MapModelService {
    private static final Logger LOGGER = Logger.getLogger(MapModelServiceImpl.class.getName());
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
    private File activeFolder = null;
    private final Deque<Graph> history = new ArrayDeque<>();

    public MapModelServiceImpl(ConfigService configService, LanguageService languageService) {
        this.configService = configService;
        this.languageService = languageService;
        this.savedRouteService = new SavedRouteServiceImpl(configService);
    }

    private Graph createGraphSnapshot() {
        Graph snapshot = new Graph();
        copyNodes(snapshot);
        copyEdges(snapshot);
        return snapshot;
    }

    private void copyNodes(Graph snapshot) {
        graphService.getGraph().getNodes().forEach((id, node) -> snapshot.getNodes().put(id, copyNode(node)));
    }

    private Node copyNode(Node node) {
        return new Node(node.getId(), node.getName(), node.getLat(), node.getLon());
    }

    private void copyEdges(Graph snapshot) {
        graphService.getGraph().getEdges().forEach(edge -> addSnapshotEdge(snapshot, edge));
    }

    private void addSnapshotEdge(Graph snapshot, Edge edge) {
        Node source = snapshot.getNodes().get(edge.getSource().getId());
        Node destination = snapshot.getNodes().get(edge.getDestination().getId());
        if (source != null && destination != null) {
            snapshot.getEdges().add(new Edge(source, destination, edge.isOneWay(), edge.getWeight()));
        }
    }

    @Override
    public GraphLoadResult loadGraph() {
        try {
            loadCsvGraph();
            logGraphLoaded();
            return new GraphLoadResult(graphService.getGraph(), languageService.getText("status.ready"));
        } catch (IOException exception) {
            return loadGraphFailure(exception);
        }
    }

    private void loadCsvGraph() throws IOException {
        csvLoader.loadNodes(configService.getString("graph.nodesCsv"));
        csvLoader.loadEdges(configService.getString("graph.edgesCsv"));
    }

    private void logGraphLoaded() {
        Graph graph = graphService.getGraph();
        LOGGER.info("Grafo cargado: " + graph.getNodes().size() + " nodos, " + graph.getEdges().size() + " aristas");
    }

    private GraphLoadResult loadGraphFailure(IOException exception) {
        LOGGER.log(Level.SEVERE, "Error al cargar el grafo desde los archivos CSV", exception);
        String message = languageService.getText("status.graphLoadError");
        return new GraphLoadResult(graphService.getGraph(), message);
    }

    @Override
    public GraphResetResult resetGraph() {
        clearGraphState();
        LOGGER.info("Grafo limpiado solo en memoria; los CSV por defecto no se modificaron");
        return new GraphResetResult(graphService.getGraph(), languageService.getText("status.newGraph"));
    }

    private void clearGraphState() {
        history.clear();
        graphService.clearGraph();
        resetInteraction();
        clearRouteState();
        activeFolder = null;
    }

    @Override
    public String createNode(double latitude, double longitude, String name) {
        history.push(createGraphSnapshot());
        Node node = new Node(graphService.nextNodeId(), name, latitude, longitude);
        graphService.addNode(node);
        resetInteraction();
        LOGGER.info("Nodo creado: id=" + node.getId() + ", nombre=" + node.getName());
        return languageService.getText("status.nodeCreated");
    }

    @Override
    public String createEdge(Node source, Node destination, boolean oneWay) {
        history.push(createGraphSnapshot());
        Edge edge = buildEdge(source, destination, oneWay);
        graphService.addEdge(edge);
        resetInteraction();
        LOGGER.info("Arista creada: " + edge.getSource().getId() + " -> " + edge.getDestination().getId());
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
            LOGGER.warning("No se encontró ruta entre los nodos " + origin.getId() + " y " + destination.getId());
            return RouteResult.empty(languageService.getText("message.noRoute"));
        }
        rememberRoute(route);
        LOGGER.info("Ruta calculada con " + route.size() + " nodos");
        return RouteResult.route(route, buildRouteMessage(route));
    }

    @Override
    public RouteResult resolveSavedRoute(SavedRoute savedRoute) {
        List<Node> route = toRouteNodes(savedRoute);
        if (route.size() < 2) {
            LOGGER.warning("Ruta guardada no disponible: " + savedRoute.getName());
            return RouteResult.empty(languageService.getText("status.savedRouteUnavailable"));
        }
        rememberRoute(route);
        LOGGER.info("Ruta guardada cargada: " + savedRoute.getName());
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
        savedRoute.getNodeIds().forEach(id -> addRouteNode(route, id));
        return route;
    }

    private void addRouteNode(List<Node> route, Integer id) {
        Node node = findOrLoadNode(id);
        if (node != null) {
            route.add(node);
        }
    }

    private Node findOrLoadNode(Integer id) {
        Node node = graphService.getGraph().getNodes().get(id);
        return node != null ? node : loadAndRegisterNode(id);
    }

    private Node loadAndRegisterNode(Integer id) {
        Node node = loadNodeFromCsv(id);
        if (node != null) {
            graphService.addNode(node);
        }
        return node;
    }

    private Node loadNodeFromCsv(int targetId) {
        try {
            return findNodeInFile(resolveNodesFile(), targetId);
        } catch (Exception exception) {
            LOGGER.log(Level.WARNING, "No se pudo cargar el nodo " + targetId + " desde el CSV", exception);
            return null;
        }
    }

    private File resolveNodesFile() {
        File file = activeFolder != null ? new File(activeFolder, "nodes.csv")
                : new File("src/main/resources/" + configService.getString("graph.nodesCsv"));
        return file.exists() ? file : new File(configService.getString("graph.nodesCsv"));
    }

    private Node findNodeInFile(File file, int targetId) throws IOException {
        for (String line : Files.readAllLines(file.toPath())) {
            Node node = parseNodeLine(line, targetId);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    private Node parseNodeLine(String line, int targetId) {
        String[] parts = line.split(",");
        if (parts.length < 4 || parts[0].equals("id")) return null;
        if (Integer.parseInt(parts[0].trim()) != targetId) return null;
        return new Node(targetId, parts[3].trim(), Double.parseDouble(parts[1].trim()),
                Double.parseDouble(parts[2].trim()));
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
        LOGGER.info("Ruta guardada con el nombre: " + name);
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

    @Override
    public GraphLoadResult loadExternalGraph(File folder) {
        try {
            loadExternalFolder(folder);
            return new GraphLoadResult(graphService.getGraph(), languageService.getText("status.ready"));
        } catch (Exception exception) {
            return externalLoadFailure(folder, exception);
        }
    }

    private void loadExternalFolder(File folder) throws IOException {
        LOGGER.info("Cargando grafo desde la carpeta: " + folder.getAbsolutePath());
        history.clear();
        this.activeFolder = folder;
        graphService.clearGraph();
        csvLoader.loadNodes(new File(folder, "nodes.csv").getAbsolutePath());
        csvLoader.loadEdges(new File(folder, "edges.csv").getAbsolutePath());
    }

    private GraphLoadResult externalLoadFailure(File folder, Exception exception) {
        LOGGER.log(Level.SEVERE, "Error al cargar el grafo desde la carpeta " + folder.getAbsolutePath(), exception);
        return new GraphLoadResult(graphService.getGraph(), languageService.getText("status.graphLoadError"));
    }

    @Override
    public boolean saveGraphToFolder(File folder) {
        try {
            writeGraphFiles(folder);
            LOGGER.info("Grafo guardado en la carpeta: " + folder.getAbsolutePath());
            return true;
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error al guardar el grafo en la carpeta " + folder.getAbsolutePath(), exception);
            return false;
        }
    }

    private void writeGraphFiles(File folder) throws IOException {
        Files.createDirectories(folder.toPath());
        Files.write(new File(folder, "nodes.csv").toPath(), nodeLines());
        Files.write(new File(folder, "edges.csv").toPath(), edgeLines());
    }

    private List<String> nodeLines() {
        List<String> lines = new ArrayList<>(List.of("id,lat,lon,name"));
        graphService.getGraph().getNodes().values().forEach(node -> lines.add(toNodeLine(node)));
        return lines;
    }

    private String toNodeLine(Node node) {
        return node.getId() + "," + node.getLat() + "," + node.getLon() + "," + node.getName();
    }

    private List<String> edgeLines() {
        List<String> lines = new ArrayList<>(List.of("fromId,toId,oneWay"));
        graphService.getGraph().getEdges().forEach(edge -> lines.add(toEdgeLine(edge)));
        return lines;
    }

    private String toEdgeLine(Edge edge) {
        return edge.getSource().getId() + "," + edge.getDestination().getId() + "," + edge.isOneWay();
    }

    @Override
    public boolean saveGraph() {
        return saveGraphToFolder(resolveSaveFolder());
    }

    private File resolveSaveFolder() {
        if (activeFolder != null) {
            return activeFolder;
        }
        return new File(configService.getString("graph.saveFolder"));
    }

    @Override
    public InteractionResult undo() {
        if (history.isEmpty()) {
            LOGGER.warning("No hay acciones para deshacer");
            return InteractionResult.message("No hay acciones");
        }
        restoreSnapshot(history.pop());
        LOGGER.info("Última acción deshecha");
        return InteractionResult.message("Action undone");
    }

    private void restoreSnapshot(Graph snapshot) {
        graphService.clearGraph();
        snapshot.getNodes().values().forEach(graphService::addNode);
        snapshot.getEdges().forEach(graphService::addEdge);
    }

    @Override
    public Graph getGraph() {
        return graphService.getGraph();
    }
}