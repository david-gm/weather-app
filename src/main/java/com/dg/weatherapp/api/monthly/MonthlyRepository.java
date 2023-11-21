package com.dg.weatherapp.api.monthly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyRepository extends JpaRepository<Monthly, Long> {

    Optional<Monthly> findByLocationId(Long locationId);
}
