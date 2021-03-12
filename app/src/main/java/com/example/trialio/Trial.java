package com.example.trialio;

import android.location.Location;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;

public class Trial implements Serializable {
    //protected User experimenter;
    protected Location location;
    protected Date date;
    //protected Image qrcode;
    protected String data;

    // Changing these variables to type string for testing adding trials -Jeff
    //protected String location;
    //protected String date;
    //protected String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
