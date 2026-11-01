package co.edu.uptc.view;

import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import java.util.List;

public interface MapView {
    void start();
    void showGraph(Graph graph);
    void showRoute(List<Node> route);
    void clearRoute();
    void updateStatusMessage(String message);
    void updateCoordinates(double lat, double lon, int zoomPercentage);
}
