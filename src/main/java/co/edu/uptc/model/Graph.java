package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class Graph {
    private final Map<Integer, Node> nodes;
    private final Map<Integer, List<Edge>> adjacencyList;
    private final List<Edge> edges;

    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(Node node) {
        if (node == null) {
            return;
        }
        nodes.putIfAbsent(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        if (!isValidEdge(edge)) {
            return;
        }
        addDirectedEdge(edge.getSource(), edge);
        edges.add(edge);
        addReverseIfNeeded(edge);
    }

    private boolean isValidEdge(Edge edge) {
        return edge != null && edge.getSource() != null && edge.getDestination() != null;
    }

    private void addDirectedEdge(Node source, Edge edge) {
        adjacencyList.computeIfAbsent(source.getId(), id -> new ArrayList<>()).add(edge);
    }

    private void addReverseIfNeeded(Edge edge) {
        if (edge.isOneWay()) {
            return;
        }
        Edge reverse = new Edge(edge.getDestination(), edge.getSource(), false, edge.getWeight());
        addDirectedEdge(edge.getDestination(), reverse);
    }
}
