package code.roadEstimator.services;

import code.roadEstimator.dto.Coordinates;
import code.roadEstimator.dto.DemoRez;
import code.roadEstimator.entities.RouteCaracteristics;
import code.roadEstimator.dto.RoutingResult;
import code.roadEstimator.entities.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.abs;

@Service
public class RoutingService {

    static public PersonData personData = new PersonData();

    public double getDistance(double par1, double par2, double par3, double par4) {
        double r = 6371e3;
        double theta1 = par1 * Math.PI/180;
        double theta2 = par3 * Math.PI/180;
        double theta3 = (par3 - par1) * Math.PI/180;
        double delta = (par4 - par2) * Math.PI/180;

        double a = Math.sin(theta3 / 2) * Math.sin(theta3 / 2) +
                Math.cos(theta1) * Math.cos(theta2) *
                        Math.sin(delta / 2) * Math.sin(delta / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return(c * r);
    }
    private GraphData graphData = GraphData.getInstace();

    public double calculateIfOnRoad(double latC, double longC, Street street) {
        double min = Double.MAX_VALUE;
        for (NodesCity nodesCity : street.getMainWay().getNodesCityList()) {
            NodesCity iterFirst = nodesCity;
            //double long1 = abs(latC - iterFirst.getLatData());
            //double long2 = abs(longC - iterFirst.getLongData());
            //if (min > long1  + long2) {
                //min = long1 + long2;
            //}
            double temp = getDistance(latC, longC, iterFirst.getLatData(), iterFirst.getLongData());
            if (min > temp)
                min = temp;
        }
        return min;
    }

    private Street getCloseStreet(double latC, double longC) {
        double min = Double.MAX_VALUE;
        int counter = 0;
        int minPlace = -1;
        for (Street street : graphData.getStreetList()) {
            double temp = calculateIfOnRoad(latC, longC, street);
            if (min > temp) {
                min = temp;
                minPlace = counter;
            }
            counter++;
        }
        return graphData.getStreetList().get(minPlace);
    }

    private double getSpeedByCluster(PersonData personData, Date date) {
        int counterCar = 0;
        int speed = 0;
        if (personData == null)
            return 25;
        for (RouteCaracteristics personData1 : personData.getRouteCaracteristicsList()) {
            int count = 0;
            int total = 0;
            for (int temp : personData1.getDayCount())
                total += temp;
            count += personData1.getDayCount()[date.getDay()];
            if (date.getDay() == 0 || date.getDay() == 6)
                count += personData1.getWeekDayCount();
            else
                count += total - personData1.getWeekDayCount();
            if (counterCar < count) {
                speed = personData1.getSpeed();
            }
        }
        if (speed == 0)
            return 25;
        return speed;
    }

    private int calculateDuration(Nodes current, Link next, Date date){
        double distance = getDistance(current.getLatitude(), current.getLongitute(), next.getNodesCity().getLatData(), next.getNodesCity().getLongData());
        return (int) (distance / (getSpeedByCluster(current.getValue().getPersonData(), date) * 1000/3600));
    }

    public RoutingResult getRoute(Coordinates coordinates) {
        Street startStreet = getCloseStreet(coordinates.getLatitude(), coordinates.getLongitude());
        Street endStreet = getCloseStreet(coordinates.getDestLat(), coordinates.getDestLong());
        Nodes startingNode = new Nodes(startStreet, null, 0,  coordinates.getLongitude(), coordinates.getLatitude());
        Queue<Nodes> queue = new PriorityQueue<>();
        queue.add(startingNode);
        List<CostMapping> costMappingList = new ArrayList<>();
        int counter = 0;
        while (!queue.isEmpty()) {
            //if (counter == 1000000)
                //break;
            Nodes temp = queue.remove();
            boolean flagE = false, flagCost = false;
            if (temp.getParent() != null)
            if (temp.getParent().getValue().getMainWay().getIdWay() == endStreet.getMainWay().getIdWay())
                continue;
            for (CostMapping costMapping : costMappingList) {
                if (costMapping.getStreet().getMainWay().getIdWay() == (temp.getValue().getMainWay().getIdWay())) {
                    flagE = true;
                    if (costMapping.getCost() > temp.getCost()) {
                        costMapping.setCost(temp.getCost());
                        costMapping.setNode(temp);
                        flagCost = true;
                    }
                }
            }
            if (!flagE) {
                costMappingList.add(new CostMapping(temp.getCost(), temp.getValue(),temp));
            } else
            if (!flagCost)
                continue;
            for (Link street : temp.getValue().getLinks()) {
                Nodes newNode = new Nodes(street.getStreet(), temp, temp.getCost() + calculateDuration(temp, street, coordinates.getDate()));
                newNode.setLatitude(street.getNodesCity().getLatData());
                newNode.setLongitute(street.getNodesCity().getLongData());
                queue.add(newNode);
            }
            counter++;
        }
        /*List<NodesCity> nodesCityList = new ArrayList<>();
        NodesCity nodesCity = new NodesCity();
        nodesCity.setLatData((float)coordinates.getLatitude());
        nodesCity.setLongData((float)coordinates.getLongitude());
        nodesCityList.add(nodesCity);

        double []distances = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        List<NodesCity> initialList = new ArrayList<>();
        initialList.add(new NodesCity());
        initialList.add(new NodesCity());
        initialList.add(new NodesCity());
        initialList.add(new NodesCity());
        for (NodesCity iterator : graphData.getNodesCities()) {
            double distance = getDistance(iterator.getLatData(), iterator.getLongData(), nodesCity.getLatData(), nodesCity.getLongData());
            for (int i = 0; i < distances.length; i++) {
                if (distance < distances[i]) {
                    for (int j = distances.length - 1; j > i; j--) {
                        distances[j] = distances[j - 1];
                        initialList.set(j, initialList.get(j - 1));
                    }
                    distances[i] = distance;
                    initialList.set(i, iterator);
                }
            }
        }
        Queue<NodesCity> queue = new PriorityQueue<>();
        for (NodesCity iterator : initialList) {
            queue.add(iterator);
        }
        executeRouting(queue, nodesCityList);
        NodesCity nodesDest = new NodesCity();
        nodesCity.setLatData((float)coordinates.getDestLat());
        nodesCity.setLongData((float)coordinates.getDestLong());
        nodesCityList.add(nodesDest);
        return nodesCityList;*/
        for (CostMapping costMapping : costMappingList) {
            Nodes nodes = costMapping.getNode();
            if (endStreet.getMainWay().getIdWay() == costMapping.getNode().getValue().getMainWay().getIdWay())  {
                RoutingResult routingResult = new RoutingResult();
                NodesCity temp = new NodesCity();
                temp.setLatData(coordinates.getDestLat());
                temp.setLongData(coordinates.getDestLong());
                Link link = new Link(endStreet, temp);
                int finalCost = calculateDuration(nodes, link, coordinates.getDate());
                while(nodes != null) {
                    routingResult.getStreetList().add(nodes.getValue().getMainWay().getName());
                    nodes = nodes.getParent();
                }
                routingResult.setCost(costMapping.getCost() + finalCost);
                return routingResult;
            }
        }
        return null;
    }

    public DemoRez demo() {
        return  FileInitializer.demoRez;
    }

}
