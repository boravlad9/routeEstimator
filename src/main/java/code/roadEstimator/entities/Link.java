package code.roadEstimator.entities;

public class Link {

    private Street street;
    private NodesCity nodesCity;

    public Link() {
    }

    public Link(Street street, NodesCity nodesCity) {
        this.street = street;
        this.nodesCity = nodesCity;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public NodesCity getNodesCity() {
        return nodesCity;
    }

    public void setNodesCity(NodesCity nodesCity) {
        this.nodesCity = nodesCity;
    }
}
