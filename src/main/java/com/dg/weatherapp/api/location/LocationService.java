package com.dg.weatherapp.api.location;

import com.dg.weatherapp.api.location.exceptions.LocationDuplicateException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getLocations(){
        return locationRepository.findAll();
    }
    public Location saveNewLocation(Location newLocation) {
        List<Location> locationsFound = locationRepository.findByLatitudeAndLongitude(newLocation.getLatitude(), newLocation.getLongitude());
        if(locationsFound.isEmpty())
            return locationRepository.save(newLocation);
        else
            throw new LocationDuplicateException(newLocation);
    }
}
