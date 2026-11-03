package co.edu.uptc.model;

import lombok.Getter;

@Getter
public class Node {
    private int id;
    private String name;
    private double lat;
    private double lon;

    public Node(int id, double lat, double lon) {
        this(id, "", lat, lon);
    }

    public Node(int id, String name, double lat, double lon) {
        this.id = id;
        this.name = name == null ? "" : name.trim();
        this.lat = lat;
        this.lon = lon;
    }

    public boolean hasName() {
        return !name.isBlank();
    }
}
