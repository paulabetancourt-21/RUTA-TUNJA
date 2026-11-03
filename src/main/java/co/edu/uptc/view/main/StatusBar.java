package co.edu.uptc.view.main;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.util.List;

public class StatusBar extends JPanel {

    private final LanguageService languageService;
    private final ConfigService configService;
    private final JLabel statusMessageLabel;
    private final JLabel coordinatesLabel;

    public StatusBar(LanguageService languageService, ConfigService configService) {
        this.languageService = languageService;
        this.configService = configService;
        this.statusMessageLabel = new JLabel();
        this.coordinatesLabel = new JLabel();
        initLayout();
        applyLanguage();
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        setBorder(padding());
        add(statusMessageLabel, BorderLayout.WEST);
        add(coordinatesLabel, BorderLayout.EAST);
    }

    private EmptyBorder padding() {
        List<String> values = configService.getList("ui.status.padding");
        return new EmptyBorder(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)),
                Integer.parseInt(values.get(2)), Integer.parseInt(values.get(3)));
    }

    public void applyLanguage() {
        statusMessageLabel.setText(languageService.getText("status.ready"));
        double latitude = configService.getDouble("ui.status.initialLatitude");
        double longitude = configService.getDouble("ui.status.initialLongitude");
        int zoom = configService.getInt("ui.status.defaultZoomPercent");
        coordinatesLabel.setText(buildCoordinatesText(latitude, longitude, zoom));
    }

    public void setStatusMessage(String message) {
        statusMessageLabel.setText(message);
    }

    private String buildCoordinatesText(double lat, double lon, int zoomPercentage) {
        String latLabel = languageService.getText("status.lat");
        String lonLabel = languageService.getText("status.lon");
        String zoomLabel = languageService.getText("status.zoom");
        String format = configService.getString("ui.status.coordinateFormat");
        return String.format(format, latLabel, lat, lonLabel, lon, zoomLabel, zoomPercentage);
    }
}
