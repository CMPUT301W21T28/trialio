package com.example.trialio.models;

import java.io.Serializable;

/**
 * Represents a region where an experiment takes place
 */
public class Region implements Serializable {

    private String description;
    // private Location geoLocation;
    private Double kmRadius;

    /**
     * Gets the description of a region
     *
     * @return the description of a region
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of a region
     *
     * @param description the description of a region to be set
     */
    public void setDescription(String description) {
        this.description = description;
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
}
