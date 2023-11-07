package com.dg.weatherapp.api.monthly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyRepository extends JpaRepository<Monthly, Long> {

}
