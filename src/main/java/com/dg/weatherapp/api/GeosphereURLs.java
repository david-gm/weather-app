package com.dg.weatherapp.api;

public class GeosphereURLs {
    private static final String URL_GEOSPHERE = "https://dataset.api.hub.geosphere.at/v1/timeseries/historical/";

    public static final String URL_DAILY_1KM = String.format("%sspartacus-v2-1d-1km/metadata", URL_GEOSPHERE);
    public static final String URL_MONTHLY_1KM = String.format("%sspartacus-v1-1m-1km", URL_GEOSPHERE);
}
