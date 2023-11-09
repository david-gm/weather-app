package com.dg.weatherapp.api.location.exceptions;

import com.dg.weatherapp.api.location.Location;

public class LocationDuplicateException extends RuntimeException {

    public LocationDuplicateException(Location locationDuplicate) {
        super(String.format("Location lat: %f, lon: %f already existing",
                locationDuplicate.getLatitude(),
                locationDuplicate.getLongitude()));
    }
}
