package co.edu.uptc.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlLoader {

    private static final Logger LOGGER = Logger.getLogger(YamlLoader.class.getName());

    public Map<String, Object> load(String resourcePath) {
        InputStream inputStream = openStream(resourcePath);
        if (inputStream == null) {
            LOGGER.warning("No se encontró el archivo YAML: " + resourcePath);
            return emptyMap();
        }
        return loadFromStream(inputStream);
    }

    public Map<String, Object> loadFromFile(File file) {
        if (file == null || !file.exists()) {
            LOGGER.warning("No existe el archivo YAML: " + file);
            return emptyMap();
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return loadFromStream(fileInputStream);
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo YAML: " + file.getAbsolutePath(), exception);
            return emptyMap();
        }
    }

    private InputStream openStream(String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    private Map<String, Object> emptyMap() {
        return new HashMap<String, Object>();
    }

    private Map<String, Object> loadFromStream(InputStream inputStream) {
        try (InputStream stream = inputStream) {
            return parseStream(stream);
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error al interpretar el contenido YAML", exception);
            return emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseStream(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Object loadedObject = yaml.load(inputStream);
        if (isMap(loadedObject)) {
            return (Map<String, Object>) loadedObject;
        }
        return emptyMap();
    }

    private boolean isMap(Object object) {
        return object instanceof Map;
    }
}
