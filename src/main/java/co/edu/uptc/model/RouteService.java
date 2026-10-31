package co.edu.uptc.model;

import java.util.List;

public interface RouteService {
    double calculateTotalDistance(List<Node> route);
}