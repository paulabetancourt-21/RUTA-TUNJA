package co.edu.uptc.view.map;

import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import java.util.List;

public interface MapPanel {
    void setGraph(Graph graph);
    void setRoute(List<Node> route);
    void setStyle(MapStyle style);
    MapStyle getStyle();
    void setClickListener(MapClickListener clickListener);
    void setSelectedNode(Node node);
    void setZoomFactor(double zoomFactor);
    double getZoomFactor();
}
