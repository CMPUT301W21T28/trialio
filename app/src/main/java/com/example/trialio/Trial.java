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
