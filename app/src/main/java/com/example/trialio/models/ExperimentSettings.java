package com.example.trialio.models;

import java.io.Serializable;

public class ExperimentSettings implements Serializable {

    private String description;
    private Region region;
    private String ownerID;
    private boolean geoLocationRequired;

    public ExperimentSettings () { }

    public ExperimentSettings(String description, Region region, String ownerID, boolean geoLocationRequired) {
        this.description = description;
        this.region = region;
        this.ownerID = ownerID;
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

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public boolean getGeoLocationRequired() {
        return geoLocationRequired;
    }

    public void setGeoLocationRequired(boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
    }
}