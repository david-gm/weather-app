package com.dg.weatherapp.api.location.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class LocationDuplicateAdvice {

    @ResponseBody
    @ExceptionHandler(LocationDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String locationDuplicateHandler(LocationDuplicateException ex) {
        return ex.getMessage();
    }
}
