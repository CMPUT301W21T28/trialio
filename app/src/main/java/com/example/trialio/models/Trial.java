package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

public class Trial implements Serializable {
    protected String experimenterID;
    protected Location location;
    protected Date date;
    protected String data;

    public Trial() { }

    public Trial(String experimenterID, Location location, Date date) {
        this.experimenterID = experimenterID;
        this.location = location;
        this.date = date;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }

    public String getExperimenterID() {
        return experimenterID;
    }

    public void setExperimenterID(String experimenterID) {
        this.experimenterID = experimenterID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
