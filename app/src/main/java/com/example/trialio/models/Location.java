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
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.concurrent.Executor;

/**
 * Represents a location with a latitude and a longitude
 */
public class Location implements Serializable {
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
     * This function requests the wifi or GPS of the device to return the current location, and
     * double checks permission for the user location
     *
     * @deprecated
     * @param context the context used by location
     */
    public Task<android.location.Location> getCurrentLocation(Context context) {
        //getting location permission from the user
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locClient = LocationServices.getFusedLocationProviderClient(context);
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
            return locationTask;
        }

        return null;
    }

    /**
     * Verifies location permissions are enabled and requests the wifi or GPS of the device to
     * get the current location. If location permissions are enabled, a
     * Task<android.location.Location> is returned that will resolve to the location information.
     * If location permissions are not enabled, NULL is returned
     *
     * @param context the context used by location
     * @return If permissions enabled, a Task<android.location.Location> that will resolve to the
     * location information. If location permissions are not enabled, NULL.
     */
    public static Task<android.location.Location> requestLocation(Context context) {
        // get location permission from the user
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient locClient = LocationServices.getFusedLocationProviderClient(context);
            Task<android.location.Location> locationTask = locClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    Log.d(TAG, "Current location get successful");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Failed to get location " + e.getLocalizedMessage());
                }
            });
            return locationTask;
        } else {
            return null;
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
