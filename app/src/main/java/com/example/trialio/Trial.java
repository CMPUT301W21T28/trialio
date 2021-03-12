package com.example.trialio;

import java.io.Serializable;
import java.util.Date;

public class Trial implements Serializable {
    protected String experimenterID;
    protected com.example.trialio.Location location;
    protected Date date;

    public Trial() { }

    public Trial(String experimenterID, com.example.trialio.Location location, Date date) {
        this.experimenterID = experimenterID;
        this.location = location;
        this.date = date;
    }

    public String getExperimenterID() {
        return experimenterID;
    }

    public void setExperimenterID(String experimenterID) {
        this.experimenterID = experimenterID;
    }

    public com.example.trialio.Location getLocation() {
        return location;
    }

    public void setLocation(com.example.trialio.Location location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
