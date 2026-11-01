package co.edu.uptc.presenter;

import co.edu.uptc.model.GraphLoadResult;
import co.edu.uptc.model.GraphResetResult;
import co.edu.uptc.model.InteractionResult;
import co.edu.uptc.model.MapModelService;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.RouteResult;
import co.edu.uptc.model.SaveRouteInfo;
import co.edu.uptc.model.SavedRoute;
import co.edu.uptc.view.MapView;
import co.edu.uptc.view.map.MapStyle;

import java.util.List;

public class MapPresenterImpl implements MapPresenter {

    private final MapView view;
    private final MapModelService model;

    public MapPresenterImpl(MapView view, MapModelService model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void start() {
        view.showGraphLoad(model.loadGraph());
        view.start();
    }

    @Override
    public void startCreateNode() {
        view.updateStatusMessage(model.startCreateNode());
    }

    @Override
    public void startCreateEdge() {
        view.updateStatusMessage(model.startCreateEdge());
    }

    @Override
    public void startCalculateRoute() {
        view.updateStatusMessage(model.startCalculateRoute());
    }

    @Override
    public void onMapClicked(double latitude, double longitude, Node node) {
        view.showInteraction(model.handleClick(latitude, longitude, node));
    }

    @Override
    public void createNode(double latitude, double longitude, String name) {
        view.showGraphChanged(model.createNode(latitude, longitude, name));
    }

    @Override
    public void createEdge(Node source, Node destination, boolean oneWay) {
        view.showGraphChanged(model.createEdge(source, destination, oneWay));
    }

    @Override
    public void calculateRoute(Node origin, Node destination) {
        view.showRouteResult(model.calculateRoute(origin, destination));
    }

    @Override
    public void notifyCancelled() {
        view.updateStatusMessage(model.cancelDialog());
    }

    @Override
    public void cancelInteraction() {
        view.showOptionalMessage(model.cancelInteraction());
    }

    @Override
    public void openSaveRouteDialog() {
        view.showSaveRouteInfo(model.getSaveInfo());
    }

    @Override
    public void saveCurrentRoute(String name) {
        view.showSavedRoute(model.saveRoute(name));
    }

    @Override
    public List<SavedRoute> getSavedRoutes() {
        return model.getSavedRoutes();
    }

    @Override
    public void showSavedRoute(SavedRoute route) {
        view.showRouteResult(model.resolveSavedRoute(route));
    }

    @Override
    public void clearRoute() {
        view.clearRoute(model.clearRoute());
    }

    @Override
    public void resetGraph() {
        view.confirmNewGraph();
    }

    @Override
    public void createNewGraph() {
        view.showGraphReset(model.resetGraph());
    }

    @Override
    public void saveGraph() {
        view.showGraphSaved();
    }

    @Override
    public void undo() {
        view.showUndo();
    }

    @Override
    public void zoomIn() {
        view.zoomIn();
    }

    @Override
    public void zoomOut() {
        view.zoomOut();
    }

    @Override
    public void resetZoom() {
        view.resetZoom();
    }

    @Override
    public void openGraphDialog() {
        view.showOpenGraphDialog();
    }

    @Override
    public void openConfigDialog() {
        view.showConfigDialog();
    }

    @Override
    public void applyStyle(MapStyle style) {
        view.applyStyle(style);
    }

    @Override
    public void openLoadLanguageDialog() {
        view.showLoadLanguageDialog();
    }

    @Override
    public void changeLanguage(String languageCode) {
        view.changeLanguage(languageCode);
    }

    @Override
    public void openHowItWorksDialog() {
        view.showHowItWorksDialog();
    }

    @Override
    public void openAboutDialog() {
        view.showAboutDialog();
    }
}
