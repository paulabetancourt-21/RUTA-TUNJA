package co.edu.uptc.presenter;

import co.edu.uptc.i18n.LanguageService;
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
    private final LanguageService languageService;

    public MapPresenterImpl(MapView view, MapModelService model, LanguageService languageService) {
        this.view = view;
        this.model = model;
        this.languageService = languageService;
    }

    @Override
    public void start() {
        GraphLoadResult result = model.loadGraph();
        view.showGraph(result.graph());
        view.updateStatusMessage(result.message());
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
        renderInteraction(model.handleClick(latitude, longitude, node));
    }

    private void renderInteraction(InteractionResult result) {
        switch (result.type()) {
            case CREATE_NODE -> view.showCreateNodeDialog(result.latitude(), result.longitude());
            case CREATE_EDGE -> view.showCreateEdgeDialog(result.origin(), result.destination(), result.distanceKm());
            case CALCULATE_ROUTE -> view.showCalculateRouteDialog(result.origin(), result.destination());
            case SELECT_NODE -> showSelectedNode(result);
            case MESSAGE -> view.updateStatusMessage(result.message());
            default -> { }
        }
    }

    private void showSelectedNode(InteractionResult result) {
        view.setSelectedNode(result.selectedNode());
        view.updateStatusMessage(result.message());
    }

    @Override
    public void createNode(double latitude, double longitude, String name) {
        view.updateStatusMessage(model.createNode(latitude, longitude, name));
        view.repaintMap();
    }

    @Override
    public void createEdge(Node source, Node destination, boolean oneWay) {
        view.updateStatusMessage(model.createEdge(source, destination, oneWay));
        view.repaintMap();
    }

    @Override
    public void calculateRoute(Node origin, Node destination) {
        RouteResult result = model.calculateRoute(origin, destination);
        view.showRoute(result.route());
        view.updateStatusMessage(result.message());
    }

    @Override
    public void notifyCancelled() {
        view.updateStatusMessage(model.cancelDialog());
    }

    @Override
    public void cancelInteraction() {
        model.cancelInteraction().ifPresent(view::updateStatusMessage);
    }

    @Override
    public void openSaveRouteDialog() {
        SaveRouteInfo info = model.getSaveInfo();
        if (info.available()) {
            view.showSaveRouteDialog(info.summary());
        } else {
            view.updateStatusMessage(info.message());
        }
    }

    @Override
    public void saveCurrentRoute(String name) {
        view.updateStatusMessage(model.saveRoute(name));
        view.refreshMenuBar();
    }

    @Override
    public List<SavedRoute> getSavedRoutes() {
        return model.getSavedRoutes();
    }

    @Override
    public void showSavedRoute(SavedRoute savedRoute) {
        RouteResult result = model.resolveSavedRoute(savedRoute);
        view.showRoute(result.route());
        view.updateStatusMessage(result.message());
    }

    @Override
    public void clearRoute() {
        view.clearRoute();
        view.updateStatusMessage(model.clearRoute());
    }

    @Override
    public void resetGraph() {
        if (!view.confirm(languageService.getText("message.confirmNewGraph"),
                languageService.getText("message.newGraphTitle"))) {
            return;
        }
        GraphResetResult result = model.resetGraph();
        view.showGraph(result.graph());
        view.updateStatusMessage(result.message());
    }

    @Override
    public void saveGraph() {
        view.showMessage(languageService.getText("message.graphSavedMessage"),
                languageService.getText("message.graphSavedTitle"));
        view.updateStatusMessage(languageService.getText("status.graphSaved"));
    }

    @Override
    public void undo() {
        view.updateStatusMessage(languageService.getText("status.undo"));
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
        view.showConfigDialog(view.getStyle());
    }

    @Override
    public void applyStyle(MapStyle style) {
        view.setStyle(style);
        view.updateStatusMessage(languageService.getText("status.configurationUpdated"));
    }

    @Override
    public void openLoadLanguageDialog() {
        view.showLoadLanguageDialog();
    }

    @Override
    public void changeLanguage(String languageCode) {
        languageService.setLanguage(languageCode);
        view.refreshTexts();
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
