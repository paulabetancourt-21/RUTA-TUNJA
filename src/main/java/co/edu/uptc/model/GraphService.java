package co.edu.uptc.model;

public interface GraphService {
    Graph getGraph();
    void addNode(Node node);
    void addEdge(Edge edge);
    void clearGraph();
    int nextNodeId();
}
