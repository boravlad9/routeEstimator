package code.roadEstimator.services;

import code.roadEstimator.entities.*;
import code.roadEstimator.repositories.NoduriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.lang.Math.*;

@Component
public class FileInitializer implements CommandLineRunner {
    private final NoduriRepository noduriRepository;
    private GraphData graphData = null;

    @Autowired
    public FileInitializer(NoduriRepository noduriRepository) {
        this.noduriRepository = noduriRepository;
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
        graphData = new GraphData();
        BufferedReader  myReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/data.txt")));
        myReader.readLine();
        Pattern wayPattern = Pattern.compile("[^\\t]*\\t[^\\t]*\\tway\\t\\d*\\t\\t");
        Pattern nodePattern = Pattern.compile("\\t\\tnode\\t\\d*\\t([+-]?\\d*\\.?\\d*)\\t([+-]?\\d*\\.?\\d*)");
        List<NodesCity> nodesCities = new ArrayList<>();
        WayCity lastWay = null;
        while (myReader.ready()) {
            String data = myReader.readLine();
            if (wayPattern.matcher(data).matches()) {
                String split[] = data.split("\\t");
                lastWay = new WayCity(split[0], split[1], Long.parseLong(split[3]));
                graphData.addWay(lastWay);
                continue;
            }
            if(nodePattern.matcher(data).matches()) {
                String split[] = data.split("\\t");
                nodesCities.add(new NodesCity(Long.parseLong(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]), lastWay));
                continue;
            }
        }
        for (int i = 0; i < nodesCities.size(); i++) {
            boolean flag = false;
            for (int j = i + 1; j < nodesCities.size(); j++) {
                if (nodesCities.get(i).getNodeId() == nodesCities.get(j).getNodeId()) {
                    flag = true;
                    break;
                }
            }
            if (flag)
            {
                boolean flagSecond = true;
                List<NodesCity> nodesCityList = graphData.getNodesCities();
                for (NodesCity nodesCity : nodesCityList) {
                    if (nodesCities.get(i).getNodeId() == nodesCity.getNodeId())
                    {
                        flagSecond = false;
                        break;
                    }
                }
                if (flagSecond) {
                    graphData.addNode(nodesCities.get(i));
                }
            }
        }
    }

    @Override
    public void run(String... args) {
        try {
            loadInputData();
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
            while (myReader.ready()) {
                String data = myReader.readLine();

                boolean flagIgnore = true;
                for (Pattern pattern : ignoredPatterns) {
                    if (pattern.matcher(data).matches()) {
                        if (pattern.equals(ignoredPatterns.get(0))) {
                            reviewNodes(noduriList);
                            for (Noduri noduri : noduriList) {
                                noduriParsate.add(noduri);
                            }
                            noduriList.clear();
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
            myReader.close();
            createClusters(noduriParsate);
            if (!flag) {
                System.out.println("Some data was lost due to inappropriate parsing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createClusters(List<Noduri> noduriParsate) {
        noduriParsate.sort(Comparator.naturalOrder());

        int uniqueValues = countUnique(noduriParsate);

        List<Noduri> toSend = new ArrayList();
        for (int i = 0; i < noduriParsate.size(); i += 100) {
            toSend.add(noduriParsate.get(i));
        }

        Breaks lastBreaks = computeBreaks(toSend, 2);
        double lastGvf = lastBreaks.gvf();
        double lastImprovement = lastGvf - computeBreaks(toSend, 1).gvf();

        for (int i = 3; i <= Math.min(6, uniqueValues); ++i) {
            Breaks breaks = computeBreaks(toSend, i);
            double gvf = breaks.gvf();
            double marginalImprovement = gvf - lastGvf;
            //if (marginalImprovement < lastImprovement) {
               // break;
            //}
            lastBreaks = breaks;
            lastGvf = gvf;
            lastImprovement = marginalImprovement;
        }
        System.out.println(lastBreaks);
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
        for (int l = 2; l <= numdata; l++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int m = 1; m <= l; m++) {
                int i3 = l - m + 1;

                double val = list.get(i3 - 1).getSpeed();

                s2 += val * val;
                s1 += val;

                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numClass; j++) {
                        if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
                            mat1[l][j] = i3;
                            mat2[l][j] = v + mat2[i4][j - 1];
                        }
                    }
                }
            }
            mat1[l][1] = 1;
            mat2[l][1] = v;
        }
        int k = numdata;

        int[] kclass = new int[numClass];

        kclass[numClass - 1] = list.size() - 1;

        for (int j = numClass; j >= 2; j--) {
            int id = (int) (mat1[k][j]) - 2;

            kclass[j - 2] = id;

            k = (int) mat1[k][j] - 1;
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

    public void calculateDeviation(List<Noduri> noduriList) {
        List<List<Noduri>> bucketsZile = new ArrayList<>();
        List<List<Noduri>> bucketsDayNight = new ArrayList<>();
        List<List<Noduri>> bucketsWeekday = new ArrayList<>();
        List<List<Noduri>> bucketsHour = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            bucketsZile.add(new ArrayList<>());
        }
        for (int i = 0; i < 2; i++) {
            bucketsDayNight.add(new ArrayList<>());
        }
        for (int i = 0; i < 2; i++) {
            bucketsWeekday.add(new ArrayList<>());
        }
        for (int i = 0; i < 24; i++) {
            bucketsHour.add(new ArrayList<>());
        }
        for (Noduri noduri : noduriList) {
            bucketsZile.get(noduri.getTimestamp().getDay()).add(noduri);
        }
        for (Noduri noduri : noduriList) {
            if (noduri.getTimestamp().getHours() < 12) {
                bucketsDayNight.get(0).add(noduri);
            } else {
                bucketsDayNight.get(1).add(noduri);
            }
        }
        for (Noduri noduri : noduriList) {
            if (noduri.getTimestamp().getDay() < 5) {
                bucketsWeekday.get(0).add(noduri);
            } else {
                bucketsWeekday.get(1).add(noduri);
            }
        }
        for (Noduri noduri : noduriList) {
            bucketsHour.get(noduri.getTimestamp().getHours()).add(noduri);
        }
        for (List<Noduri> liste : bucketsZile) {
            int sum = 0;
            for (Noduri noduri : liste) {
                sum += noduri.getSpeed();
            }
            sum /= liste.size();
            long deviation = 0;
            for (Noduri noduri : liste) {
                deviation += (sum - noduri.getSpeed()) * (sum - noduri.getSpeed());
            }
        }
    }

    private List<Integer> getDeviation(List<List<Noduri>> total) {
        List<Integer> deviations = new ArrayList<>();
        for (List<Noduri> liste : total) {
            int sum = 0;
            for (Noduri noduri : liste) {
                sum += noduri.getSpeed();
            }
            sum /= liste.size();
            int deviation = 0;
            for (Noduri noduri : liste) {
                deviation += (sum - noduri.getSpeed()) * (sum - noduri.getSpeed());
            }
            deviations.add(deviation);
        }
        return deviations;
    }
}
