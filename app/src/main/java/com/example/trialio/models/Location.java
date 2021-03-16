package com.example.trialio.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.Serializable;

public class Location implements Serializable, LocationListener{
    private double latitude;
    private double longitude;
    protected boolean wifiOn = false;
    private LocationManager locationManager;


    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void findCurrentLocation (Context context, Activity parentActivity) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(parentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
        //try{wifiOn=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
        //if(wifiOn) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, this);
        //}
        //else {
            //return;
        //}
        setLatitude(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());
        setLongitude(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
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
