package co.edu.uptc.persistence;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.GraphService;
import co.edu.uptc.model.Node;
import co.edu.uptc.util.GeoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        try (Stream<String> lines = readLines(filePath)) {
            lines.skip(1).forEach(this::parseAndAddNode);
        }
    }

    @Override
    public void loadEdges(String filePath) throws IOException {
        try (Stream<String> lines = readLines(filePath)) {
            lines.skip(1).forEach(this::parseAndAddEdge);
        }
    }

    private Stream<String> readLines(String filePath) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(filePath);
        if (stream != null) {
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines();
        }
        return Files.lines(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    private void parseAndAddNode(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            return;
        }
        int id = Integer.parseInt(parts[0].trim());
        double lat = Double.parseDouble(parts[1].trim());
        double lon = Double.parseDouble(parts[2].trim());
        graphService.addNode(new Node(id, lat, lon));
    }

    private void parseAndAddEdge(String line) {
        String[] parts = line.split(",");
        if (parts.length < 3) {
            return;
        }
        int fromId = Integer.parseInt(parts[0].trim());
        int toId = Integer.parseInt(parts[1].trim());
        boolean oneWay = Boolean.parseBoolean(parts[2].trim());
        addEdge(fromId, toId, oneWay);
    }

    private void addEdge(int fromId, int toId, boolean oneWay) {
        Node source = graphService.getGraph().getNodes().get(fromId);
        Node destination = graphService.getGraph().getNodes().get(toId);
        if (source != null && destination != null) {
            double weight = GeoUtil.calculateDistance(source, destination);
            graphService.addEdge(new Edge(source, destination, oneWay, weight));
        }
    }
}
