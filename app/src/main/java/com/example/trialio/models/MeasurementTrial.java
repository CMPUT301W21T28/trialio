package com.example.trialio.models;

import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;

import java.io.Serializable;
import java.util.Date;

public class MeasurementTrial extends Trial implements Serializable {
    private Double measurement;
    private String unit;

    public MeasurementTrial(String experimenterID, Location location, Date date, Double measurement, String unit, String data) {
        super(experimenterID, location, date, data);
        this.measurement = measurement;
        this.unit = unit;
    }

    public Double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Double measurement) {
        this.measurement = measurement;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
