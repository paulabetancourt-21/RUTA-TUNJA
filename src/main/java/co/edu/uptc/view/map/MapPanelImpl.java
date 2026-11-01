package co.edu.uptc.view.map;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MapPanelImpl extends JPanel implements MapPanel {

    private final ConfigService configService;
    private BufferedImage mapImage;
    private Graph graph;
    private double zoomFactor = 1.0;
    private int nodeClickTolerance = 10;
    private boolean initialFitDone = false;

    public MapPanelImpl(ConfigService configService) {
        this.configService = configService;
        loadMapImage();
        initMouseListener();
        initComponentListener();
        setBackground(new Color(245, 245, 245));
    }

    private void loadMapImage() {
        String imagePath = configService.getString("map.imagePath");
        try {
            this.mapImage = ImageIO.read(new File("src/main/resources/" + imagePath));
        } catch (IOException e) {
            this.mapImage = null;
        }
    }

    private void initComponentListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!initialFitDone && mapImage != null && getWidth() > 0) {
                    fitImageToWidth();
                    initialFitDone = true;
                }
            }
        });
    }

    private void fitImageToWidth() {
        if (mapImage != null && getWidth() > 0) {
            // Ajusta el zoom exactamente al ancho de la pantalla para evitar espacios en blanco laterales
            zoomFactor = (double) getWidth() / mapImage.getWidth();
            updatePreferredSize();
        }
    }

    private void updatePreferredSize() {
        if (mapImage != null) {
            int w = (int) (mapImage.getWidth() * zoomFactor);
            int h = (int) (mapImage.getHeight() * zoomFactor);
            setPreferredSize(new Dimension(w, h));
            revalidate();
        }
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMapClick(e.getX(), e.getY());
            }
        });
    }

    private void handleMapClick(int pixelX, int pixelY) {
        System.out.println("Debug TopLat: " + configService.getDouble("geoReference.topLeftLat"));
        System.out.println("Debug TopLon: " + configService.getDouble("geoReference.topLeftLon"));
        double lat = pixelToLatitude(pixelY);
        double lon = pixelToLongitude(pixelX);
        System.out.printf("Clic en Mapa -> Píxel: (%d, %d) | Lat: %.6f, Lon: %.6f%n", pixelX, pixelY, lat, lon);
        
        Node clickedNode = findNodeNear(pixelX, pixelY);
        if (clickedNode != null) {
            System.out.println("Nodo seleccionado: " + clickedNode.getId());
        }
    }

    private double pixelToLatitude(int pixelY) {
        double topLat = configService.getDouble("geoReference.topLeftLat");
        double bottomLat = configService.getDouble("geoReference.bottomRightLat");
        int height = mapImage != null ? mapImage.getHeight() : getHeight();
        return topLat + ((double) pixelY / (height * zoomFactor)) * (bottomLat - topLat);
    }

    private double pixelToLongitude(int pixelX) {
        double leftLon = configService.getDouble("geoReference.topLeftLon");
        double rightLon = configService.getDouble("geoReference.bottomRightLon");
        int width = mapImage != null ? mapImage.getWidth() : getWidth();
        return leftLon + ((double) pixelX / (width * zoomFactor)) * (rightLon - leftLon);
    }

    private Node findNodeNear(int px, int py) {
        if (graph == null || graph.getNodes() == null) return null;
        for (Node node : graph.getNodes().values()) {
            int nodePx = longitudeToPixel(node.getLon());
            int nodePy = latitudeToPixel(node.getLat());
            double distance = Math.hypot(nodePx - px, nodePy - py);
            if (distance <= nodeClickTolerance) {
                return node;
            }
        }
        return null;
    }

    private int longitudeToPixel(double lon) {
        double leftLon = configService.getDouble("geoReference.topLeftLon");
        double rightLon = configService.getDouble("geoReference.bottomRightLon");
        int width = mapImage != null ? mapImage.getWidth() : getWidth();
        return (int) (((lon - leftLon) / (rightLon - leftLon)) * width * zoomFactor);
    }

    private int latitudeToPixel(double lat) {
        double topLat = configService.getDouble("geoReference.topLeftLat");
        double bottomLat = configService.getDouble("geoReference.bottomRightLat");
        int height = mapImage != null ? mapImage.getHeight() : getHeight();
        return (int) (((lat - topLat) / (bottomLat - topLat)) * height * zoomFactor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        if (graph == null || graph.getNodes() == null) return;
        
        g2d.setColor(Color.RED);
        for (Node node : graph.getNodes().values()) {
            int px = longitudeToPixel(node.getLon());
            int py = latitudeToPixel(node.getLat());
            int size = 8;
            g2d.fillOval(px - size / 2, py - size / 2, size, size);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Nodo " + node.getId(), px + 6, py - 4);
            g2d.setColor(Color.RED);
        }
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint();
    }

    @Override
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
        updatePreferredSize();
        repaint();
    }

    @Override
    public double getZoomFactor() {
        return zoomFactor;
    }
}