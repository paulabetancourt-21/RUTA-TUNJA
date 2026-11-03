package co.edu.uptc.view.map;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.util.ColorUtil;

import lombok.Getter;
import lombok.Setter;

import java.awt.Color;

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

    public MapStyle(ConfigService config) {
        loadMetrics(config);
        loadColors(config);
    }

    private void loadMetrics(ConfigService config) {
        this.nodeDiameter = config.getInt("ui.style.nodeDiameter");
        this.edgeWidth = (float) config.getDouble("ui.style.edgeWidth");
        this.routeWidth = (float) config.getDouble("ui.style.routeWidth");
    }

    private void loadColors(ConfigService config) {
        this.nodeColor = ColorUtil.fromHex(config.getString("ui.style.nodeColor"));
        this.edgeColor = ColorUtil.fromHex(config.getString("ui.style.edgeColor"));
        this.routeColor = ColorUtil.fromHex(config.getString("ui.style.routeColor"));
        this.labelColor = ColorUtil.fromHex(config.getString("ui.style.labelColor"));
        this.originColor = ColorUtil.fromHex(config.getString("ui.style.originColor"));
        this.destinationColor = ColorUtil.fromHex(config.getString("ui.style.destinationColor"));
    }
}
