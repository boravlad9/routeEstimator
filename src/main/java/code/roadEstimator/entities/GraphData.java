package code.roadEstimator.entities;

import java.util.ArrayList;
import java.util.List;

public class GraphData {

    private List<NodesCity> nodesCities = new ArrayList<>();
    private List<WayCity> wayCityList = new ArrayList<>();
    private List<Street> streetList = new ArrayList<>();

    private static GraphData instance = null;

    private GraphData() {

    }

    public static GraphData getInstace() {
        if (instance == null) {
            instance = new GraphData();
        }
        return instance;
    }

    public List<NodesCity> getNodesCities() {
        return nodesCities;
    }

    public void setNodesCities(List<NodesCity> nodesCities) {
        this.nodesCities = nodesCities;
    }

    public List<WayCity> getWayCityList() {
        return wayCityList;
    }

    public void setWayCityList(List<WayCity> wayCityList) {
        this.wayCityList = wayCityList;
    }


    public List<Street> getStreetList() {
        return streetList;
    }

    public void setStreetList(List<Street> streetList) {
        this.streetList = streetList;
    }
}
