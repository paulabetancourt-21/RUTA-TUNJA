package co.edu.uptc.view.map;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Graph;
import co.edu.uptc.model.Node;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapPanelImpl extends JPanel implements MapPanel {

    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 20.0;
    private static final double ARROW_ANGLE = Math.toRadians(28);
    private static final double ARROW_FACTOR = 1.8;
    private static final float MIN_STROKE = 1.0f;
    private static final int MIN_NODE_DIAMETER = 6;
    private static final int SELECTION_FACTOR = 3;

    private final ConfigService configService;
    private final LanguageService languageService;
    private BufferedImage mapImage;
    private Graph graph;
    private List<Node> route;
    private MapStyle style;
    private MapClickListener clickListener;
    private Node selectedNode;
    private double zoomFactor = 1.0;
    private int nodeClickTolerance;
    private boolean initialFitDone = false;

    public MapPanelImpl(ConfigService configService, LanguageService languageService) {
        this.configService = configService;
        this.languageService = languageService;
        this.style = new MapStyle();
        this.route = new ArrayList<>();
        this.nodeClickTolerance = configService.getInt("tolerance.clickRadiusPx");
        loadMapImage();
        initMouseListener();
        initComponentListener();
        setBackground(new Color(245, 245, 245));
    }

    private void loadMapImage() {
        String imagePath = configService.getString("map.imagePath");
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(imagePath)) {
            this.mapImage = stream == null ? null : ImageIO.read(stream);
        } catch (IOException exception) {
            this.mapImage = null;
        }
    }

    private void initComponentListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                fitOnFirstResize();
            }
        });
    }

    private void fitOnFirstResize() {
        if (initialFitDone || mapImage == null || getWidth() <= 0) {
            return;
        }
        zoomFactor = (double) getWidth() / mapImage.getWidth();
        initialFitDone = true;
        updatePreferredSize();
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                handleMapClick(event.getX(), event.getY());
            }
        });
    }

    private void handleMapClick(int pixelX, int pixelY) {
        double latitude = pixelToLatitude(pixelY);
        double longitude = pixelToLongitude(pixelX);
        notifyClick(latitude, longitude, findNodeNear(pixelX, pixelY));
    }

    private void notifyClick(double latitude, double longitude, Node node) {
        if (clickListener != null) {
            clickListener.onMapClicked(latitude, longitude, node);
        }
    }

    private Node findNodeNear(int pixelX, int pixelY) {
        if (graph == null || graph.getNodes() == null) {
            return null;
        }
        for (Node node : graph.getNodes().values()) {
            if (isInsideTolerance(node, pixelX, pixelY)) {
                return node;
            }
        }
        return null;
    }

    private boolean isInsideTolerance(Node node, int pixelX, int pixelY) {
        Point point = toPixel(node);
        return Math.hypot(point.x - pixelX, point.y - pixelY) <= nodeClickTolerance;
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

    private Point toPixel(Node node) {
        return new Point(longitudeToPixel(node.getLon()), latitudeToPixel(node.getLat()));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawMapImage(g2d);
        drawEdges(g2d);
        drawRoute(g2d);
        drawNodes(g2d);
        drawMarkers(g2d);
        drawSelection(g2d);
    }

    private void drawMapImage(Graphics2D g2d) {
        if (mapImage == null) {
            return;
        }
        int width = (int) (mapImage.getWidth() * zoomFactor);
        int height = (int) (mapImage.getHeight() * zoomFactor);
        g2d.drawImage(mapImage, 0, 0, width, height, this);
    }

    private void drawEdges(Graphics2D g2d) {
        if (graph == null || graph.getEdges() == null) {
            return;
        }
        g2d.setColor(style.getEdgeColor());
        g2d.setStroke(createStroke(style.getEdgeWidth()));
        graph.getEdges().forEach(edge -> drawEdge(g2d, edge));
    }

    private void drawEdge(Graphics2D g2d, Edge edge) {
        Point source = toPixel(edge.getSource());
        Point destination = toPixel(edge.getDestination());
        g2d.drawLine(source.x, source.y, destination.x, destination.y);
        if (edge.isOneWay()) {
            drawArrow(g2d, source, destination);
        }
    }

    private void drawArrow(Graphics2D g2d, Point source, Point destination) {
        double angle = Math.atan2(destination.y - source.y, destination.x - source.x);
        Point tip = move(destination, angle + Math.PI, scaledNodeDiameter() / 2.0);
        drawArrowSide(g2d, tip, angle, 1);
        drawArrowSide(g2d, tip, angle, -1);
    }

    private void drawArrowSide(Graphics2D g2d, Point tip, double angle, int sign) {
        double sideAngle = angle + Math.PI + sign * ARROW_ANGLE;
        Point end = move(tip, sideAngle, scaledArrowLength());
        g2d.drawLine(tip.x, tip.y, end.x, end.y);
    }

    private Point move(Point origin, double angle, double distance) {
        int x = (int) Math.round(origin.x + Math.cos(angle) * distance);
        int y = (int) Math.round(origin.y + Math.sin(angle) * distance);
        return new Point(x, y);
    }

    private void drawRoute(Graphics2D g2d) {
        if (route == null || route.size() < 2) {
            return;
        }
        g2d.setColor(style.getRouteColor());
        g2d.setStroke(createStroke(style.getRouteWidth()));
        drawRouteSegments(g2d);
    }

    private void drawRouteSegments(Graphics2D g2d) {
        for (int index = 0; index < route.size() - 1; index++) {
            drawRouteSegment(g2d, route.get(index), route.get(index + 1));
        }
    }

    private void drawRouteSegment(Graphics2D g2d, Node from, Node to) {
        Point start = toPixel(from);
        Point end = toPixel(to);
        g2d.drawLine(start.x, start.y, end.x, end.y);
    }

    private void drawNodes(Graphics2D g2d) {
        if (graph == null || graph.getNodes() == null) {
            return;
        }
        graph.getNodes().values().forEach(node -> drawNode(g2d, node));
    }

    private void drawNode(Graphics2D g2d, Node node) {
        Point point = toPixel(node);
        int diameter = scaledNodeDiameter();
        g2d.setColor(style.getNodeColor());
        g2d.fillOval(point.x - diameter / 2, point.y - diameter / 2, diameter, diameter);
        drawNodeLabel(g2d, node, point, diameter);
    }

    private void drawNodeLabel(Graphics2D g2d, Node node, Point point, int diameter) {
        g2d.setColor(style.getLabelColor());
        g2d.drawString(nodeLabel(node), point.x + diameter, point.y - diameter);
    }

    private String nodeLabel(Node node) {
        if (node.hasName()) {
            return node.getName();
        }
        return languageService.getText("map.node") + " " + node.getId();
    }

    private void drawMarkers(Graphics2D g2d) {
        if (route == null || route.isEmpty()) {
            return;
        }
        drawMarker(g2d, route.get(0), style.getOriginColor());
        drawMarker(g2d, route.get(route.size() - 1), style.getDestinationColor());
    }

    private void drawMarker(Graphics2D g2d, Node node, Color color) {
        Point point = toPixel(node);
        int diameter = scaledNodeDiameter() * 2;
        g2d.setColor(color);
        g2d.setStroke(createStroke(style.getEdgeWidth()));
        g2d.drawOval(point.x - diameter / 2, point.y - diameter / 2, diameter, diameter);
    }

    private void drawSelection(Graphics2D g2d) {
        if (selectedNode == null) {
            return;
        }
        Point point = toPixel(selectedNode);
        int diameter = scaledNodeDiameter() * SELECTION_FACTOR;
        g2d.setColor(style.getRouteColor());
        g2d.setStroke(createStroke(style.getEdgeWidth()));
        g2d.drawOval(point.x - diameter / 2, point.y - diameter / 2, diameter, diameter);
    }

    private BasicStroke createStroke(float baseWidth) {
        return new BasicStroke(scaledStroke(baseWidth), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    private int scaledNodeDiameter() {
        return Math.max(MIN_NODE_DIAMETER, (int) Math.round(style.getNodeDiameter() * zoomFactor));
    }

    private float scaledStroke(float baseWidth) {
        return Math.max(MIN_STROKE, (float) (baseWidth * zoomFactor));
    }

    private double scaledArrowLength() {
        return Math.max(4.0, style.getNodeDiameter() * ARROW_FACTOR * zoomFactor);
    }

    private void updatePreferredSize() {
        if (mapImage == null) {
            return;
        }
        int width = (int) (mapImage.getWidth() * zoomFactor);
        int height = (int) (mapImage.getHeight() * zoomFactor);
        setPreferredSize(new Dimension(width, height));
        revalidate();
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
        this.route = new ArrayList<>();
        this.selectedNode = null;
        repaint();
    }

    @Override
    public void setRoute(List<Node> route) {
        this.route = route == null ? new ArrayList<>() : new ArrayList<>(route);
        repaint();
    }

    @Override
    public void setStyle(MapStyle style) {
        this.style = style == null ? new MapStyle() : style;
        repaint();
    }

    @Override
    public MapStyle getStyle() {
        return style;
    }

    @Override
    public void setClickListener(MapClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void setSelectedNode(Node node) {
        this.selectedNode = node;
        repaint();
    }

    @Override
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = clampZoom(zoomFactor);
        updatePreferredSize();
        repaint();
    }

    private double clampZoom(double zoomFactor) {
        return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoomFactor));
    }

    @Override
    public double getZoomFactor() {
        return zoomFactor;
    }
}
