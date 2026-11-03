package co.edu.uptc.persistence;

import co.edu.uptc.model.SavedRoute;

import java.util.List;

public interface SavedRouteService {
    List<SavedRoute> findAll();
    void save(SavedRoute route);
}
