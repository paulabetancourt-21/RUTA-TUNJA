package co.edu.uptc.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface MapModelService {
    GraphLoadResult loadGraph();
    GraphResetResult resetGraph();
    String createNode(double latitude, double longitude, String name);
    String createEdge(Node source, Node destination, boolean oneWay);
    String startCreateNode();
    String startCreateEdge();
    String startCalculateRoute();
    InteractionResult handleClick(double latitude, double longitude, Node node);
    Optional<String> cancelInteraction();
    String cancelDialog();
    RouteResult calculateRoute(Node origin, Node destination);
    RouteResult resolveSavedRoute(SavedRoute savedRoute);
    SaveRouteInfo getSaveInfo();
    String saveRoute(String name);
    List<SavedRoute> getSavedRoutes();
    String clearRoute();
    GraphLoadResult loadExternalGraph(File folder);
}
