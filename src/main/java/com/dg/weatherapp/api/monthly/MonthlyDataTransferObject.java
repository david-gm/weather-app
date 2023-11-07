package com.dg.weatherapp.api.monthly;

import com.dg.weatherapp.api.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class MonthlyDataTransferObject {
    private Long id;
    private Location location;
    private List<Double> temperature;
    private List<Double> precipitation;
    private List<LocalDateTime> datetime;
}
