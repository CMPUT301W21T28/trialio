package com.example.trialio.models;

import java.io.Serializable;

public class Region implements Serializable {

    private String description;
    // private Location geoLocation;
    private Double kmRadius;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getKmRadius() {
        return kmRadius;
    }

    public void setKmRadius(Double kmRadius) {
        this.kmRadius = kmRadius;
    }
}
