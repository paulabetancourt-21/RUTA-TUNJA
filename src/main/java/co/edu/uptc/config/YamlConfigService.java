package co.edu.uptc.config;

import co.edu.uptc.util.AbstractYamlService;
import java.util.HashMap;
import java.util.Map;

public class YamlConfigService extends AbstractYamlService implements ConfigService {
    private String resourcePath;

    public YamlConfigService(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
    }

    public YamlConfigService() {
        super();
        this.resourcePath = null;
    }

    private void ensureLoaded() {
        if (resourcePath == null) {
            resourcePath = getString("files.applicationConfig");
        }
        loadIfNeeded(resourcePath);
    }

    @Override
    public String getString(String key) {
        ensureLoaded();
        Object value = getValue(key);
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    @Override
    public int getInt(String key) {
        ensureLoaded();
        Object value = getValue(key);
        if (value == null) {
            return 0;
        }
        return parseInt(value);
    }

    @Override
    public boolean getBoolean(String key) {
        ensureLoaded();
        Object value = getValue(key);
        if (value == null) {
            return false;
        }
        return parseBoolean(value);
    }

    @Override
    public double getDouble(String key) {
        ensureLoaded();
        Object value = getValue(key);
        if (value == null) {
            return 0.0;
        }
        return parseDouble(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSection(String key) {
        ensureLoaded();
        Object value = getValue(key);
        if (isMap(value)) {
            return (Map<String, Object>) value;
        }
        return new HashMap<String, Object>();
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
