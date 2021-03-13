package com.example.trialio.models;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Location implements LocationListener, Serializable {
    private double latitude;
    private double longitude;
    protected boolean gpsOn,wifiOn;
    private LocationManager locationManager;


    public Location() { }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    //    public ArrayList<double> getCoord() { }

}
