package code.roadEstimator.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class NodesCity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    private UUID id;

    @Column(name = "node_id", nullable = false)
    private long NodeId;

    @Column(name = "long_data", nullable = false)
    private float longData;

    @Column(name = "lat_data", nullable = false)
    private float latData;

    @ManyToOne(cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
    private WayCity wayCity;

    public NodesCity() {
    }

    public NodesCity(long nodeId, float longData, float latData, WayCity wayCity) {
        NodeId = nodeId;
        this.longData = longData;
        this.latData = latData;
        this.wayCity = wayCity;
    }

    public NodesCity(UUID id, long nodeId, float longData, float latData) {
        this.id = id;
        NodeId = nodeId;
        this.longData = longData;
        this.latData = latData;
    }

    public NodesCity(long nodeId, float longData, float latData) {
        NodeId = nodeId;
        this.longData = longData;
        this.latData = latData;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getNodeId() {
        return NodeId;
    }

    public WayCity getWayCity() {
        return wayCity;
    }

    public void setWayCity(WayCity wayCity) {
        this.wayCity = wayCity;
    }

    public void setNodeId(long nodeId) {
        NodeId = nodeId;
    }

    public float getLongData() {
        return longData;
    }

    public void setLongData(float longData) {
        this.longData = longData;
    }

    public float getLatData() {
        return latData;
    }

    public void setLatData(float latData) {
        this.latData = latData;
    }
}
