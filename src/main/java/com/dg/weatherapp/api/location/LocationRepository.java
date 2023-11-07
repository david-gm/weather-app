package com.dg.weatherapp.api.location;

import com.dg.weatherapp.api.monthly.MonthlyData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByLatitudeAndLongitude(double latitude, double longitude);
}
