package co.edu.uptc.model;

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
    public void addNode(Node node) {
        graph.addNode(node);
    }

    @Override
    public void addEdge(Edge edge) {
        graph.addEdge(edge);
    }

    @Override
    public void clearGraph() {
        graph.clear();
    }

    @Override
    public int nextNodeId() {
        return graph.getNodes().keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
}
