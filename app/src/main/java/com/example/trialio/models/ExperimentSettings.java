package com.example.trialio.models;

import java.io.Serializable;

/**
 * Represents the settings of an experiment, more specifically the description, region, owner, and
 * whether or not geo-location is required
 */
public class ExperimentSettings implements Serializable {

    private String description;
    private Region region;
    private String ownerId;
    private boolean geoLocationRequired;

    /**
     * Constructor for an Experiment
     */
    public ExperimentSettings() {
    }

    /**
     * Constructor for an ExperimentSettings
     *
     * @param description         the description of an experiment
     * @param region              the region of an experiment
     * @param ownerId             the owner of an experiment
     * @param geoLocationRequired whether or not geo-location is required for an experiment
     */
    public ExperimentSettings(String description, Region region, String ownerId, boolean geoLocationRequired) {
        this.description = description;
        this.region = region;
        this.ownerId = ownerId;
        this.geoLocationRequired = geoLocationRequired;
    }

    /**
     * Gets the description of an experiment
     *
     * @return the description of an experiment
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of an experiment
     *
     * @param description the description of an experiment to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the region of an experiment
     *
     * @return the region of an experiment
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Sets the region of an experiment
     *
     * @param region the region of an experiment to be set
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    /**
     * Gets the owner of an experiment
     *
     * @return the owner of an experiment
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the owner ID
     *
     * @param ownerId the owner ID to be set
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets the geo-location requirement boolean of an experiment
     *
     * @return the geo-location requirement boolean of an experiment
     */
    public boolean getGeoLocationRequired() {
        return geoLocationRequired;
    }

    /**
     * Sets the geo-location requirement boolean of an experiment
     *
     * @param geoLocationRequired the geo-location requirement boolean of an experiment to be set
     */
    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
    }
}