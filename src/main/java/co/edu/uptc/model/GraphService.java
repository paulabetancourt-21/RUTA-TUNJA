package co.edu.uptc.model;

import java.util.List;

public interface GraphService {
    Graph getGraph();
    void buildGraph(List<Node> nodes, List<Edge> edges);
}