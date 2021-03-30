package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a trial of experiment type measurement
 */
public class MeasurementTrial extends Trial implements Serializable {
    private double measurement;
    private String unit;

    public MeasurementTrial() { }

    /**
     * Constructor for a MeasurementTrial
     *
     * @param experimenterID the unique identifier of an experimenter
     * @param location       the location of a measurement trial
     * @param date           the date of a measurement trial
     * @param measurement    the measurement of a measurement trial
     * @param unit           the measurement unit used for the measurement trial
     */
    public MeasurementTrial(String experimenterID, Location location, Date date, double measurement, String unit) {
        super(experimenterID, location, date);
        this.measurement = measurement;
        this.unit = unit;
    }

    /**
     * Gets the measurement of a measurement trial
     *
     * @return the measurement of a measurement trial
     */
    public double getMeasurement() {
        return measurement;
    }

    /**
     * Sets the measurement of a measurement trial
     *
     * @param measurement the measurement of a measurement trial to be set
     */
    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }

    /**
     * Gets the measurement unit used for the measurement trial
     *
     * @return the measurement unit used for the measurement trial
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the measurement unit used for the measurement trial
     *
     * @param unit the measurement unit used for the measurement trial to be set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
