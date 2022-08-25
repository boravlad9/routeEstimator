package code.roadEstimator.entities;

import java.util.List;

public class PersonData {

    private Breaks breaks;
    private List<Noduri> noduriPersoana;
    private List<RouteCaracteristics> routeCaracteristicsList;

    public PersonData() {
    }

    public PersonData(Breaks breaks, List<Noduri> noduriPersoana, List<RouteCaracteristics> routeCaracteristicsList) {
        this.breaks = breaks;
        this.noduriPersoana = noduriPersoana;
        this.routeCaracteristicsList = routeCaracteristicsList;
    }

    public Breaks getBreaks() {
        return breaks;
    }

    public void setBreaks(Breaks breaks) {
        this.breaks = breaks;
    }

    public List<Noduri> getNoduriPersoana() {
        return noduriPersoana;
    }

    public void setNoduriPersoana(List<Noduri> noduriPersoana) {
        this.noduriPersoana = noduriPersoana;
    }

    public List<RouteCaracteristics> getRouteCaracteristicsList() {
        return routeCaracteristicsList;
    }

    public void setRouteCaracteristicsList(List<RouteCaracteristics> routeCaracteristicsList) {
        this.routeCaracteristicsList = routeCaracteristicsList;
    }
}
