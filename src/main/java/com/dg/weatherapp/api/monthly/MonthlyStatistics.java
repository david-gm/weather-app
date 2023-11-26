package com.dg.weatherapp.api.monthly;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MonthlyStatistics {
    private Integer month;
    private Double mean;
    private Double standardDeviation;
    private List<Double> data;
    private List<Double> deviationFromMean;
    private List<LocalDateTime> datetime;

    MonthlyStatistics(Integer month) {
        this.month = month;
        this.data = new ArrayList<>();
        this.deviationFromMean = new ArrayList<>();
        this.datetime = new ArrayList<>();
    }
}
