package co.edu.uptc.model;

import java.util.List;

public interface DijkstraService {
    List<Node> calculateShortestPath(Node source, Node destination);
}