package co.edu.uptc.config;

import java.util.Map;

public interface ConfigService {
    String getString(String key);
    int getInt(String key);
    boolean getBoolean(String key);
    double getDouble(String key);
    Map<String, Object> getSection(String key);
}
