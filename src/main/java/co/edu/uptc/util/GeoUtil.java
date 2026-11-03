package co.edu.uptc.util;

import co.edu.uptc.model.Node;

public class GeoUtil {
    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double calculateDistance(Node n1, Node n2) {
        double a = calculateA(n1, n2);
        return EARTH_RADIUS_KM * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    private static double calculateA(Node n1, Node n2) {
        double dLat = Math.toRadians(n2.getLat() - n1.getLat());
        double dLon = Math.toRadians(n2.getLon() - n1.getLon());
        double lat1 = Math.toRadians(n1.getLat());
        double lat2 = Math.toRadians(n2.getLat());
        
        return Math.pow(Math.sin(dLat / 2), 2) + 
               Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
    }
}