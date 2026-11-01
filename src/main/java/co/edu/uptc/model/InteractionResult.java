package co.edu.uptc.model;

public record InteractionResult(
        InteractionType type,
        double latitude,
        double longitude,
        Node origin,
        Node destination,
        Node selectedNode,
        double distanceKm,
        String message) {

    public static InteractionResult none() {
        return new InteractionResult(InteractionType.NONE, 0, 0, null, null, null, 0, "");
    }

    public static InteractionResult createNode(double latitude, double longitude) {
        return new InteractionResult(InteractionType.CREATE_NODE, latitude, longitude,
                null, null, null, 0, "");
    }

    public static InteractionResult createEdge(Node origin, Node destination, double distanceKm) {
        return new InteractionResult(InteractionType.CREATE_EDGE, 0, 0,
                origin, destination, null, distanceKm, "");
    }

    public static InteractionResult calculateRoute(Node origin, Node destination) {
        return new InteractionResult(InteractionType.CALCULATE_ROUTE, 0, 0,
                origin, destination, null, 0, "");
    }

    public static InteractionResult selectNode(Node selectedNode, String message) {
        return new InteractionResult(InteractionType.SELECT_NODE, 0, 0,
                null, null, selectedNode, 0, message);
    }

    public static InteractionResult message(String message) {
        return new InteractionResult(InteractionType.MESSAGE, 0, 0,
                null, null, null, 0, message);
    }
}
