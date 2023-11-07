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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class WeatherAppService {

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
//        List<Monthly> results = monthlyDataRepository.findByLatitudeAndLongitude(monthlyData.getLatitude(), monthlyData.getLongitude());
//        if(results.isEmpty()) {
            return monthlyRepository.save(monthlyData);
//        }
//        return null;
    }

    public Monthly createMonthlyData(double[] coordinates) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        Locale locale = LocaleContextHolder.getLocale();
        String latLon = String.format(locale, "%f,%f", coordinates[0], coordinates[1]);
        String url = String.format("%s?parameters=Tm&parameters=RR&start=1961-01-01T00:00&end=2023-10-28T00:00&lat_lon=%s&output_format=geojson",
                GeosphereURLs.URL_MONTHLY_1KM, latLon
        );

        String result = restTemplate.getForObject(url, String.class);

        if (result != null) {
            JsonNode mapJson = objectMapper.readTree(result);
            Location location = createLocationFromString(mapJson);
            List <Location> locFound = locationRepository.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude());

            if(locFound.isEmpty())
                locationRepository.save(location);
            else
                return null;
            return createMonthlyFromString(mapJson, location);
        }
        return null;
    }

    private Location createLocationFromString(JsonNode mapJson) throws IOException {
        JsonNode jsonLocation = mapJson.get("features").get(0).get("geometry").get("coordinates");
        ArrayList<Double> locationArray = readerDouble.readValue(jsonLocation);

        Location location = new Location();
        location.setLatitude(locationArray.get(0));
        location.setLongitude(locationArray.get(1));

        return location;
    }

    private Monthly createMonthlyFromString(JsonNode mapJson, Location location) throws IOException {
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
        monthly.setLocation(location);

        return monthly;
    }

    public Optional<Monthly> getMonthlyById(Long id) {
        return monthlyRepository.findById(id);
    }

    public MonthlyDataTransferObject mapMonthly(Monthly monthly)
    {
        MonthlyDataTransferObject mdtro = new MonthlyDataTransferObject();
        mdtro.setId(monthly.getId());
        mdtro.setLocation(monthly.getLocation());

        mdtro.setDatetime(monthly.getMonthlyData().stream().map(MonthlyData::getDatetime).collect(Collectors.toList()));
        mdtro.setTemperature(monthly.getMonthlyData().stream().map(MonthlyData::getTemperature).collect(Collectors.toList()));
        mdtro.setPrecipitation(monthly.getMonthlyData().stream().map(MonthlyData::getPrecipitation).collect(Collectors.toList()));
        return mdtro;
    }
}
