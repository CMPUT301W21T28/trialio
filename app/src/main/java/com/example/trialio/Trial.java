package com.example.trialio;

import android.location.Location;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;

public class Trial implements Serializable {
    //protected User experimenter;
    //protected Location location;
    //protected Date date;
    //protected Image qrcode;

    // Changing these variables to type string for testing adding trials -Jeff
    protected String location;
    protected String date;
    protected String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }




}
