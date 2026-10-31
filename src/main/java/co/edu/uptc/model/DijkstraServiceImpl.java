package co.edu.uptc.model;

import java.util.*;

/**
 * Implementa el algoritmo de Dijkstra para encontrar el camino más corto
 * entre dos nodos de un grafo con pesos no negativos.
 */
public class DijkstraServiceImpl implements DijkstraService {
    private GraphService graphService;
    private Map<Integer, Double> distances;
    private Map<Integer, Node> previousNodes;
    private Set<Integer> visitedNodeIds;
    private PriorityQueue<Node> pendingNodes;

    public DijkstraServiceImpl(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public List<Node> calculateShortestPath(Node source, Node destination) {
        initStructures(source);
        processNodes(destination);
        return buildPath(source, destination);
    }

    private void initStructures(Node source) {
        distances = new HashMap<>();
        previousNodes = new HashMap<>();
        visitedNodeIds = new HashSet<>();
        pendingNodes = new PriorityQueue<>(Comparator.comparingDouble((Node node) -> distances.get(node.getId())));
        initDistances(source);
    }

    private void initDistances(Node source) {
        graphService.getGraph().getNodes().keySet().forEach(nodeId -> distances.put(nodeId, Double.POSITIVE_INFINITY));
        distances.put(source.getId(), 0.0);
        pendingNodes.add(source);
    }

    private void processNodes(Node destination) {
        while (!pendingNodes.isEmpty() && !visitedNodeIds.contains(destination.getId())) {
            Node current = pendingNodes.poll();
            visitedNodeIds.add(current.getId());
            evaluateNeighbors(current);
        }
    }

    private void evaluateNeighbors(Node current) {
        List<Edge> edges = graphService.getGraph().getAdjacencyList().getOrDefault(current.getId(), new ArrayList<>());
        edges.forEach(edge -> updateDistance(current, edge));
    }

    private void updateDistance(Node current, Edge edge) {
        validateWeight(edge);
        double newDistance = distances.get(current.getId()) + edge.getWeight();
        if (newDistance < distances.get(edge.getDestination().getId())) {
            relaxEdge(edge.getDestination(), current, newDistance);
        }
    }

    private void validateWeight(Edge edge) {
        if (edge.getWeight() < 0) {
            throw new IllegalArgumentException("Dijkstra no admite pesos negativos: " + edge.getWeight());
        }
    }

    private void relaxEdge(Node neighbor, Node current, double newDistance) {
        pendingNodes.remove(neighbor);
        distances.put(neighbor.getId(), newDistance);
        previousNodes.put(neighbor.getId(), current);
        pendingNodes.add(neighbor);
    }

    private List<Node> buildPath(Node source, Node destination) {
        List<Node> path = traceBack(destination);
        return Objects.equals(path.get(0).getId(), source.getId()) ? path : new ArrayList<>();
    }

    private List<Node> traceBack(Node destination) {
        LinkedList<Node> path = new LinkedList<>();
        for (Node step = destination; step != null; step = previousNodes.get(step.getId())) {
            path.addFirst(step);
        }
        return path;
    }
}