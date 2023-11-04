package com.dg.weatherapp.api.monthly;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MonthlyData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double latitude;
    private Double longitude;

    private ArrayList<Double> temperature; // Degree Celsius
    private ArrayList<Double> precipitation; // kg m-2

    private ArrayList<LocalDateTime> datetime;
}
