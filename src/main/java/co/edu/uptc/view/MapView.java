package co.edu.uptc.view;

public interface MapView {
    void start();
    void updateStatusMessage(String message);
    void updateCoordinates(double lat, double lon, int zoomPercentage);
}