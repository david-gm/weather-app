package com.dg.weatherapp.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import com.dg.weatherapp.api.monthly.MonthlyData;

@RestController()
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class APIController {

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);
    private static final String BASE_URL = "https://dataset.api.hub.geosphere.at/v1/";
    private final WeatherAppService weatherAppService;

    APIController(WeatherAppService weatherAppService) {
        this.weatherAppService = weatherAppService;
    }

    @PostConstruct
    private void init() {
        LOG.debug(String.format("This is a test message from the %s", APIController.class));
    }

    @GetMapping("/")
    public String index() {
        return "test";
    }

    @GetMapping(value = "/external")
    public Map<String, Object> testExternalAPI() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(BASE_URL + "datasets", String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            if (result != null)
                return objectMapper.readValue(result.getBytes(), new TypeReference<>() {
                });
            else
                return new HashMap<>();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Foo Not Found", e);
        }
    }

    @GetMapping(value = "/monthly/{id}")
    public ResponseEntity<MonthlyData> getMonthlyData(@PathVariable Long id) {
        try {
            Optional<MonthlyData> monthlyData = weatherAppService.getMonthlyDataById(id);
            if (monthlyData.isPresent()) {
                return new ResponseEntity<MonthlyData>(monthlyData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<MonthlyData>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @PostMapping(value = "/monthly")
    public ResponseEntity<?> storeMonthlyData() {
        double[] coordinates = {47.10778, 15.42694}; // Prochaskagasse 31

        try {
            MonthlyData monthlyData = weatherAppService.createMonthlyData(coordinates);
            if (monthlyData != null) {
                MonthlyData monthlyDataSaved = weatherAppService.save(monthlyData);
                if (monthlyDataSaved == null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location already stored in DB");
                return new ResponseEntity<>(HttpStatus.OK);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not get response from GEOSPHERE Austria");
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Foo Not Found", e);
        }
    }
}
