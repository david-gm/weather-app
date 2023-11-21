package com.dg.weatherapp.api.monthly;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DataDescriptorMonths {
    private List<Integer> months;
    private List<Double> data;

    DataDescriptorMonths() {
        months = new ArrayList<>();
        data = new ArrayList<>();
    }
}
