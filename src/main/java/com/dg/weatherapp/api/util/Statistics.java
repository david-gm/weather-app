package com.dg.weatherapp.api.util;

import java.util.List;

public class Statistics {
    public static Double computeMean(List<Double> data) {
        double sum = 0.0;
        int size = 0;
        for (Double d : data) {
            if (d == null)
                continue;
            size++;
            sum += d;
        }
        return sum / (double) size;
    }

    public static Double computeStd(List<Double> data) {
        Double mean = Statistics.computeMean(data);
        double sum = 0.0;
        int size = 0;
        for (Double d : data) {
            if (d == null)
                continue;
            size++;
            sum += Math.pow((d - mean), 2);
        }

        return Math.sqrt(1.0 / (double) size * sum);
    }
}
