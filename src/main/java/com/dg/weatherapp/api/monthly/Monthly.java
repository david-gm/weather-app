package com.dg.weatherapp.api.monthly;

import com.dg.weatherapp.api.location.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Monthly {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<MonthlyData> monthlyData;

    @OneToOne
    private Location location;
}
