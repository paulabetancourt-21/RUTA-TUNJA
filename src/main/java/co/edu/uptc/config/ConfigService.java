package co.edu.uptc.config;

import java.util.List;

public interface ConfigService {
    String getString(String key);
    int getInt(String key);
    double getDouble(String key);
    List<String> getList(String key);
}
