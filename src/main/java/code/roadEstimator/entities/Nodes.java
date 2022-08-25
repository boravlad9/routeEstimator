package code.roadEstimator.entities;

import java.util.HashSet;
import java.util.Set;

public class Nodes implements Comparable<Nodes>{

    private Street value = null;
    private Nodes parent = null;
    private Integer cost = 0;
    private double longitute, latitude = 0;

    public Nodes(Street value, Nodes parent) {
        this.value = value;
        this.parent = parent;
    }

    public Nodes(Street value) {
        this.value = value;
    }

    public Nodes(Street value, Nodes parent, int cost) {
        this.value = value;
        this.parent = parent;
        this.cost = cost;
    }

    public Nodes() {
    }

    public Nodes(Street value, Nodes parent, Integer cost, double longitute, double latitude) {
        this.value = value;
        this.parent = parent;
        this.cost = cost;
        this.longitute = longitute;
        this.latitude = latitude;
    }

    public Street getValue() {
        return value;
    }

    public void setValue(Street value) {
        this.value = value;
    }

    public Nodes getParent() {
        return parent;
    }

    public void setParent(Nodes parent) {
        this.parent = parent;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int compareTo(Nodes nodes) {
        return this.getCost().compareTo(nodes.getCost());
    }
}
