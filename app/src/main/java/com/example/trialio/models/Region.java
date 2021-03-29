package com.example.trialio.models;

import java.io.Serializable;

/**
 * Represents a region where an experiment takes place
 */
public class Region implements Serializable {

    private String regionText;
    // private Location geoLocation;
    private double kmRadius;

    /**
     * Gets the description of a region
     *
     * @return the description of a region
     */
    public String getRegionText() {
        return regionText;
    }

    /**
     * Sets the description of a region
     *
     * @param regionText the description of a region to be set
     */
    public void setRegionText(String regionText) {
        this.regionText = regionText;
    }

    /**
     * Gets the region radius in kilometres
     *
     * @return the region radius in kilometres
     */
    public double getKmRadius() {
        return kmRadius;
    }

    /**
     * Sets the region radius in kilometres
     *
     * @param kmRadius the region radius in kilometres to be set
     */
    public void setKmRadius(double kmRadius) {
        this.kmRadius = kmRadius;
    }
}
