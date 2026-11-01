package co.edu.uptc.view.map;

import co.edu.uptc.model.Graph;

public interface MapPanel {
    void setGraph(Graph graph);
    void setZoomFactor(double zoomFactor);
    double getZoomFactor();
}