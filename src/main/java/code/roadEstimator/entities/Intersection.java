package code.roadEstimator.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

public class Intersection{

    private NodesCity nodesCity;

    private List<WayCity> intersectionPart;

    public Intersection(NodesCity nodesCity) {
        this.nodesCity = nodesCity;
        intersectionPart = new ArrayList<>();
    }

    public Intersection() {
        intersectionPart = new ArrayList<>();
    }

    public NodesCity getNodesCity() {
        return nodesCity;
    }

    public void setNodesCity(NodesCity nodesCity) {
        this.nodesCity = nodesCity;
    }

    public List<WayCity> getIntersectionPart() {
        return intersectionPart;
    }

    public void setIntersectionPart(List<WayCity> intersectionPart) {
        this.intersectionPart = intersectionPart;
    }
}
