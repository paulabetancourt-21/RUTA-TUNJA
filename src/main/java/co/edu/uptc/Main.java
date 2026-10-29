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
