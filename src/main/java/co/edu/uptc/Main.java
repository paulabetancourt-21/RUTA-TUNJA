// package co.edu.uptc;

// import co.edu.uptc.config.ConfigService;
// import co.edu.uptc.config.YamlConfigService;

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
//         System.out.println("Project ruta tunja - co.edu.uptc");
//     }
// }

package co.edu.uptc;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.config.YamlConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.i18n.YamlLanguageService;
import co.edu.uptc.view.MapView;
import co.edu.uptc.view.main.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConfigService configService = new YamlConfigService();
            LanguageService languageService = new YamlLanguageService(configService);
            MapView mapView = new MainFrame(languageService, configService);
            mapView.start();
        });
    }
}

// package co.edu.uptc;

// import co.edu.uptc.config.ConfigService;
// import co.edu.uptc.config.YamlConfigService;
// import co.edu.uptc.model.Edge;
// import co.edu.uptc.model.GraphService;
// import co.edu.uptc.model.GraphServiceImpl;
// import co.edu.uptc.persistence.CsvLoaderService;
// import co.edu.uptc.persistence.CsvLoaderServiceImpl;

// import java.util.List;
// import java.util.Map;

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
//             printEdgeWeights(graphService);
//         } catch (Exception e) {
//             System.err.println("Error: " + e.getMessage());
//         }
//     }

//     private void printEdgeWeights(GraphService graphService) {
//         Map<Integer, List<Edge>> adjacencyList = graphService.getGraph().getAdjacencyList();
//         System.out.println("=== PESOS CALCULADOS EN LAS ARISTAS ===");
//         adjacencyList.values().forEach(edges -> edges.forEach(this::printEdgeDetail));
//     }

//     private void printEdgeDetail(Edge edge) {
//         System.out.printf("Origen: %d | Destino: %d | Peso: %.4f km | Sentido: %s%n",
//                 edge.getSource().getId(),
//                 edge.getDestination().getId(),
//                 edge.getWeight(),
//                 edge.isOneWay() ? "Un sentido" : "Doble sentido");
//     }
// }


