package co.edu.uptc.view.map;

import co.edu.uptc.model.Node;

public interface MapClickListener {
    void onMapClicked(double latitude, double longitude, Node node);
}
