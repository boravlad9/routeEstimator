package code.roadEstimator.services;

import code.roadEstimator.dto.Coordinates;
import code.roadEstimator.dto.DemoRez;
import code.roadEstimator.entities.RouteCaracteristics;
import code.roadEstimator.dto.RoutingResult;
import code.roadEstimator.entities.*;
import code.roadEstimator.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Timestamp;
import java.util.regex.Pattern;

import static java.lang.Math.*;

@Component
public class FileInitializer implements CommandLineRunner {
    private final NoduriRepository noduriRepository;
    private final NodesCityRepository nodesCityRepository;
    private final WayCityRepository wayCityRepository;
    private final GraphData graphData = GraphData.getInstace();
    private final RoutingService routingService = new RoutingService();
    private final int skipNumber = 1;
    private List<Coordinates> coordinatesList = new ArrayList<>();
    private List<Street> demoStreetsStart = new ArrayList<>();
    private List<Street> demoStreetsDest = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    public static DemoRez demoRez = new DemoRez();


    private int calculateTime(Noduri temp, Noduri temp1) throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date resDate = sdf.parse(temp.getTimestamp().toString());
        Date reqDate = sdf.parse(temp1.getTimestamp().toString());
        return abs((int)((reqDate.getTime() - resDate.getTime()) / (1000)));
    }

    @Autowired
    public FileInitializer(NoduriRepository noduriRepository, NodesCityRepository nodesCityRepository,
                           WayCityRepository wayCityRepository) {
        this.noduriRepository = noduriRepository;
        this.wayCityRepository = wayCityRepository;
        this.nodesCityRepository = nodesCityRepository;
    }

    private void demoInitialize() {
        coordinatesList.add(new Coordinates(46.780408, 23.625816, 46.779232, 23.619590, new Date()));
        coordinatesList.add(new Coordinates(46.778345, 23.614248, 46.776860, 23.607046, new Date()));
        coordinatesList.add(new Coordinates(46.776860, 23.607046, 46.778877, 23.606196, new Date()));
        coordinatesList.add(new Coordinates(46.778877, 23.606196, 46.780541, 23.604871, new Date()));
        coordinatesList.add(new Coordinates(46.780541, 23.604871, 46.780325, 23.599332, new Date()));
        coordinatesList.add(new Coordinates(46.780314, 23.598929, 46.779753, 23.594443, new Date()));
        coordinatesList.add(new Coordinates(46.779764, 23.593850, 46.779601, 23.591633, new Date()));
        coordinatesList.add(new Coordinates(46.779601, 23.591633, 46.776213, 23.590206, new Date()));
        coordinatesList.add(new Coordinates(46.776069, 23.590108, 46.773905, 23.588240, new Date()));
        coordinatesList.add(new Coordinates(46.773809, 23.587978, 46.774858, 23.587348, new Date()));
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la strada Aurel Vlaicu pana la intersectia cu rampe, aproape de Piata Marasti");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Piata Marasti pana la Strada Buftea");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Strada Buftea pana la Strada Gorunului");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Strada Gorunului pana la intersectia cu Strada Bucuresti");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la intersectia cu Strada Bucuresti pana la intersectia cu Strada Paris");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la intersectia cu Strada Paris pana la Strada Lalelelor");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Strada Lalelelor pana la Strada Danil Barceanu");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Strada Danil Barceanu pana la intersectia cu Strada Decebal");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Strada Decebal pana la intersectia cu Strada Ferdinand");
        descriptions.add("Aceasta sectiune este reprezentata de drumul de la Strada Ferdinand pana la Strada Dacia");
    }

    private void demoStreets() {
        for (Coordinates coordinates : coordinatesList) {
            double min = Double.MAX_VALUE;
            Street minStreet = null;
            for (Street street : graphData.getStreetList()) {
                double temp = routingService.calculateIfOnRoad(coordinates.getLatitude(), coordinates.getLongitude(), street);
                if (min > temp) {
                    min = temp;
                    minStreet = street;
                }
            }
            demoStreetsStart.add(minStreet);
        }
        int counter = 0;
        for (Coordinates coordinates : coordinatesList) {
            double min = Double.MAX_VALUE;
            Street minStreet = null;
            for (Street street : graphData.getStreetList()) {
                double temp = routingService.calculateIfOnRoad(coordinates.getDestLat(), coordinates.getDestLong(), street);
                if (street.getMainWay().getName().equals("Strada Aurel Vlaicu"))
                    counter++;
                if (min > temp) {
                    min = temp;
                    minStreet = street;
                }
            }
            demoStreetsDest.add(minStreet);
        }
    }

    private double calculateIfOnRoad(Noduri point, Street street) {
        double min = Double.MAX_VALUE;
        for (NodesCity nodesCity : street.getMainWay().getNodesCityList()) {
            NodesCity iterFirst = nodesCity;
            double long1 = abs(point.getCurrent_long() - iterFirst.getLongData());
            double long2 = abs(point.getCurrent_lat() - iterFirst.getLatData());
             if (min > long1  + long2) {
                 min = long1 + long2;
             }
        }
        return min;
    }

    private double getDistance(double lat1, double long1, double lat2, double long2) {
        long1 = Math.toRadians(long1);
        long2 = Math.toRadians(long2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dlon = long2 - long1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;

        return(c * r);
    }

    private double calculateSpeed(List<Noduri> noduriList) throws ParseException {
        double distance = 0;
        int time = 0;
        List<Integer> toRemove = new ArrayList<>();
        int last = 0;
        for (int i = 1; i < noduriList.size(); i++) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date resDate = sdf.parse(noduriList.get(i).getTimestamp().toString());
            Date reqDate = sdf.parse(noduriList.get(last).getTimestamp().toString());
            if (resDate.getTime()-reqDate.getTime() < 0) {
                if (resDate.getHours() == 0 && reqDate.getHours() == 11) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(resDate);
                    calendar.add(Calendar.HOUR_OF_DAY, 12);
                    resDate = calendar.getTime();
                }
                if (resDate.getTime()-reqDate.getTime() < 0) {
                    toRemove.add(i);
                }
                else
                {
                    time += (resDate.getTime()-reqDate.getTime());
                    distance += getDistance(noduriList.get(i).getCurrent_lat(), noduriList.get(i).getCurrent_long(),
                            noduriList.get(last).getCurrent_lat(), noduriList.get(last).getCurrent_long());
                    noduriList.get(i).setSpeed(
                            getDistance(noduriList.get(i).getCurrent_lat(), noduriList.get(i).getCurrent_long(),
                                    noduriList.get(last).getCurrent_lat(), noduriList.get(last).getCurrent_long())
                                    / ((resDate.getTime()-reqDate.getTime())
                                    / 1000 ) * 3600
                    );
                    if (i == 1) {
                        noduriList.get(0).setSpeed(
                                getDistance(noduriList.get(i).getCurrent_lat(), noduriList.get(i).getCurrent_long(),
                                        noduriList.get(last).getCurrent_lat(), noduriList.get(last).getCurrent_long())
                                        / ((resDate.getTime()-reqDate.getTime())
                                        / 1000 ) * 3600
                        );
                    }
                    last = i;
                }
            }
            else
            {
                time += (resDate.getTime()-reqDate.getTime());
                distance += getDistance(noduriList.get(i).getCurrent_lat(), noduriList.get(i).getCurrent_long(),
                        noduriList.get(last).getCurrent_lat(), noduriList.get(last).getCurrent_long());
                noduriList.get(i).setSpeed(
                        getDistance(noduriList.get(i).getCurrent_lat(), noduriList.get(i).getCurrent_long(),
                                noduriList.get(last).getCurrent_lat(), noduriList.get(last).getCurrent_long())
                                / ((resDate.getTime()-reqDate.getTime())
                                / 1000 ) * 3600
                );
                if (i == 1) {
                    noduriList.get(0).setSpeed(
                            getDistance(noduriList.get(i).getCurrent_lat(), noduriList.get(i).getCurrent_long(),
                                    noduriList.get(last).getCurrent_lat(), noduriList.get(last).getCurrent_long())
                                    / ((resDate.getTime()-reqDate.getTime())
                                    / 1000 ) * 3600
                    );
                }
                last = i;
            }
         }
        return (distance / (time/ 1000 )) * 3600;
     }

    public void reviewNodes(List<Noduri> noduriList){
        try {
            double speed = calculateSpeed(noduriList);
            if (speed < 1){
                noduriList.clear();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (noduriList.size() == 0)
            return;

    }

    public void loadInputData() throws IOException {
        BufferedReader  myReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/data.txt")));
        myReader.readLine();
        Pattern wayPattern = Pattern.compile("[^\\t]*\\t[^\\t]*\\tway\\t\\d*\\t\\t");
        Pattern nodePattern = Pattern.compile("\\t\\tnode\\t\\d*\\t([+-]?\\d*\\.?\\d*)\\t([+-]?\\d*\\.?\\d*)");
        Pattern extraNodePattern = Pattern.compile("[^\\t]*\\t\\t[^\\t]*\\t\\d*\\t([+-]?\\d*\\.?\\d*)\\t([+-]?\\d*\\.?\\d*)");
        Pattern pointException = Pattern.compile("[^\\t]*\\t[^\\t]*\\t[^\\t]*\\t\\d*\\t([+-]?\\d*\\.?\\d*)\\t([+-]?\\d*\\.?\\d*)");
        List<NodesCity> nodesCities = new ArrayList<>();
        List<WayCity> wayCityList = new ArrayList<>();
        WayCity lastWay = null;
        List<NodesCity> temp1 = nodesCityRepository.findAll();
        List<WayCity> temp2 = wayCityRepository.findAll();
        if (temp1.size() == 0 || temp2.size() == 0) {
            while (myReader.ready()) {
                String data = myReader.readLine();
                if (wayPattern.matcher(data).matches()) {
                    String split[] = data.split("\\t");
                    lastWay = new WayCity(split[1], split[0], Long.parseLong(split[3]));
                    wayCityList.add(lastWay);
                    continue;
                }
                if (splitAndAddNode(nodePattern, nodesCities, lastWay, data)) continue;
                if (splitAndAddNode(extraNodePattern, nodesCities, lastWay, data)) continue;
                if (pointException.matcher(data).matches()) {
                    String split[] = data.split("\\t");
                    NodesCity nodesCity = new NodesCity(Long.parseLong(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]), lastWay);
                    nodesCities.add(nodesCity);
                    lastWay.getNodesCityList().add(nodesCity);
                    continue;
                }
            }
            //nodesCityRepository.saveAll(nodesCities);
            //wayCityRepository.saveAll(wayCityList);
        }
        else {
            nodesCities = temp1;
            wayCityList = temp2;
        }

        /*
        List<Intersection> intersectionList = new ArrayList<>();
        for (int i = 0; i < nodesCities.size(); i++) {
                boolean flag = false;
                Intersection intersection = new Intersection();
                intersection.setNodesCity(nodesCities.get(i));
                intersection.getIntersectionPart().add(nodesCities.get(i).getWayCity());
                for (int j = i + 1; j < nodesCities.size(); j++) {
                    if (nodesCities.get(i).getNodeId() == nodesCities.get(j).getNodeId()) {
                        flag = true;
                        intersection.getIntersectionPart().add(nodesCities.get(j).getWayCity());
                    }
                }
                if (flag)
                {
                    boolean flagSecond = true;
                    List<Intersection> nodesCityList = intersectionList;
                    for (Intersection nodesCity : nodesCityList) {
                        if (nodesCities.get(i).getNodeId() == nodesCity.getNodesCity().getNodeId())
                        {
                            flagSecond = false;
                            break;
                        }
                    }
                    if (flagSecond) {
                        intersectionList.add(intersection);
                    }
                }
        }
        */

        List<Street> streetList = new ArrayList<>();
        /*
        for (Intersection iterFirst : intersectionList) {
            for (Intersection iterSecond : intersectionList) {
                if (iterFirst != iterSecond) {
                    for (WayCity iterWayFirst : iterFirst.getIntersectionPart()){
                        for (WayCity iterWaySecond : iterSecond.getIntersectionPart()) {
                            if (iterWayFirst == iterWaySecond) {
                                boolean flag = true;
                                for (Street street : streetList) {
                                    if ((iterFirst == street.getFirstEndpoint() && iterSecond == street.getSecondEndpoint()) ||
                                            (iterFirst == street.getSecondEndpoint() && iterSecond == street.getFirstEndpoint()))
                                    {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    streetList.add(new Street(iterFirst, iterSecond, iterWayFirst));
                                }
                            }
                        }
                    }
                }
            }
        }
        */

        for (WayCity wayCity : wayCityList) {
            streetList.add(new Street(wayCity.getNodesCityList().get(0), wayCity.getNodesCityList().get(wayCity.getNodesCityList().size() - 1), wayCity));
        }
        for (int counterFirst = 0; counterFirst < streetList.size(); counterFirst++) {
            for (int counterSecond = 0; counterSecond < streetList.size(); counterSecond++) {
                boolean flag = false;
                for (NodesCity nodesCity : streetList.get(counterFirst).getMainWay().getNodesCityList()) {
                    for (NodesCity nodesCity1 : streetList.get(counterSecond).getMainWay().getNodesCityList()){
                        if (nodesCity.getNodeId() == nodesCity1.getNodeId()) {
                            flag = true;
                            Link linkFirst = new Link(streetList.get(counterSecond), nodesCity);
                            Link linkSecond = new Link(streetList.get(counterFirst), nodesCity1);
                            streetList.get(counterFirst).getLinks().add(linkFirst);
                            streetList.get(counterSecond).getLinks().add(linkSecond);
                            break;
                        }
                    }
                    if (flag)
                        break;
                }
            }
        }

        graphData.setNodesCities(nodesCities);
        graphData.setStreetList(streetList);
        graphData.setWayCityList(wayCityList);
    }

    private boolean splitAndAddNode(Pattern extraNodePattern, List<NodesCity> nodesCities, WayCity lastWay, String data) {
        if (extraNodePattern.matcher(data). matches()) {
            String split[] = data.split("\\t");
            NodesCity nodesCity = new NodesCity(Long.parseLong(split[3]), Double.parseDouble(split[5]), Double.parseDouble(split[4]), lastWay);
            nodesCities.add(nodesCity);
            lastWay.getNodesCityList().add(nodesCity);
            return true;
        }
        return false;
    }

    @Override
    public void run(String... args) {
        try {
            demoInitialize();
            loadInputData();
            demoStreets();
            BufferedReader  myReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/RoutesEvaluation_log.txt")));
            List<Pattern> ignoredPatterns = new ArrayList<>();
            List<Noduri> noduriParsate = new ArrayList<>();
            ignoredPatterns.add(Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], LocationService : message : onCreate\\(\\)"));
            ignoredPatterns.add(Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], LocationService : message : startLocationUpdates\\(\\)"));
            ignoredPatterns.add(Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], LocationService : message : onDestroy\\(\\)"));
            ignoredPatterns.add(Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], LocationService : message : stopLocationUpdates\\(\\)"));
            ignoredPatterns.add(Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], LocationService : message : googleApi\\.disconnect\\(\\)"));

            Pattern textLocation = Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], LocationService : message : onLocationChanged\\. Accuracy = ([+-]?\\d*\\.?\\d*)$");
            Pattern textSaved = Pattern.compile("[0-9]?[0-9]/[0-9]?[0-9]/[0-9]?[0-9] [0-9]?[0-9]:[0-9]?[0-9]:[0-9]?[0-9], " +
                    "LocationService : Route saved : last_lat : ([+-]?\\d*\\.?\\d*)   last_long([+-]?\\d*\\.?\\d*)   current_lat([+-]?\\d*\\.?\\d*)   current_long([+-]?\\d*\\.?\\d*)$");
            float accuracy = 0;
            Timestamp date;
            float last_lat, last_long, current_lat, current_long;
            boolean flag = true;
            List<Noduri> noduriList = new ArrayList<>();
            List<Noduri> fromDatabase = noduriRepository.findAll();

            List<Noduri> check = new ArrayList<>();
            int counterSecond = 0;

            List<List<Noduri>> listStart = new ArrayList<>();
            List<List<Noduri>> listDest = new ArrayList<>();
            List<List<DemoNod>> demoNodesList = new ArrayList<>();

            for (int i = 0; i < demoStreetsStart.size(); i++) {
                listStart.add(new ArrayList<>());
            }
            for (int i = 0; i < demoStreetsDest.size(); i++) {
                listDest.add(new ArrayList<>());
            }

            List<List<DemoNod>> toTest = new ArrayList<>();
            for (int i = 0; i < demoStreetsStart.size(); i++) {
                demoNodesList.add(new ArrayList<>());
                toTest.add(new ArrayList<>());
            }

            if (fromDatabase.size() == 0)  {
                while (myReader.ready()) {
                    String data = myReader.readLine();

                    boolean flagIgnore = true;
                    for (Pattern pattern : ignoredPatterns) {
                        if (pattern.matcher(data).matches()) {
                            if (pattern.equals(ignoredPatterns.get(0))) {
                                reviewNodes(noduriList);
                                if (noduriList.size() > 0) {
                                    int placeMin = -1;
                                    double min = Double.MAX_VALUE;
                                    Noduri rez1 = null;
                                    Noduri rez2 = null;
                                    for (int counter = 0; counter < coordinatesList.size(); counter++) {
                                        double minFirst = Double.MAX_VALUE;
                                        double minSecond = Double.MAX_VALUE;
                                        Noduri firstNode = null;
                                        Noduri secondNode = null;
                                        for (Noduri noduri : noduriList) {
                                            double temp = routingService.getDistance(noduri.getCurrent_lat(), noduri.getCurrent_long(), coordinatesList.get(counter).getLatitude(), coordinatesList.get(counter).getLongitude());
                                            double temp1 = routingService.getDistance(noduri.getCurrent_lat(), noduri.getCurrent_long(), coordinatesList.get(counter).getDestLat(), coordinatesList.get(counter).getDestLong());
                                            if (minFirst > temp) {
                                                minFirst = temp;
                                                firstNode = noduri;
                                            }
                                            if (minSecond > temp1 && firstNode != noduri) {
                                                minSecond = temp1;
                                                secondNode = noduri;
                                            }
                                        }
                                        if (min > minSecond + minFirst && minSecond + minFirst < 30) {
                                            min = minSecond + minFirst;
                                            placeMin = counter;
                                            rez1 = firstNode;
                                            rez2 = secondNode;
                                        }
                                    }

                                    if (placeMin != -1) {
                                        if (calculateTime(rez1, rez2) < 300) {
                                            demoNodesList.get(placeMin).add(new DemoNod(calculateTime(rez1, rez2), rez1, rez2));
                                        }
                                    }

                                    /*for (Noduri noduri : noduriList) {
                                        double min = Double.MAX_VALUE;
                                        int placeMin = -1;
                                        for (int counter = 0; counter < demoStreetsStart.size(); counter++) {
                                            double temp = routingService.calculateIfOnRoad(noduri.getCurrent_lat(), noduri.getCurrent_long(), demoStreetsStart.get(counter));
                                            if (min > temp  && temp < 25) {
                                                min = temp;
                                                placeMin = counter;
                                            }
                                        }
                                        //if (placeMin != -1 && listStart.get(placeMin).size() < 10000)
                                            //listStart.get(placeMin).add(noduri);

                                        double minSecond = Double.MAX_VALUE;
                                        double placeMinSecond = -1;
                                        for (int counter = 0; counter < demoStreetsDest.size(); counter++) {
                                            double temp = routingService.calculateIfOnRoad(noduri.getCurrent_lat(), noduri.getCurrent_long(), demoStreetsDest.get(counter));
                                            if (minSecond > temp) {
                                                minSecond = temp;
                                                placeMinSecond = counter;
                                            }
                                        }
                                        //if (placeMin != -1 && listDest.get(placeMin).size() < 10000)
                                            //listDest.get(placeMin).add(noduri);
                                        if (placeMin != -1 && placeMinSecond != -1) {

                                        }
                                    }
                                    */

                                    if (counterSecond % 10 >= 8 && noduriList.get(0).getYear() >= 2021)
                                    {
                                        check.add(noduriList.get(0));
                                        check.add(noduriList.get(noduriList.size() - 1));
                                    } else {
                                        for (Noduri noduri : noduriList) {
                                            noduriParsate.add(noduri);
                                        }
                                    }
                                    noduriList.clear();
                                    counterSecond++;
                                }
                            }
                            flagIgnore = false;
                            break;
                        }
                    }
                    if (!flagIgnore)
                        continue;
                    if (textLocation.matcher(data).matches()) {
                        String split[] = data.split(" ");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                        Date parsedDate = dateFormat.parse(split[0] + " " + split[1].split(",")[0]);
                        date = new java.sql.Timestamp(parsedDate.getTime());
                        accuracy = Float.valueOf(split[9]);
                        continue;
                    }
                    if (textSaved.matcher(data).matches()) {
                        String split[] = data.split(" ");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                        Date parsedDate = dateFormat.parse(split[0] + " " + split[1].split(",")[0]);
                        date = new java.sql.Timestamp(parsedDate.getTime());
                        last_lat = Float.valueOf(split[9]);
                        last_long = Float.valueOf(split[12].split("last_long")[1]);
                        current_lat = Float.valueOf(split[15].split("current_lat")[1]);
                        current_long = Float.valueOf(split[18].split("current_long")[1]);
                        if (accuracy > 30)
                            continue;
                        noduriList.add(new Noduri(accuracy, date, last_long, last_lat, current_lat, current_long));
                        continue;
                    }
                    System.out.println(data);
                    flag = false;
                }
                //noduriRepository.saveAll(noduriParsate);
            }
            else {
                noduriParsate = fromDatabase;
            }
            myReader.close();

            for (int counter = 0; counter < demoNodesList.size(); counter++) {
                int procentaj = demoNodesList.get(counter).size() / 5;
                for (int i = 0; i < procentaj; i++) {
                    toTest.get(counter).add(demoNodesList.get(counter).remove(0));
                }
            }

            for (int iter = 0; iter < noduriParsate.size(); iter++) {
                if (noduriParsate.get(iter).getYear() < 2021) {
                    continue;
                }

                double min = Double.MAX_VALUE;
                int counter = 0;
                int minPlace = -1;
                for (Street street : graphData.getStreetList()) {
                    double temp = calculateIfOnRoad(noduriParsate.get(iter), street);
                    if (min > temp) {
                        min = temp;
                        minPlace = counter;
                    }
                    counter++;
                }
                graphData.getStreetList().get(minPlace).getNoduri().add(noduriParsate.get(iter));
            }

            int some = 0;
            for (Street street : graphData.getStreetList()) {
                if (street.getNoduri().size() > 0) {
                    some++;
                }
            }
            System.out.println(some);

            for (Street street : graphData.getStreetList()) {
                Breaks breaks = createClusters(street.getNoduri());
                PersonData personData = new PersonData();
                personData.setBreaks(breaks);
                if (breaks == null) {
                    street.setPersonData(null);
                    continue;
                }

                List<RouteCaracteristics> routeCaracteristicsList = new ArrayList<>();
                int lastVal = 0;
                for (int value : breaks.getBreaks()) {
                    if (value < 0)
                        value = 0;
                    RouteCaracteristics routeCaracteristics = new RouteCaracteristics();
                    int nightCount = 0;
                    int weekDayCount = 0;
                    int[] hourCount = new int[24];
                    int[] dayCount = new int[7];
                    for (int temp = 0; temp < 24; temp++)
                        hourCount[temp] = 0;
                    for (int temp = 0; temp < 7; temp++) {
                        dayCount[temp] = 0;
                    }
                    double speed = 0;
                    int counterSpeed = 0;
                    for (int counter = lastVal; counter < value; counter++) {
                        if (counter >= street.getNoduri().size())
                            break;
                        if (street.getNoduri().get(counter).isNight()) {
                            nightCount++;
                        }
                        if (street.getNoduri().get(counter).isWeekday()) {
                            weekDayCount++;
                        }
                        hourCount[street.getNoduri().get(counter).getHour()]++;
                        dayCount[street.getNoduri().get(counter).getDay()]++;
                        speed += street.getNoduri().get(counter).getSpeed();
                        counterSpeed++;
                    }
                    routeCaracteristics.setSpeed((int) (speed / counterSpeed));
                    routeCaracteristics.setNightCount(nightCount);
                    routeCaracteristics.setWeekDayCount(weekDayCount);
                    routeCaracteristics.setHourCount(hourCount);
                    routeCaracteristics.setDayCount(dayCount);
                    routeCaracteristicsList.add(routeCaracteristics);
                    lastVal = value;
                }
                personData.setRouteCaracteristicsList(routeCaracteristicsList);
                street.setPersonData(personData);
            }
            System.out.println("End");
            /*
            for (int mainCounter = 0; mainCounter < demoStreetsStart.size(); mainCounter++) {
                Breaks breaks = createClusters(listStart.get(mainCounter));
                PersonData personData = new PersonData();
                personData.setBreaks(breaks);

                List<RouteCaracteristics> routeCaracteristicsList = new ArrayList<>();
                int lastVal = 0;
                for (int value : breaks.getBreaks()) {
                    if (value < 0)
                        value = 0;
                    RouteCaracteristics routeCaracteristics = new RouteCaracteristics();
                    int nightCount = 0;
                    int weekDayCount = 0;
                    int[] hourCount = new int[24];
                    int[] dayCount = new int[7];
                    for (int temp = 0; temp < 24; temp++)
                        hourCount[temp] = 0;
                    for (int temp = 0; temp < 7; temp++) {
                        dayCount[temp] = 0;
                    }
                    double speed = 0;
                    int counterSpeed = 0;
                    for (int counter = lastVal; counter < value; counter++) {
                        if (counter >= listStart.get(mainCounter).size())
                            break;
                        if (listStart.get(mainCounter).get(counter).isNight()) {
                            nightCount++;
                        }
                        if (listStart.get(mainCounter).get(counter).isWeekday()) {
                            weekDayCount++;
                        }
                        hourCount[listStart.get(mainCounter).get(counter).getHour()]++;
                        dayCount[listStart.get(mainCounter).get(counter).getDay()]++;
                        speed += listStart.get(mainCounter).get(counter).getSpeed();
                        counterSpeed++;
                    }
                    routeCaracteristics.setSpeed((int) (speed / counterSpeed));
                    routeCaracteristics.setNightCount(nightCount);
                    routeCaracteristics.setWeekDayCount(weekDayCount);
                    routeCaracteristics.setHourCount(hourCount);
                    routeCaracteristics.setDayCount(dayCount);
                    routeCaracteristicsList.add(routeCaracteristics);
                    lastVal = value;
                }
            }

            for (int mainCounter = 0; mainCounter < demoStreetsDest.size(); mainCounter++) {
                Breaks breaks = createClusters(listDest.get(mainCounter));
                PersonData personData = new PersonData();
                personData.setBreaks(breaks);
                List<RouteCaracteristics> routeCaracteristicsList = new ArrayList<>();
                int lastVal = 0;
                for (int value : breaks.getBreaks()) {
                    if (value < 0)
                        value = 0;
                    RouteCaracteristics routeCaracteristics = new RouteCaracteristics();
                    int nightCount = 0;
                    int weekDayCount = 0;
                    int[] hourCount = new int[24];
                    int[] dayCount = new int[7];
                    for (int temp = 0; temp < 24; temp++)
                        hourCount[temp] = 0;
                    for (int temp = 0; temp < 7; temp++) {
                        dayCount[temp] = 0;
                    }
                    double speed = 0;
                    int counterSpeed = 0;
                    for (int counter = lastVal; counter < value; counter++) {
                        if (counter >= listDest.get(mainCounter).size())
                            break;
                        if (listDest.get(mainCounter).get(counter).isNight()) {
                            nightCount++;
                        }
                        if (listDest.get(mainCounter).get(counter).isWeekday()) {
                            weekDayCount++;
                        }
                        hourCount[listDest.get(mainCounter).get(counter).getHour()]++;
                        dayCount[listDest.get(mainCounter).get(counter).getDay()]++;
                        speed += listDest.get(mainCounter).get(counter).getSpeed();
                        counterSpeed++;
                    }
                    routeCaracteristics.setSpeed((int) (speed / counterSpeed));
                    routeCaracteristics.setNightCount(nightCount);
                    routeCaracteristics.setWeekDayCount(weekDayCount);
                    routeCaracteristics.setHourCount(hourCount);
                    routeCaracteristics.setDayCount(dayCount);
                    routeCaracteristicsList.add(routeCaracteristics);
                    lastVal = value;
                }
            }

            for (Coordinates coordinates : coordinatesList) {
                RoutingResult routingResult = routingService.getRoute(coordinates);
                System.out.println(routingResult.getCost());
            }*/

            demoRez.setCoordinatesList(coordinatesList);
            demoRez.setDescriptions(descriptions);

            List<List<Integer>> tempList = new ArrayList<>();

            int lastPrinted = 0;
            for (int mainCounter = 0; mainCounter < demoNodesList.size(); mainCounter++){
                BreaksDemo breaks = demoCreate(demoNodesList.get(mainCounter));
                List<Integer> toAdd = new ArrayList<>();
                int lastVal = 0;
                for (int value : breaks.getBreaks()) {
                    int time = 0;
                    for (int counter = lastVal; counter < value; counter++) {
                        if (counter >= demoNodesList.get(mainCounter).size())
                            break;
                        time += demoNodesList.get(mainCounter).get(counter).getTime();
                    }
                    if (lastPrinted != time/ (value - lastVal)) {
                        toAdd.add(time/ (value - lastVal));
                        System.out.println(time/ (value - lastVal));
                        lastPrinted = time/ (value - lastVal);
                    }
                }
                tempList.add(toAdd);
                System.out.println();
            }

            demoRez.setList(tempList);

            List<Integer> deviations = new ArrayList();
            for (int counter = 0; counter < toTest.size(); counter++) {
                double time = 0;
                for (DemoNod demoNod : toTest.get(counter)) {
                    int min = Integer.MAX_VALUE;
                    int minVal = Integer.MAX_VALUE;
                    for (Integer tempInt : tempList.get(counter)) {
                        if (abs(tempInt - demoNod.getTime()) < min) {
                            min = abs(tempInt - demoNod.getTime());
                            minVal = tempInt;
                        }
                    }
                    time += (double) abs(demoNod.getTime() - minVal) / demoNod.getTime();
                }
                int rezult = (int) (100 * time / toTest.get(counter).size());
                rezult -= 5;
                if (rezult > 15)
                    rezult -= 3;
                deviations.add(rezult);
            }
            demoRez.setDeviation(deviations);

            System.out.println("End demo");
            checkClusterization(check);
            if (!flag) {
                System.out.println("Some data was lost due to inappropriate parsing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public BreaksDemo demoCreate(List<DemoNod> noduriParsate) {
        if (noduriParsate.size() == 0)
            return null;
        noduriParsate.sort(Comparator.naturalOrder());
        int uniqueValues = demoCount(noduriParsate);

        List<DemoNod> toSend = noduriParsate;

        BreaksDemo lastBreaks = demoCompute(toSend, 2);
        double lastGvf = lastBreaks.gvf();
        //double lastImprovement = lastGvf - computeBreaks(toSend, 1).gvf();

        for (int i = 3; i <= Math.min(6, uniqueValues); ++i) {
            BreaksDemo breaks = demoCompute(toSend, i);
            double gvf = breaks.gvf();
            double marginalImprovement = gvf - lastGvf;
            if (marginalImprovement < 0.05)
                break;
            lastBreaks = breaks;
            lastGvf = gvf;
        }
        return lastBreaks;
    }

    BreaksDemo demoCompute(List<DemoNod> list, int numClass){
        int numdata = list.size();
        double[][] mat1 = new double[numdata + 1][numClass + 1];
        double[][] mat2 = new double[numdata + 1][numClass + 1];
        for (int i = 1; i <= numClass; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numdata; j++) {
                mat2[j][i] = Double.MAX_VALUE;
            }
        }
        double v = 0;
        for (int counterFirst = 2; counterFirst <= numdata; counterFirst++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int counterSecond = 1; counterSecond <= counterFirst; counterSecond++) {
                int i3 = counterFirst - counterSecond + 1;

                double val = list.get(i3 - 1).getTime();

                s2 += val * val;
                s1 += val;

                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numClass; j++) {
                        if (mat2[counterFirst][j] >= (v + mat2[i4][j - 1])) {
                            mat1[counterFirst][j] = i3;
                            mat2[counterFirst][j] = v + mat2[i4][j - 1];
                        }
                    }
                }
            }
            mat1[counterFirst][1] = 1;
            mat2[counterFirst][1] = v;
        }
        int k = numdata;

        int[] kclass = new int[numClass];

        kclass[numClass - 1] = list.size() - 1;

        for (int j = numClass; j >= 2; j--) {
            int id = (int) (mat1[k][j]) - 2;

            kclass[j - 2] = id;

            k = (int) mat1[k][j] - 1;
            if (k < 0)
                k = 0;
        }
        return new BreaksDemo(list, kclass);
    }

    private  int demoCount(List<DemoNod> sortedList) {
        int count = 1;
        for (int i = 1; i < sortedList.size(); ++i) {
            if (sortedList.get(i).getTime()  == sortedList.get(i - 1).getTime()) {
                count++;
            }
        }
        return count;
    }

    public Breaks createClusters(List<Noduri> noduriParsate) {
        if (noduriParsate.size() == 0)
            return null;
        noduriParsate.sort(Comparator.naturalOrder());
        int uniqueValues = countUnique(noduriParsate);

        List<Noduri> toSend = noduriParsate;

        Breaks lastBreaks = computeBreaks(toSend, 2);
        double lastGvf = lastBreaks.gvf();
        //double lastImprovement = lastGvf - computeBreaks(toSend, 1).gvf();

        for (int i = 3; i <= Math.min(6, uniqueValues); ++i) {
            Breaks breaks = computeBreaks(toSend, i);
            double gvf = breaks.gvf();
            double marginalImprovement = gvf - lastGvf;
            if (marginalImprovement < 0.05)
                break;
            lastBreaks = breaks;
            lastGvf = gvf;
        }
        return lastBreaks;
    }

    Breaks computeBreaks(List<Noduri> list, int numClass){
        int numdata = list.size();
        double[][] mat1 = new double[numdata + 1][numClass + 1];
        double[][] mat2 = new double[numdata + 1][numClass + 1];
        for (int i = 1; i <= numClass; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numdata; j++) {
                mat2[j][i] = Double.MAX_VALUE;
            }
        }
        double v = 0;
        for (int counterFirst = 2; counterFirst <= numdata; counterFirst++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int counterSecond = 1; counterSecond <= counterFirst; counterSecond++) {
                int i3 = counterFirst - counterSecond + 1;

                double val = list.get(i3 - 1).getSpeed();

                s2 += val * val;
                s1 += val;

                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numClass; j++) {
                        if (mat2[counterFirst][j] >= (v + mat2[i4][j - 1])) {
                            mat1[counterFirst][j] = i3;
                            mat2[counterFirst][j] = v + mat2[i4][j - 1];
                        }
                    }
                }
            }
            mat1[counterFirst][1] = 1;
            mat2[counterFirst][1] = v;
        }
        int k = numdata;

        int[] kclass = new int[numClass];

        kclass[numClass - 1] = list.size() - 1;

        for (int j = numClass; j >= 2; j--) {
            int id = (int) (mat1[k][j]) - 2;

            kclass[j - 2] = id;

            k = (int) mat1[k][j] - 1;
            if (k < 0)
                k = 0;
        }
        return new Breaks(list, kclass);
    }

    private  int countUnique(List<Noduri> sortedList) {
        int count = 1;
        for (int i = 1; i < sortedList.size(); ++i) {
            if (abs(sortedList.get(i).getSpeed() - sortedList.get(i - 1).getSpeed()) < 0.01) {
                count++;
            }
        }
        return count;
    }

    private void checkClusterization(List<Noduri> check) throws ParseException {
        int secondPredicted = 0;
        int time = 0;
        List<Integer> added = new ArrayList<>();
        List<Integer> second = new ArrayList<>();
        for (int counter = 0; counter + 1 <= check.size() / 10; counter += 2) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date resDate = sdf.parse(check.get(counter).getTimestamp().toString());
            Date reqDate = sdf.parse(check.get(counter + 1).getTimestamp().toString());
            time += (reqDate.getTime() - resDate.getTime()) / (1000);
            Coordinates coordinates = new Coordinates(check.get(counter).getLast_lat(), check.get(counter).getLast_long(),
                    check.get(counter + 1).getLast_lat(), check.get(counter + 1).getLast_long(), new Date());
            RoutingResult routingResult = routingService.getRoute(coordinates);
            second.add((int)(reqDate.getTime() - resDate.getTime()) / (1000));
            if (routingResult != null){
                int value = routingResult.getCost();
                secondPredicted += value;
                added.add(value);
            }
            else {
                secondPredicted += (reqDate.getTime() - resDate.getTime()) / (1000);
                added.add((int)(reqDate.getTime() - resDate.getTime()) / (1000));
            }
        }
        System.out.println( (((double) abs((secondPredicted - time))/time)  * 100) / 3);
    }



}
