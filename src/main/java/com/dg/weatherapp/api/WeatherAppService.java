package com.dg.weatherapp.api;

import com.dg.weatherapp.api.location.Location;
import com.dg.weatherapp.api.location.LocationRepository;
import com.dg.weatherapp.api.monthly.Monthly;
import com.dg.weatherapp.api.monthly.MonthlyData;
import com.dg.weatherapp.api.monthly.MonthlyDataTransferObject;
import com.dg.weatherapp.api.monthly.MonthlyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class WeatherAppService {


    final static DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ObjectMapper objectMapper;
    private final ObjectReader readerDouble;
    private final ObjectReader readerString;

    private final LocationRepository locationRepository;
    private final MonthlyRepository monthlyRepository;


    WeatherAppService(LocationRepository locationRepository, MonthlyRepository monthlyRepository) {
        this.objectMapper = new ObjectMapper();
        this.readerDouble = objectMapper.readerFor(new TypeReference<List<Double>>() {
        });
        this.readerString = objectMapper.readerFor(new TypeReference<List<String>>() {
        });
        this.locationRepository = locationRepository;
        this.monthlyRepository = monthlyRepository;
    }

    public Monthly save(Monthly monthlyData) {
        return monthlyRepository.save(monthlyData);
    }

    public Monthly createMonthlyData(Location requestedLocation) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        Locale locale = LocaleContextHolder.getLocale();
        String latLon = String.format(locale, "%f,%f", requestedLocation.getLatitude(), requestedLocation.getLongitude());

        String url = String.format("%s?parameters=Tm&parameters=RR&start=1961-01-01T00:00&end=%s&lat_lon=%s&output_format=geojson",
                GeosphereURLs.URL_MONTHLY_1KM, getDateTimeYesterday(), latLon
        );

        String result = restTemplate.getForObject(url, String.class);

        if (result != null) {
            JsonNode mapJson = objectMapper.readTree(result);
            Location mappedLocation = createLocationFromString(mapJson);

            return createMonthlyFromString(mapJson, requestedLocation, mappedLocation);
        }
        return null;
    }

    private static String getDateTimeYesterday() {
        LocalDateTime dtNow = java.time.LocalDateTime.now();
        LocalDateTime dtYesterday = dtNow.minusDays(1);
        return dtYesterday.format(CUSTOM_FORMATTER) + "T23:59";
    }

    private Location createLocationFromString(JsonNode mapJson) throws IOException {
        JsonNode jsonLocation = mapJson.get("features").get(0).get("geometry").get("coordinates");
        ArrayList<Double> locationArray = readerDouble.readValue(jsonLocation);

        Location location = new Location();
        location.setLatitude(locationArray.get(1));
        location.setLongitude(locationArray.get(0));

        return location;
    }

    private Monthly createMonthlyFromString(JsonNode mapJson, Location requestedlocation, Location mappedLocation) throws IOException {
        JsonNode jsonTemp = mapJson.get("features").get(0).get("properties").get("parameters").get("Tm").get("data");
        ArrayList<Double> temperature = readerDouble.readValue(jsonTemp);

        JsonNode jsonPercipitation = mapJson.get("features").get(0).get("properties").get("parameters").get("RR").get("data");
        ArrayList<Double> percipitation = readerDouble.readValue(jsonPercipitation);

        JsonNode jsonTimestamps = mapJson.get("timestamps");
        ArrayList<String> datetime = readerString.readValue(jsonTimestamps);
        ArrayList<LocalDateTime> datetimes_ = new ArrayList<>();
        for (String dt : datetime) {
            LocalDateTime dateTime = LocalDateTime.parse(dt.substring(0, 16));
            datetimes_.add(dateTime);
        }

        ArrayList<MonthlyData> monthlyDataList = new ArrayList<>();
        for (int i = 0; i < temperature.size(); i++) {
            MonthlyData monthlyData = new MonthlyData();
            monthlyData.setTemperature(temperature.get(i));
            monthlyData.setPrecipitation(percipitation.get(i));
            monthlyData.setDatetime(datetimes_.get(i));


            monthlyDataList.add(monthlyData);
        }

        Monthly monthly = new Monthly();
        monthly.setMonthlyData(monthlyDataList);
        monthly.setLocation(requestedlocation);
        monthly.setMappedLatitude(mappedLocation.getLatitude());
        monthly.setMappedLongitude(mappedLocation.getLongitude());

        return monthly;
    }

    public Optional<Monthly> getMonthlyById(Long locationId) {
        return monthlyRepository.findByLocationId(locationId);
    }

    public MonthlyDataTransferObject mapMonthly(Monthly monthly, LocalDate startDate, LocalDate endDate) {
        MonthlyDataTransferObject mdtro = new MonthlyDataTransferObject();
        mdtro.setId(monthly.getId());
        mdtro.setLocation(monthly.getLocation());

        mdtro.setDatetime(monthly.getMonthlyData().stream().map(MonthlyData::getDatetime).collect(Collectors.toList()));
        mdtro.setTemperature(monthly.getMonthlyData().stream().map(MonthlyData::getTemperature).collect(Collectors.toList()));
        mdtro.setPrecipitation(monthly.getMonthlyData().stream().map(MonthlyData::getPrecipitation).collect(Collectors.toList()));

        mdtro.filterByDate(startDate, endDate);
        mdtro.addAdditionalData();

        return mdtro;
    }
}
