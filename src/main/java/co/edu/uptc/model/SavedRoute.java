package co.edu.uptc.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SavedRoute {
    private String name;
    private List<Integer> nodeIds;
    private double distanceKm;
}
