package co.edu.uptc.presenter;

import co.edu.uptc.model.Node;
import co.edu.uptc.model.SavedRoute;
import co.edu.uptc.view.map.MapClickListener;
import co.edu.uptc.view.map.MapStyle;
import java.io.File;
import java.util.List;

public interface MapPresenter extends MapClickListener {
    void start();
    void startCreateNode();
    void startCreateEdge();
    void startCalculateRoute();
    void cancelInteraction();
    void createNode(double latitude, double longitude, String name);
    void createEdge(Node source, Node destination, boolean oneWay);
    void calculateRoute(Node origin, Node destination);
    void notifyCancelled();
    void openSaveRouteDialog();
    void saveCurrentRoute(String name);
    List<SavedRoute> getSavedRoutes();
    void showSavedRoute(SavedRoute route);
    void clearRoute();
    void resetGraph();
    void createNewGraph();
    void saveGraph();
    void undo();
    void zoomIn();
    void zoomOut();
    void resetZoom();
    void openGraphDialog();
    void openConfigDialog();
    void applyStyle(MapStyle style);
    void openLoadLanguageDialog();
    void changeLanguage(String languageCode);
    void openHowItWorksDialog();
    void openAboutDialog();
    void loadGraphFromFolder(File folder);
}