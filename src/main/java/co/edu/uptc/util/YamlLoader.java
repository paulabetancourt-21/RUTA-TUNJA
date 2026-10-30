package co.edu.uptc.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class YamlLoader {
    
    public Map<String, Object> load(String resourcePath) {
        InputStream inputStream = openStream(resourcePath);
        if (inputStream == null) {
            return emptyMap();
        }
        return loadFromStream(inputStream);
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
