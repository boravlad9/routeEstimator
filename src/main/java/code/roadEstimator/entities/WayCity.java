package code.roadEstimator.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class WayCity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "idWay", nullable = false)
    private long idWay;

    @OneToMany(mappedBy="wayCity", cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<NodesCity> nodesCityList;



    public WayCity(String name, String type, long idWay) {
        this.name = name;
        this.type = type;
        this.idWay = idWay;
        nodesCityList = new ArrayList<>();
    }

    public WayCity(UUID id, String name, String type, long idWay) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.idWay = idWay;
        nodesCityList = new ArrayList<>();
    }

    public WayCity() {
        nodesCityList = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<NodesCity> getNodesCityList() {
        return nodesCityList;
    }

    public void setNodesCityList(List<NodesCity> nodesCityList) {
        this.nodesCityList = nodesCityList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getIdWay() {
        return idWay;
    }

    public void setIdWay(long idWay) {
        this.idWay = idWay;
    }
}
