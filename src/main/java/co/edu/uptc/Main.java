package co.edu.uptc;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.config.YamlConfigService;

public class Main {
    public static void main(String[] args) {
        ConfigService configService = new YamlConfigService();
        new Main(configService).start();
    }

    private ConfigService configService;

    public Main(ConfigService configService) {
        this.configService = configService;
    }

    private void start() {
        System.out.println("Project ruta tunja - co.edu.uptc");
    }
}

// package co.edu.uptc;

// import co.edu.uptc.config.ConfigService;
// import co.edu.uptc.config.YamlConfigService;
// import co.edu.uptc.model.GraphService;
// import co.edu.uptc.model.GraphServiceImpl;
// import co.edu.uptc.persistence.CsvLoaderService;
// import co.edu.uptc.persistence.CsvLoaderServiceImpl;

// public class Main {
//     public static void main(String[] args) {
//         ConfigService configService = new YamlConfigService();
//         new Main(configService).start();
//     }

//     private ConfigService configService;

//     public Main(ConfigService configService) {
//         this.configService = configService;
//     }

//     private void start() {
//         GraphService graphService = new GraphServiceImpl();
//         CsvLoaderService csvLoader = new CsvLoaderServiceImpl(graphService);

//         try {
//             csvLoader.loadNodes("src/main/resources/csv/nodes.csv");
//             csvLoader.loadEdges("src/main/resources/csv/edges.csv");
//             int loadedNodes = graphService.getGraph().getNodes().size();
//             int loadedEdges = graphService.getGraph().getAdjacencyList().values().stream().mapToInt(java.util.List::size).sum();
//             System.out.println("Fase 5 Exitosa -> Nodos cargados: " + loadedNodes + " | Aristas cargadas: " + loadedEdges);
//         } catch (Exception e) {
//             System.err.println("Error al cargar archivos CSV: " + e.getMessage());
//         }
//     }
// }
