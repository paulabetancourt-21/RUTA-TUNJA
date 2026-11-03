package co.edu.uptc.model;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.persistence.CsvLoaderService;
import co.edu.uptc.persistence.CsvLoaderServiceImpl;
import co.edu.uptc.persistence.SavedRouteService;
import co.edu.uptc.persistence.SavedRouteServiceImpl;

public final class ModelDependencies {

    private GraphService graphService;
    private CsvLoaderService csvLoader;
    private DijkstraService dijkstraService;
    private RouteService routeService;
    private SavedRouteService savedRouteService;

    public static ModelDependencies createDefault(ConfigService config) {
        GraphService graph = new GraphServiceImpl();
        return new ModelDependencies()
                .graphService(graph)
                .csvLoader(new CsvLoaderServiceImpl(config, graph))
                .dijkstraService(new DijkstraServiceImpl(graph))
                .routeService(new RouteServiceImpl())
                .savedRouteService(new SavedRouteServiceImpl(config));
    }

    public GraphService graphService() {
        return graphService;
    }

    public ModelDependencies graphService(GraphService value) {
        this.graphService = value;
        return this;
    }

    public CsvLoaderService csvLoader() {
        return csvLoader;
    }

    public ModelDependencies csvLoader(CsvLoaderService value) {
        this.csvLoader = value;
        return this;
    }

    public DijkstraService dijkstraService() {
        return dijkstraService;
    }

    public ModelDependencies dijkstraService(DijkstraService value) {
        this.dijkstraService = value;
        return this;
    }

    public RouteService routeService() {
        return routeService;
    }

    public ModelDependencies routeService(RouteService value) {
        this.routeService = value;
        return this;
    }

    public SavedRouteService savedRouteService() {
        return savedRouteService;
    }

    public ModelDependencies savedRouteService(SavedRouteService value) {
        this.savedRouteService = value;
        return this;
    }
}
