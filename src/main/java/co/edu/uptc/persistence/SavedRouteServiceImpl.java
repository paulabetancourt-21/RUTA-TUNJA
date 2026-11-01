package co.edu.uptc.persistence;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.model.SavedRoute;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SavedRouteServiceImpl implements SavedRouteService {

    private static final String SEPARATOR = "\t";

    private final Path filePath;
    private final List<SavedRoute> routes;

    public SavedRouteServiceImpl(ConfigService configService) {
        this.filePath = Paths.get(configService.getString("routes.savedFile"));
        this.routes = new ArrayList<>();
        load();
    }

    @Override
    public List<SavedRoute> findAll() {
        return new ArrayList<>(routes);
    }

    @Override
    public void save(SavedRoute route) {
        if (route == null) {
            return;
        }
        routes.add(route);
        persist();
    }

    private void load() {
        if (!Files.exists(filePath)) {
            return;
        }
        try {
            Files.readAllLines(filePath, StandardCharsets.UTF_8).forEach(this::addFromLine);
        } catch (IOException exception) {
            routes.clear();
        }
    }

    private void addFromLine(String line) {
        String[] parts = line.split(SEPARATOR);
        if (parts.length < 3) {
            return;
        }
        SavedRoute route = buildRouteSafely(parts);
        if (route != null) {
            routes.add(route);
        }
    }

    private SavedRoute buildRouteSafely(String[] parts) {
        try {
            return buildRoute(parts);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private SavedRoute buildRoute(String[] parts) {
        List<Integer> nodeIds = parseIds(parts[1]);
        return new SavedRoute(parts[0], nodeIds, Double.parseDouble(parts[2]));
    }

    private List<Integer> parseIds(String text) {
        return Arrays.stream(text.split(","))
                .filter(id -> !id.isBlank())
                .map(Integer::parseInt)
                .toList();
    }

    private void persist() {
        try {
            createParentDirectory();
            Files.write(filePath, toLines(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            // La ruta permanece en memoria aunque falle el archivo.
        }
    }

    private void createParentDirectory() throws IOException {
        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private List<String> toLines() {
        return routes.stream().map(this::toLine).toList();
    }

    private String toLine(SavedRoute route) {
        String nodeIds = route.getNodeIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return sanitize(route.getName()) + SEPARATOR + nodeIds + SEPARATOR + route.getDistanceKm();
    }

    private String sanitize(String name) {
        return name.replace(SEPARATOR, " ").replace("\n", " ").replace("\r", " ");
    }
}
