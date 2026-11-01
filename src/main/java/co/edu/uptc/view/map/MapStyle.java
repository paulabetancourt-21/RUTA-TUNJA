package co.edu.uptc.view.map;

import java.awt.Color;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapStyle {
    private int nodeDiameter;
    private float edgeWidth;
    private float routeWidth;
    private Color nodeColor;
    private Color edgeColor;
    private Color routeColor;
    private Color labelColor;
    private Color originColor;
    private Color destinationColor;

    public MapStyle() {
        this.nodeDiameter = 10;
        this.edgeWidth = 2.0f;
        this.routeWidth = 4.0f;
        this.nodeColor = new Color(200, 30, 30);
        this.edgeColor = new Color(60, 60, 60);
        this.routeColor = new Color(30, 90, 220);
        this.labelColor = Color.BLACK;
        this.originColor = new Color(20, 150, 60);
        this.destinationColor = new Color(230, 120, 20);
    }
}
