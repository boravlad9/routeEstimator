package code.roadEstimator.dto;

import java.util.ArrayList;
import java.util.List;

public class DemoRez {

    List<List<Integer>> list = new ArrayList<>();
    List<Coordinates> coordinatesList = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    List<Integer> deviation = new ArrayList<>();

    public DemoRez() {
    }

    public DemoRez(List<List<Integer>> list, List<Coordinates> coordinatesList, List<String> descriptions, List<Integer> deviation) {
        this.list = list;
        this.coordinatesList = coordinatesList;
        this.descriptions = descriptions;
        this.deviation = deviation;
    }

    public List<List<Integer>> getList() {
        return list;
    }

    public void setList(List<List<Integer>> list) {
        this.list = list;
    }

    public List<Coordinates> getCoordinatesList() {
        return coordinatesList;
    }

    public void setCoordinatesList(List<Coordinates> coordinatesList) {
        this.coordinatesList = coordinatesList;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<Integer> getDeviation() {
        return deviation;
    }

    public void setDeviation(List<Integer> deviation) {
        this.deviation = deviation;
    }
}
