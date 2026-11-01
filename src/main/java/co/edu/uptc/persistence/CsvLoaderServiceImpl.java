package co.edu.uptc.persistence;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.GraphService;
import co.edu.uptc.model.Node;
import co.edu.uptc.util.GeoUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class CsvLoaderServiceImpl implements CsvLoaderService {
    private final GraphService graphService;

    public CsvLoaderServiceImpl(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public void loadNodes(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.skip(1).forEach(this::parseAndAddNode);
        }
    }

    @Override
    public void loadEdges(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.skip(1).forEach(this::parseAndAddEdge);
        }
    }

    private void parseAndAddNode(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return;

        int id = Integer.parseInt(parts[0].trim());
        double lat = Double.parseDouble(parts[1].trim());
        double lon = Double.parseDouble(parts[2].trim());

        graphService.getGraph().addNode(new Node(id, lat, lon));
    }

    private void parseAndAddEdge(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) return;

        int fromId = Integer.parseInt(parts[0].trim());
        int toId = Integer.parseInt(parts[1].trim());
        boolean oneWay = Boolean.parseBoolean(parts[2].trim());

        Node source = graphService.getGraph().getNodes().get(fromId);
        Node destination = graphService.getGraph().getNodes().get(toId);

        if (source != null && destination != null) {
            double weight = GeoUtil.calculateDistance(source, destination);
            graphService.getGraph().addEdge(new Edge(source, destination, oneWay, weight));
        }
    }
}