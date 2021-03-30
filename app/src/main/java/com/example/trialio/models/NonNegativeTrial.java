package com.example.trialio.models;

import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a trial of experiment type non-negative count
 */
public class NonNegativeTrial extends Trial implements Serializable {
    private int nonNegCount;

    public NonNegativeTrial() { }

    /**
     * Constructor for a NonNegativeTrial
     *
     * @param experimenterID the unique identifier of an experimenter
     * @param location       the location of a non-negative count trial
     * @param date           the date of a non-negative count trial
     * @param nonNegCount    the count of a non-negative count trial
     */
    public NonNegativeTrial(String experimenterID, Location location, Date date, int nonNegCount) {
        super(experimenterID, location, date);
        this.nonNegCount = nonNegCount;
    }

    /**
     * Gets the count of a non-negative count trial
     *
     * @return the count of a non-negative count trial
     */
    public int getNonNegCount() {
        return nonNegCount;
    }

    /**
     * Sets the count of a non-negative count trial
     *
     * @param nonNegCount the count of a non-negative count trial to be set
     */
    public void setNonNegCount(int nonNegCount) {
        this.nonNegCount = nonNegCount;
    }
}
