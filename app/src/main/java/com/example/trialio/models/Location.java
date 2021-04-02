package com.example.trialio.models;
/*
Location permissions method from youtube video

Video Title: Location | Getting Current location | FusedLocationProviderClient

Link to Video: https://www.youtube.com/watch?v=rNYaEFl6Fms

Video uploader: yoursTRULY

Uploader's channel: https://www.youtube.com/channel/UCr0y1P0-zH2o3cFJyBSfAKg

 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trialio.R;
import com.example.trialio.activities.ExperimentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.type.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a location with a latitude and a longitude
 */
public class Location implements Serializable{
    private static final String TAG = "Location";
    private double latitude;
    private double longitude;

    final int REQUEST_CODE_FINE_PERMISSION = 99;
    transient FusedLocationProviderClient locClient;

    /**
     * Constructor for a Location
     */
    public Location() {

    }

    /**
     * Constructor for a Location
     *
     * @param latitude  the latitude of a location
     * @param longitude the longitude of a location
     */
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
        This function requests the wifi or gps of the device to return the current location, and
        double checks permission for the user location
     */

    /**
     * This function requests the wifi or GPS of the device to return the current location, and
     *         double checks permission for the user location
     *
     * @param context  the context used by location
     * @param activity the activity used by location
     */
    public void getCurrentLocation(Context context, Activity activity) {
        //getting location permission from the user
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locClient = LocationServices.getFusedLocationProviderClient(activity);
            Task<android.location.Location> locationTask = locClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if (location != null) {
                        setLatitude(location.getLatitude());
                        setLongitude(location.getLongitude());
                    } else {
                        Log.d(TAG, "Location was NULL");
                    }
                }
            });
            locationTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "OnFailure" + e.getLocalizedMessage());
                }
            });
        }

    }

    /**
     * Gets the latitude of a location
     *
     * @return the latitude of a location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of a location
     *
     * @param latitude the latitude of a location to be set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of a location
     *
     * @return the longitude of a location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of a location
     *
     * @param longitude the longitude of a location to be set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
