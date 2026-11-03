package co.edu.uptc.model;

import java.util.List;

public record RouteResult(List<Node> route, String message) {

    public static RouteResult empty(String message) {
        return new RouteResult(List.of(), message);
    }

    public static RouteResult route(List<Node> route, String message) {
        return new RouteResult(List.copyOf(route), message);
    }
}
