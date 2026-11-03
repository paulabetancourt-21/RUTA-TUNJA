package co.edu.uptc.persistence;

import java.io.IOException;

public interface CsvLoaderService {
    void loadNodes(String filePath) throws IOException;
    void loadEdges(String filePath) throws IOException;
}