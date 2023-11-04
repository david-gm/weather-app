package com.dg.weatherapp.api;

import com.dg.weatherapp.api.monthly.MonthlyData;
import com.dg.weatherapp.api.monthly.MonthlyDataRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
@Slf4j
public class WeatherAppService {

    private final ObjectMapper objectMapper;
    private final ObjectReader readerDouble;
    private final ObjectReader readerString;

    private final MonthlyDataRepository monthlyDataRepository;

    WeatherAppService(MonthlyDataRepository monthlyDataRepository) {
        this.objectMapper = new ObjectMapper();
        this.readerDouble = objectMapper.readerFor(new TypeReference<List<Double>>() {
        });
        this.readerString = objectMapper.readerFor(new TypeReference<List<String>>() {
        });
        this.monthlyDataRepository = monthlyDataRepository;
    }

    public MonthlyData save(MonthlyData monthlyData) {
        List<MonthlyData> results = monthlyDataRepository.findByLatitudeAndLongitude(monthlyData.getLatitude(), monthlyData.getLongitude());
        if(results.isEmpty()) {
            return monthlyDataRepository.save(monthlyData);
        }
        return null;
    }

    public MonthlyData createMonthlyData(double[] coordinates) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        Locale locale = LocaleContextHolder.getLocale();
        String latLon = String.format(locale, "%f,%f", coordinates[0], coordinates[1]);
        String url = String.format("%s?parameters=Tm&parameters=RR&start=1961-01-01T00:00&end=2023-10-28T00:00&lat_lon=%s&output_format=geojson",
                GeosphereURLs.URL_MONTHLY_1KM, latLon
        );

        String result = restTemplate.getForObject(url, String.class);

        if (result != null) {
            return createMonthlyDataFromString(result);
        }
        return null;
    }

    private MonthlyData createMonthlyDataFromString(String jsonString) throws IOException {
        JsonNode mapJson = objectMapper.readTree(jsonString);

        JsonNode jsonLocation = mapJson.get("features").get(0).get("geometry").get("coordinates");
        ArrayList<Double> location = readerDouble.readValue(jsonLocation);

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

        MonthlyData monthlyData = new MonthlyData();
        monthlyData.setLatitude(location.get(0));
        monthlyData.setLongitude(location.get(1));
        monthlyData.setTemperature(temperature);
        monthlyData.setPrecipitation(percipitation);
        monthlyData.setDatetime(datetimes_);

        return monthlyData;
    }

    public Optional<MonthlyData> getMonthlyDataById(Long id) {
        return monthlyDataRepository.findById(id);
    }
}
