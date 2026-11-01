package co.edu.uptc.view.main;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class StatusBar extends JPanel {

    private final JLabel statusMessageLabel;
    private final JLabel coordinatesLabel;

    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(4, 8, 4, 8));
        statusMessageLabel = new JLabel("Listo. Usa los menús para construir el grafo o calcular una ruta.");
        coordinatesLabel = new JLabel("Lat - Lon - Zoom 100%");
        add(statusMessageLabel, BorderLayout.WEST);
        add(coordinatesLabel, BorderLayout.EAST);
    }

    public void setStatusMessage(String message) {
        statusMessageLabel.setText(message);
    }

    public void setCoordinates(double lat, double lon, int zoomPercentage) {
        coordinatesLabel.setText(String.format("Lat %.5f Lon %.5f Zoom %d%%", lat, lon, zoomPercentage));
    }
}