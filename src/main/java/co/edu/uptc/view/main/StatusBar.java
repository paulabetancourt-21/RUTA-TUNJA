package co.edu.uptc.view.main;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class StatusBar extends JPanel {

    private final LanguageService languageService;
    private final JLabel statusMessageLabel;
    private final JLabel coordinatesLabel;
    private double lastLatitude;
    private double lastLongitude;
    private int lastZoom;

    public StatusBar(LanguageService languageService) {
        this.languageService = languageService;
        this.lastZoom = 100;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(4, 8, 4, 8));
        statusMessageLabel = new JLabel();
        coordinatesLabel = new JLabel();
        add(statusMessageLabel, BorderLayout.WEST);
        add(coordinatesLabel, BorderLayout.EAST);
        applyLanguage();
    }

    public void applyLanguage() {
        statusMessageLabel.setText(languageService.getText("status.ready"));
        coordinatesLabel.setText(buildCoordinatesText(lastLatitude, lastLongitude, lastZoom));
    }

    public void setStatusMessage(String message) {
        statusMessageLabel.setText(message);
    }

    public void setCoordinates(double lat, double lon, int zoomPercentage) {
        lastLatitude = lat;
        lastLongitude = lon;
        lastZoom = zoomPercentage;
        coordinatesLabel.setText(buildCoordinatesText(lat, lon, zoomPercentage));
    }

    private String buildCoordinatesText(double lat, double lon, int zoomPercentage) {
        String latLabel = languageService.getText("status.lat");
        String lonLabel = languageService.getText("status.lon");
        String zoomLabel = languageService.getText("status.zoom");
        return String.format("%s %.5f %s %.5f %s %d%%", latLabel, lat, lonLabel, lon, zoomLabel, zoomPercentage);
    }
}
