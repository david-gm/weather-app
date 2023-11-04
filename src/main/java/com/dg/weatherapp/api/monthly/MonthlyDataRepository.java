package com.dg.weatherapp.api.monthly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyDataRepository extends JpaRepository<MonthlyData, Long> {
    List<MonthlyData> findByLatitudeAndLongitude(double latitude, double longitude);
}
