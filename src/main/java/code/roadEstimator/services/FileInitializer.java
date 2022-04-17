package code.roadEstimator.services;

import code.roadEstimator.entities.Noduri;
import code.roadEstimator.repositories.NoduriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@Component
public class FileInitializer implements CommandLineRunner {
    private final NoduriRepository noduriRepository;

    @Autowired
    public FileInitializer(NoduriRepository noduriRepository) {
        this.noduriRepository = noduriRepository;
    }

    public void reviewNodes(List<Noduri> noduriList){

    }

    @Override
    public void run(String... args) {
        try {
            BufferedReader  myReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/RoutesEvaluation_log.txt")));
            List<Pattern> ignoredPatterns = new ArrayList<>();
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
                        if (pattern.equals(ignoredPatterns.get(0)))
                        {
                            reviewNodes(noduriList);
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
                    noduriList.add(new Noduri(accuracy, date, last_long, last_lat, current_lat, current_long));
                    continue;
                }
                System.out.println(data);
                flag = false;
            }
            myReader.close();
            if (!flag) {
                System.out.println("Some data was lost due to inappropriate parsing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
