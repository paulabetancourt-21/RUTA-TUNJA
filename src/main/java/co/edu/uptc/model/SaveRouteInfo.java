package co.edu.uptc.model;

public record SaveRouteInfo(boolean available, String summary, String message) {

    public static SaveRouteInfo available(String summary) {
        return new SaveRouteInfo(true, summary, "");
    }

    public static SaveRouteInfo unavailable(String message) {
        return new SaveRouteInfo(false, "", message);
    }
}
