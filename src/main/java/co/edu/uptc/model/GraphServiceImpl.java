package co.edu.uptc.model;

import java.util.List;

public class GraphServiceImpl implements GraphService {
    private Graph graph;

    public GraphServiceImpl() {
        this.graph = new Graph();
    }

    @Override
    public Graph getGraph() {
        return graph;
    }

    @Override
    public void buildGraph(List<Node> nodes, List<Edge> edges) {
        nodes.forEach(graph::addNode);
        edges.forEach(graph::addEdge);
    }
}