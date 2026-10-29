package co.edu.uptc.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class YamlConfigService implements ConfigService {
    private Map<String, Object> configMap;
    private boolean loaded;

    public YamlConfigService() {
        this.loaded = false;
        this.configMap = new HashMap<String, Object>();
    }

    private void loadIfNeeded() {
        if (loaded) {
            return;
        }
        loadConfig();
    }

    private void loadConfig() {
        InputStream inputStream = openStream();
        if (inputStream == null) {
            System.err.println("No se encontró config/application.yml");
            markLoaded();
            return;
        }
        loadFromStream(inputStream);
    }

    private InputStream openStream() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("config/application.yml");
        return stream;
    }

    private void markLoaded() {
        loaded = true;
    }

    private void loadFromStream(InputStream inputStream) {
        try (InputStream stream = inputStream) {
            parseStream(stream);
        } catch (Exception exception) {
            System.err.println("Error leyendo application.yml: " + exception.getMessage());
            configMap = new HashMap<String, Object>();
        } finally {
            markLoaded();
        }
    }

    @SuppressWarnings("unchecked")
    private void parseStream(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Object loadedObject = yaml.load(inputStream);
        if (isMap(loadedObject)) {
            configMap = (Map<String, Object>) loadedObject;
            return;
        }
        configMap = new HashMap<String, Object>();
    }

    private boolean isMap(Object object) {
        return object instanceof Map;
    }

    @Override
    public String getString(String key) {
        loadIfNeeded();
        Object value = getValue(key);
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    @Override
    public int getInt(String key) {
        loadIfNeeded();
        Object value = getValue(key);
        if (value == null) {
            return 0;
        }
        return parseInt(value);
    }

    @Override
    public boolean getBoolean(String key) {
        loadIfNeeded();
        Object value = getValue(key);
        if (value == null) {
            return false;
        }
        return parseBoolean(value);
    }

    @Override
    public double getDouble(String key) {
        loadIfNeeded();
        Object value = getValue(key);
        if (value == null) {
            return 0.0;
        }
        return parseDouble(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSection(String key) {
        loadIfNeeded();
        Object value = getValue(key);
        if (isMap(value)) {
            return (Map<String, Object>) value;
        }
        return new HashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    private Object getValue(String key) {
        Object current = configMap;
        for (String part : key.split("\\.")) {
            if (!isMap(current)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(part);
        }
        return current;
    }

    private int parseInt(Object value) {
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception exception) {
            return 0;
        }
    }

    private boolean parseBoolean(Object value) {
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private double parseDouble(Object value) {
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception exception) {
            return 0.0;
        }
    }
}
