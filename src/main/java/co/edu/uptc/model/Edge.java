package co.edu.uptc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Edge {
    private Node source;
    private Node destination;
    private boolean oneWay;
    private double weight;
}