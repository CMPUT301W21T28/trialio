package com.example.trialio;

import java.io.Serializable;
import java.util.Date;

public class MeasurementTrial extends Trial implements Serializable {
    private Double measurement;
    private String unit;

    public MeasurementTrial(String experimenterID, com.example.trialio.Location location, Date date, Double measurement, String unit) {
        super(experimenterID, location, date);
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
