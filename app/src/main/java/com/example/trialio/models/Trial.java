package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a general trial, experiment type not specified
 */
public abstract class Trial implements Serializable {
    protected String experimenterId;
    protected Location location;
    protected Date date;

    /**
     * Constructor for a Trial
     */
    public Trial() { }

    /**
     * Constructor for a Trial
     *
     * @param experimenterId the unique identifier of an experimenter
     * @param location       the location of a trial
     * @param date           the date of a trial
     */
    public Trial(String experimenterId, Location location, Date date) {
        this.experimenterId = experimenterId;
        this.location = location;
        this.date = date;
    }

    /**
     * Gets the experimenter Id
     *
     * @return the experimenter id
     */
    public String getExperimenterId() {
        return experimenterId;
    }

    /**
     * Sets the experimenter Id
     *
     * @param experimenterId the experimenter id to be set
     */
    public void setExperimenterId(String experimenterId) {
        this.experimenterId = experimenterId;
    }

    /**
     * Gets the location of a trial
     *
     * @return the location of a trial
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of a trial
     *
     * @param location the location of a trial to be set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the date of a trial
     *
     * @return the date of a trial
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of a trial
     *
     * @param date the date of a trial to be set
     */
    public void setDate(Date date) {
        this.date = date;
    }

}
