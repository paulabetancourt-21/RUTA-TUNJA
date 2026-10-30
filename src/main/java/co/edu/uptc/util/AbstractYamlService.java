package co.edu.uptc.util;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractYamlService {
    protected YamlLoader yamlLoader;
    protected Map<String, Object> dataMap;
    protected boolean loaded;

    public AbstractYamlService() {
        this.yamlLoader = new YamlLoader();
        this.dataMap = new HashMap<String, Object>();
        this.loaded = false;
    }

    protected void loadIfNeeded(String resourcePath) {
        if (loaded) {
            return;
        }
        load(resourcePath);
    }

    protected void load(String resourcePath) {
        dataMap = yamlLoader.load(resourcePath);
        loaded = true;
    }

    protected void reload(String resourcePath) {
        loaded = false;
        dataMap = new HashMap<String, Object>();
        loadIfNeeded(resourcePath);
    }

    protected boolean isMap(Object object) {
        return object instanceof Map;
    }

    @SuppressWarnings("unchecked")
    protected Object getValue(String key) {
        Object current = dataMap;
        String[] parts = key.split("\\.");
        for (String part : parts) {
            if (!isMap(current)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(part);
        }
        return current;
    }
}
