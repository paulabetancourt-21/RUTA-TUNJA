package co.edu.uptc.view;

import co.edu.uptc.model.GraphLoadResult;
import co.edu.uptc.model.GraphResetResult;
import co.edu.uptc.model.InteractionResult;
import co.edu.uptc.model.RouteResult;
import co.edu.uptc.model.SaveRouteInfo;
import co.edu.uptc.view.map.MapStyle;

import java.util.Optional;

public interface MapView {

    void start();

    void showGraphLoad(GraphLoadResult result);

    void showGraphReset(GraphResetResult result);

    void showInteraction(InteractionResult result);

    void showGraphChanged(String message);

    void showRouteResult(RouteResult result);

    void showOptionalMessage(Optional<String> message);

    void showSaveRouteInfo(SaveRouteInfo info);

    void showSavedRoute(String message);

    void showGraphSaved();

    void showUndo();

    void clearRoute(String message);

    void confirmNewGraph();

    void updateStatusMessage(String message);

    void updateCoordinates(double lat, double lon, int zoomPercentage);

    void showConfigDialog();

    void applyStyle(MapStyle style);

    void showLoadLanguageDialog();

    void showOpenGraphDialog();

    void showHowItWorksDialog();

    void showAboutDialog();

    void changeLanguage(String languageCode);

    void zoomIn();

    void zoomOut();

    void resetZoom();
}
