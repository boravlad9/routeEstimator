package code.roadEstimator.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Street  {
    private NodesCity firstEndpoint = null;

    private NodesCity secondEndpoint = null;

    private PersonData personData = null;

    private WayCity mainWay = null;

    private List<Noduri> noduri = null;

    private List<Link> links = null;

    public Street() {
        this.noduri = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public Street(NodesCity firstEndpoint, NodesCity secondEndpoint, WayCity mainWay) {
        this.firstEndpoint = firstEndpoint;
        this.secondEndpoint = secondEndpoint;
        this.mainWay = mainWay;
        this.noduri = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public NodesCity getFirstEndpoint() {
        return firstEndpoint;
    }

    public void setFirstEndpoint(NodesCity firstEndpoint) {
        this.firstEndpoint = firstEndpoint;
    }

    public NodesCity getSecondEndpoint() {
        return secondEndpoint;
    }

    public void setSecondEndpoint(NodesCity secondEndpoint) {
        this.secondEndpoint = secondEndpoint;
    }

    public List<Noduri> getNoduri() {
        return noduri;
    }

    public void setNoduri(List<Noduri> noduri) {
        this.noduri = noduri;
    }

    public PersonData getPersonData() {
        return personData;
    }

    public void setPersonData(PersonData personData) {
        this.personData = personData;
    }

    public WayCity getMainWay() {
        return mainWay;
    }

    public void setMainWay(WayCity mainWay) {
        this.mainWay = mainWay;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
