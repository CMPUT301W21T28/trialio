package com.example.trialio.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.Serializable;

public class Location implements Serializable{
    private double latitude;
    private double longitude;
    private Context context;
    private Object parentActivity;
    protected boolean gpsOn, wifiOn;
    private LocationManager locationManager;


    public Location() {
        //getLocation();
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void getLocation () {
        //locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions((Activity) parentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        //}
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, this);
        //latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
        //longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
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
    
    //    public ArrayList<double> getCoord() { }

}
