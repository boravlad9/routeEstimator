package code.roadEstimator.entities;

import java.util.ArrayList;
import java.util.List;

public class Breaks {

    private List<Noduri> sortedValues;
    private int[] breaks;

    public Breaks(List<Noduri> sortedValues, int[] breaks) {
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

    private double sumOfSquareDeviations(List<Noduri>  values) {
        double mean = mean(values);
        double sum = 0.0;
        for (int i = 0; i != values.size(); ++i) {
            double sqDev = Math.pow(values.get(i).getSpeed() - mean, 2);
            sum += sqDev;
        }
        return sum;
    }

    public List<Noduri> getValues() {
        return sortedValues;
    }

    private List<Noduri> classList(int i) {
        int classStart = (i == 0) ? 0 : breaks[i - 1] + 1;
        int classEnd = breaks[i];
        List<Noduri> list = new ArrayList<Noduri>();
        for (int j = classStart; j <= classEnd; ++j) {
            list.add(sortedValues.get(j));
        }
        return list;
    }


    public Noduri getClassMin(int classIndex) {
        if (classIndex == 0) {
            return sortedValues.get(0);
        } else {
            return sortedValues.get(breaks[classIndex - 1] + 1);
        }
    }

    public Noduri getClassMax(int classIndex) {
        return sortedValues.get(breaks[classIndex]);
    }

    public int getClassCount(int classIndex) {
        if (classIndex == 0) {
            return breaks[0] + 1;
        } else {
            return breaks[classIndex] - breaks[classIndex - 1];
        }
    }

    private double mean(List<Noduri>  values) {
        double sum = 0;
        for (int i = 0; i != values.size(); ++i) {
            sum += values.get(i).getSpeed();
        }
        return sum / values.size();
    }

    public int numClassses() {
        return breaks.length;
    }

    /*public int classOf(double value) {
        for (int i = 0; i != numClassses(); ++i) {
            if (value <= getClassMax(i)) {
                return i;
            }
        }
        return numClassses() - 1;
    }*/
}