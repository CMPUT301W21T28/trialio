package com.example.trialio;

import java.io.Serializable;

public class ExperimentSettings implements Serializable {

    private String description;
    private Region region;
    private User owner;
    private boolean geoLocationRequired;

    public ExperimentSettings () { }

    public ExperimentSettings(String description, Region region, User owner, boolean geoLocationRequired) {
        this.description = description;
        this.region = region;
        this.owner = owner;
        this.geoLocationRequired = geoLocationRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean getGeoLocationRequired() {
        return geoLocationRequired;
    }

    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
    }
}
