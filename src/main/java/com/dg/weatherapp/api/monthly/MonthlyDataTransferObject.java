package com.dg.weatherapp.api.monthly;

import com.dg.weatherapp.api.location.Location;
import com.dg.weatherapp.api.util.Statistics;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Slf4j
public class MonthlyDataTransferObject {
    private Long id;
    private Location location;
    private List<Double> temperature;
    private List<Double> precipitation;
    private List<LocalDateTime> datetime;
    private HashMap<Integer, DataDescriptorMonths> temperatureByMonth; // <year, <monthly temp as list>>
    private HashMap<Integer, MonthlyStatistics> temperatureByMonthStatistics; // <month, statistics>

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

    public void addAdditionalData() {
        addDataByMonth();
        addMonthStatistics();
    }

    private void addDataByMonth() {
        temperatureByMonth = new HashMap<>();

        Set<Integer> uniqueYears = new HashSet<>();
        for (LocalDateTime localDateTime : datetime) {
            uniqueYears.add(localDateTime.getYear());
        }

        for (Integer uniqueYear : uniqueYears) {
            DataDescriptorMonths ddm = new DataDescriptorMonths();

            for (int i = 0; i < datetime.size(); i++) {
                LocalDateTime dt = datetime.get(i);
                if (dt.getYear() == uniqueYear) {
                    ddm.getMonths().add(dt.getMonthValue());
                    ddm.getData().add(temperature.get(i));
                }
            }

            temperatureByMonth.put(uniqueYear, ddm);
        }
    }

    private void addMonthStatistics() {
        temperatureByMonthStatistics = new HashMap<Integer, MonthlyStatistics>();
        List<Integer> months = getMonthsFromDateTimes();
        for (Integer month : months) {
            MonthlyStatistics ms = new MonthlyStatistics(month);
            ArrayList<Double> dataSortedByMonth = new ArrayList<>();
            for (int i = 0; i < datetime.size(); i++) {
                if (datetime.get(i).getMonthValue() != month)
                    continue;
                dataSortedByMonth.add(temperature.get(i));
                ms.getDatetime().add(datetime.get(i));
            }
            ms.setData(dataSortedByMonth);
            ms.setMean(Statistics.computeMean(dataSortedByMonth));
            ms.setStandardDeviation(Statistics.computeStd(dataSortedByMonth));

            List<Double> deviation = dataSortedByMonth.stream().map(value -> {
                if (value == null)
                    return null;
                return value - ms.getMean();
            }).collect(Collectors.toList());
            ms.setDeviationFromMean(deviation);
            temperatureByMonthStatistics.put(month, ms);
        }
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

    private List<Integer> getMonthsFromDateTimes() {
        Set<Integer> months = new HashSet<>();
        for (LocalDateTime dt : datetime) {
            months.add(dt.getMonthValue());
        }
        return months.stream().toList();
    }
}
