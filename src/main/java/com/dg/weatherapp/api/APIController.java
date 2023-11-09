package com.dg.weatherapp.api;

import com.dg.weatherapp.api.location.LocationService;
import com.dg.weatherapp.api.monthly.MonthlyDataTransferObject;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import com.dg.weatherapp.api.location.Location;
import com.dg.weatherapp.api.monthly.Monthly;

@RestController()
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class APIController {

    private static final Logger LOG = LoggerFactory.getLogger(APIController.class);
    private static final String BASE_URL = "https://dataset.api.hub.geosphere.at/v1/";
    private final WeatherAppService weatherAppService;
    private final LocationService locationService;

    APIController(WeatherAppService weatherAppService, LocationService locationService) {
        this.weatherAppService = weatherAppService;
        this.locationService = locationService;
    }

    @PostConstruct
    private void init() {
        LOG.debug(String.format("This is a test message from the %s", APIController.class));
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getLocations() {
        return new ResponseEntity<List<Location>>(locationService.getLocations(), HttpStatus.OK);
    }

    @PostMapping("/location")
    Location createLocation(@RequestBody Location newLocation) {
        return locationService.saveNewLocation(newLocation);
    }

    @GetMapping(value = "/monthly/{id}")
    public ResponseEntity<MonthlyDataTransferObject> getMonthlyData(@PathVariable Long id) {
        try {
            Optional<Monthly> monthlyData = weatherAppService.getMonthlyById(id);
            if (monthlyData.isPresent()) {
                MonthlyDataTransferObject mdto = weatherAppService.mapMonthly(monthlyData.get());
                return new ResponseEntity<MonthlyDataTransferObject>(mdto, HttpStatus.OK);
            } else {
                return new ResponseEntity<MonthlyDataTransferObject>(HttpStatus.NOT_FOUND);
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
            Monthly monthly = weatherAppService.createMonthlyData(coordinates);
            if (monthly != null) {
                Monthly monthlySaved = weatherAppService.save(monthly);
                if (monthlySaved == null)
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
