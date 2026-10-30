package co.edu.uptc.config;

import java.util.List;
import java.util.Map;

public interface ConfigService {
    String getString(String key);
    int getInt(String key);
    boolean getBoolean(String key);
    double getDouble(String key);
    Map<String, Object> getSection(String key);
    List<String> getList(String key);
}
