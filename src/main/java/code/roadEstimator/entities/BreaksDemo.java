package code.roadEstimator.entities;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class BreaksDemo {

    private List<DemoNod> sortedValues;
    private int[] breaks;

    public BreaksDemo(List<DemoNod> sortedValues, int[] breaks) {
        this.sortedValues = sortedValues;
        this.breaks = breaks;
    }

    public double gvf() {
        double sdam = sumOfSquareDeviations(sortedValues);
        double sdcm = 0.0;
        for (int i = 0; i != numClassses(); ++i) {
            sdcm += sumOfSquareDeviations(classList(i));
        }
        return (sdam - sdcm) / sdam;
    }


    private List<DemoNod> classList(int i) {
        int classStart = max(0, (i == 0) ? 0 : breaks[i - 1] + 1);
        int classEnd = breaks[i];
        List<DemoNod> list = new ArrayList<DemoNod>();
        for (int j = classStart; j <= classEnd; ++j) {
            list.add(sortedValues.get(j));
        }
        return list;
    }

    private double sumOfSquareDeviations(List<DemoNod>  values) {
        double mean = mean(values);
        double sum = 0.0;
        for (int i = 0; i != values.size(); ++i) {
            double sqDev = Math.pow(values.get(i).getTime() - mean, 2);
            sum += sqDev;
        }
        return sum;
    }

    private double mean(List<DemoNod>  values) {
        double sum = 0;
        for (int i = 0; i != values.size(); ++i) {
            sum += values.get(i).getTime();
        }
        return sum / values.size();
    }

    public int numClassses() {
        return breaks.length;
    }


    public List<DemoNod> getSortedValues() {
        return sortedValues;
    }

    public void setSortedValues(List<DemoNod> sortedValues) {
        this.sortedValues = sortedValues;
    }

    public int[] getBreaks() {
        return breaks;
    }

    public void setBreaks(int[] breaks) {
        this.breaks = breaks;
    }
}
