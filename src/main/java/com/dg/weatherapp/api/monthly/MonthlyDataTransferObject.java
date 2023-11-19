package com.dg.weatherapp.api.monthly;

import com.dg.weatherapp.api.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class MonthlyDataTransferObject {
    private Long id;
    private Location location;
    private List<Double> temperature;
    private List<Double> precipitation;
    private List<LocalDateTime> datetime;

    public void filterByDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null)
            return;

        List<LocalDateTime> newDatetime = new ArrayList<>();
        List<Double> newTemperature = new ArrayList<>();
        List<Double> newPrecipitation = new ArrayList<>();

        for (int i = 0; i < datetime.size(); i++) {
            if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
                if (checkStartDate(datetime.get(i), startDate) && checkEndDate(datetime.get(i), endDate))
                    addToNewData(i, newDatetime, newTemperature, newPrecipitation);
            } else if (Objects.nonNull(startDate)) {
                if (checkStartDate(datetime.get(i), startDate))
                    addToNewData(i, newDatetime, newTemperature, newPrecipitation);
            } else {
                if (checkEndDate(datetime.get(i), endDate))
                    addToNewData(i, newDatetime, newTemperature, newPrecipitation);
            }
        }
        datetime = newDatetime;
        temperature = newTemperature;
        precipitation = newPrecipitation;
    }

    private boolean checkStartDate(LocalDateTime date, LocalDate startDate) {
        if (date.getYear() > startDate.getYear())
            return true;
        else if (date.getYear() == startDate.getYear()) {
            return date.getMonthValue() >= startDate.getMonthValue();
        }
        return false;
    }

    private boolean checkEndDate(LocalDateTime date, LocalDate endDate) {
        if (date.getYear() < endDate.getYear())
            return true;
        else if (date.getYear() == endDate.getYear()) {
            return date.getMonthValue() <= endDate.getMonthValue();
        }
        return false;
    }

    private void addToNewData(int index, List<LocalDateTime> newDatetime, List<Double> newTemperature, List<Double> newPrecipitation) {
        newDatetime.add(datetime.get(index));
        newTemperature.add(temperature.get(index));
        newPrecipitation.add(precipitation.get(index));
    }
}
