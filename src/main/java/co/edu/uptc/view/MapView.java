package co.edu.uptc.view;

import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;
import co.edu.uptc.view.map.MapStyle;

import java.util.List;

public interface MapView {
    void start();
    void showGraph(Graph graph);
    void showRoute(List<Node> route);
    void clearRoute();
    void updateStatusMessage(String message);
    void updateCoordinates(double lat, double lon, int zoomPercentage);
    void showCreateNodeDialog(double latitude, double longitude);
    void showCreateEdgeDialog(Node source, Node destination, double distanceKm);
    void showCalculateRouteDialog(Node origin, Node destination);
    void showSaveRouteDialog(String routeSummary);
    void showConfigDialog(MapStyle style);
    void showLoadLanguageDialog();
    void showOpenGraphDialog();
    void showHowItWorksDialog();
    void showAboutDialog();
    void showMessage(String message, String title);
    boolean confirm(String message, String title);
    void refreshTexts();
    void refreshMenuBar();
    void setSelectedNode(Node node);
    void setStyle(MapStyle style);
    MapStyle getStyle();
    void zoomIn();
    void zoomOut();
    void resetZoom();
    void repaintMap();
}
