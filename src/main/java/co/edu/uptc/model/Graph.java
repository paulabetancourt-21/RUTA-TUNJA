package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class Graph {
    private Map<Integer, Node> nodes;
    private Map<Integer, List<Edge>> adjacencyList;

    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(Node node) {
        nodes.putIfAbsent(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(Edge edge) {
        int sourceId = edge.getSource().getId();
        adjacencyList.get(sourceId).add(edge);
        
        if (!edge.isOneWay()) {
            addReverseEdge(edge);
        }
    }
    
    private void addReverseEdge(Edge edge) {
        Edge reverse = new Edge(edge.getDestination(), edge.getSource(), false, edge.getWeight());
        adjacencyList.get(edge.getDestination().getId()).add(reverse);
    }
}

