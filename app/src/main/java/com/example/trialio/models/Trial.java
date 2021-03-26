package com.example.trialio.models;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a general trial, experiment type not specified
 */
public class Trial implements Serializable {
    protected String experimenterUsername;
    protected Location location;
    protected Date date;

    /**
     * Constructor for a Trial
     */
    public Trial() { }

    /**
     * Constructor for a Trial
     *
     * @param experimenterUsername the unique identifier of an experimenter
     * @param location       the location of a trial
     * @param date           the date of a trial
     */
    public Trial(String experimenterUsername, Location location, Date date) {
        this.experimenterUsername = experimenterUsername;
        this.location = location;
        this.date = date;
    }

    /**
     * Gets the experimenter Id
     *
     * @return the experimenter id
     */
    public String getExperimenterUsername() {
        return experimenterUsername;
    }

    /**
     * Sets the experimenter Id
     *
     * @param experimenterUsername the experimenter id to be set
     */
    public void setExperimenterUsername(String experimenterUsername) {
        this.experimenterUsername = experimenterUsername;
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
