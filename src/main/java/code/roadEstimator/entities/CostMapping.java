package code.roadEstimator.entities;

public class CostMapping {

    private int cost;
    private Street street;
    private Nodes node;

    public CostMapping() {
    }

    public CostMapping(int cost, Street street) {
        this.cost = cost;
        this.street = street;
    }

    public CostMapping(int cost, Street street, Nodes node) {
        this.cost = cost;
        this.street = street;
        this.node = node;
    }

    public Nodes getNode() {
        return node;
    }

    public void setNode(Nodes node) {
        this.node = node;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }


}
