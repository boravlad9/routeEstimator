package code.roadEstimator.entities;

import java.util.ArrayList;
import java.util.List;

public class GraphData {

    private static List<NodesCity> nodesCities;
    private static List<WayCity> wayCities;

    public GraphData() {
        nodesCities = new ArrayList<>();
        wayCities = new ArrayList<>();
    }

    public List<NodesCity> getNodesCities() {
        return nodesCities;
    }

    public List<WayCity> getWayCities() {
        return wayCities;
    }

    public void addNode(NodesCity nodesCity) {
        nodesCities.add(nodesCity);
    }

    public void addWay(WayCity wayCity) {
        wayCities.add(wayCity);
    }
}
