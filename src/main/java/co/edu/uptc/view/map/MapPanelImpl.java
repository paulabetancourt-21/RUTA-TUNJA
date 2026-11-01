package co.edu.uptc.view.map;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.model.Graph;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapPanelImpl extends JPanel implements MapPanel {

    private final ConfigService configService;
    private BufferedImage mapImage;
    private Graph graph;
    private double zoomFactor = 1.0;

    public MapPanelImpl(ConfigService configService) {
        this.configService = configService;
        loadMapImage();
    }

    private void loadMapImage() {
        String imagePath = configService.getString("map.imagePath");
        try {
            this.mapImage = ImageIO.read(new File("src/main/resources/" + imagePath));
        } catch (IOException e) {
            this.mapImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawMapImage(g2d);
        drawGraphElements(g2d);
    }

    private void drawMapImage(Graphics2D g2d) {
        if (mapImage != null) {
            int newWidth = (int) (mapImage.getWidth() * zoomFactor);
            int newHeight = (int) (mapImage.getHeight() * zoomFactor);
            g2d.drawImage(mapImage, 0, 0, newWidth, newHeight, this);
        }
    }

    private void drawGraphElements(Graphics2D g2d) {
        if (graph == null) return;
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint();
    }

    @Override
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        repaint();
    }

    @Override
    public double getZoomFactor() {
        return zoomFactor;
    }
}