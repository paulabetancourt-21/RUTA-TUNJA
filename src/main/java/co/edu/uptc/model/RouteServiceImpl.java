package co.edu.uptc.model;

import co.edu.uptc.util.GeoUtil;
import java.util.List;

public class RouteServiceImpl implements RouteService {

    @Override
    public double calculateTotalDistance(List<Node> route) {
        if (route == null || route.size() < 2) return 0.0;
        double total = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += GeoUtil.calculateDistance(route.get(i), route.get(i + 1));
        }
        return total;
    }
}