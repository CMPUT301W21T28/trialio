package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a trial of experiment type count
 */
public class CountTrial extends Trial implements Serializable {
    private int count;

    /**
     * Constructor for a CountTrial
     *
     * @param experimenterID the unique identifier of an experimenter
     * @param location       the location of a count trial
     * @param date           the date of a count trial
     */
    public CountTrial(String experimenterID, Location location, Date date) {
        super(experimenterID, location, date);
        this.count = 1;
    }

    /**
     * Gets the count of a count trial
     *
     * @return the count of a count trial
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count of a count trial
     *
     * @param count the count of a count trial to be set
     */
    public void setCount(int count) {
        this.count = count;
    }
}
