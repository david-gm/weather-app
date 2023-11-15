package com.dg.weatherapp.api.location.exceptions;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(Long id) {
        super(String.format("Location with id %d not found", id));
    }
}
