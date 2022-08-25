package code.roadEstimator.dto;

import code.roadEstimator.entities.Street;

import java.util.ArrayList;
import java.util.List;

public class RoutingResult {
    private int cost = 0;
    private List<String> streetList = new ArrayList<>();

    public RoutingResult() {
    }

    public RoutingResult(int cost, List<String> streetList) {
        this.cost = cost;
        this.streetList = streetList;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<String> getStreetList() {
        return streetList;
    }

    public void setStreetList(List<String> streetList) {
        this.streetList = streetList;
    }
}
