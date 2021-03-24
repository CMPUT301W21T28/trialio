package com.example.trialio.models;

import android.media.Image;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a general trial, experiment type not specified
 */
public class Trial implements Serializable {
    protected String experimenterID;
    protected Location location;
    protected Date date;
    protected Image qrCode;

    /**
     * Constructor for a Trial
     */
    public Trial() { }

    /**
     * Constructor for a Trial
     *
     * @param experimenterID the unique identifier of an experimenter
     * @param location       the location of a trial
     * @param date           the date of a trial
     */
    public Trial(String experimenterID, Location location, Date date) {
        this.experimenterID = experimenterID;
        this.location = location;
        this.date = date;
    }

    /**
     * Gets the experimenter Id
     *
     * @return the experimenter id
     */
    public String getExperimenterID() {
        return experimenterID;
    }

    /**
     * Sets the experimenter Id
     *
     * @param experimenterID the experimenter id to be set
     */
    public void setExperimenterID(String experimenterID) {
        this.experimenterID = experimenterID;
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

    /**
     * Gets the QR code for the trial
     * @return
     */
    public Image getQrCode() {
        return qrCode;
    }

    /**
     * sets the qr code for the trial
     * @param qrCode the qr code of a trial to be set
     */
    public void setQrCode(Image qrCode) {
        this.qrCode = qrCode;
    }
}
