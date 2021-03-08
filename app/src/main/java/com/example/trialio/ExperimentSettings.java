package com.example.trialio;

public class ExperimentSettings {

    private String description;
    private Region region;
    private User owner;
    private Boolean geoLocationRequired;

    public ExperimentSettings () { }

    public ExperimentSettings(String description, Region region, User owner, Boolean geoLocationRequired) {
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

    public Boolean getGeoLocationRequired() {
        return geoLocationRequired;
    }

    public void setGeoLocationRequired(Boolean geoLocationRequired) {
        this.geoLocationRequired = geoLocationRequired;
    }
}
