package com.example.trialio.models;

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

import java.io.Serializable;

public class Location implements Serializable{
    private static final String TAG = "Location";
    private double latitude;
    private double longitude;

    final int REQUEST_CODE_FINE_PERMISSION = 99;
    FusedLocationProviderClient locClient;


    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void getCurrentLocation(Context context, Activity activity) {
        //getting location permission from the user
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permission is not granted so request permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show user a message if user refused to give permission
                new AlertDialog.Builder(activity)
                        .setMessage("Geo-Location may be required for this experiment, please grant location permission")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_PERMISSION);
                            }
                        }).show();
            } else {
                // request permission
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_PERMISSION);
            }
        }
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

    //LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    //lm.requestLocationUpdates(NETWORK_PROVIDER, 10000, 500, listener);
    //location.setLongitude(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
    //location.setLatitude(lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());

}
