package co.edu.uptc.config;

import co.edu.uptc.util.AbstractYamlService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlConfigService extends AbstractYamlService implements ConfigService {

    private static final String DEFAULT_CONFIG_PATH = "config/application.yml";
    private String resourcePath;

    public YamlConfigService(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
    }

    public YamlConfigService() {
        this(DEFAULT_CONFIG_PATH);
    }

    private void ensureLoaded() {
        loadIfNeeded(resourcePath != null ? resourcePath : DEFAULT_CONFIG_PATH);
    }

    @Override
    public String getString(String key) {
        ensureLoaded();
        Object value = getValue(key);
        return value == null ? "" : String.valueOf(value);
    }

    @Override
    public int getInt(String key) {
        ensureLoaded();
        Object value = getValue(key);
        return value == null ? 0 : parseInt(value);
    }

    @Override
    public boolean getBoolean(String key) {
        ensureLoaded();
        Object value = getValue(key);
        return value != null && parseBoolean(value);
    }

    @Override
    public double getDouble(String key) {
        ensureLoaded();
        Object value = getValue(key);
        return value == null ? 0.0 : parseDouble(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSection(String key) {
        ensureLoaded();
        Object value = getValue(key);
        return isMap(value) ? (Map<String, Object>) value : new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getList(String key) {
        ensureLoaded();
        Object value = getValue(key);
        return (value instanceof List) ? convertList((List<?>) value) : new ArrayList<>();
    }

    private List<String> convertList(List<?> list) {
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            result.add(String.valueOf(item));
        }
        return result;
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