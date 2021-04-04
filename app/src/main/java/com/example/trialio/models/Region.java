package com.example.trialio.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a region where an experiment takes place
 */
public class Region implements Serializable {

    private String regionText;
    private Location geoLocation;
    private Double kmRadius;

    public Region() {
        this.regionText = null;
        this.geoLocation = new Location();
        this.kmRadius = null;
    }

    public Region(String text) {
        this.regionText = text;
        this.geoLocation = new Location();
        this.kmRadius = null;
    }

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
    public Double getKmRadius() {
        return kmRadius;
    }

    /**
     * Sets the region radius in kilometres
     *
     * @param kmRadius the region radius in kilometres to be set
     */
    public void setKmRadius(Double kmRadius) {
        this.kmRadius = kmRadius;
    }

    /**
     * Gets the geo location of the experiment.
     *
     * @return The geo location of the experiment.
     */
    public Location getGeoLocation() {
        return geoLocation;
    }

    /**
     * Sets the geo location of the experiment.
     *
     * @param geoLocation The location to set as the geo location of the experiment.
     */
    public void setGeoLocation(Location geoLocation) {
        this.geoLocation = geoLocation;
    }
}
