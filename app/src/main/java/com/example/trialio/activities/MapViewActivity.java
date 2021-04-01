package com.example.trialio.activities;

/*
Map View markers method from Youtube Video

Video Title: How to Create Multiple Markers on Google Maps in Android

Link to Video: https://youtu.be/kcFjBtEVikE

Video uploader: Gadgets and Technical field Android Tech

Uploader's channel: https://www.youtube.com/channel/UCBXE_skWN_eFn0eat7658rA

 */

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterTrials;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    private Experiment experiment;
    private UserManager userManager;
    private UiSettings uiSettings;
    private GoogleMap trialsMap;
    private ArrayList<Trial> trialList;
    private String experimentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");
        experimentType = experiment.getTrialManager().getType();

        // get the managers
        userManager = new UserManager();
        trialList = new ArrayList<>();

        //get a list of all trials in this experiment
        experiment.getTrialManager().setAllVisibleTrialsFetchListener(new TrialManager.OnAllVisibleTrialsFetchListener() {
            @Override
            public void onAllVisibleTrialsFetch(ArrayList<Trial> newTrialList) {
                trialList.addAll(newTrialList);
                TextView textTotalTrials = findViewById(R.id.numTrials);
                textTotalTrials.setText("Total: " + "\n" + trialList.size());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setFields();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trialsMap);
        mapFragment.getMapAsync(this);
    }

    public void setFields() {

        // get the fields
        TextView textDescription = findViewById(R.id.settings_description);
        TextView textOwner = findViewById(R.id.settings_text_owner);
        TextView textType = findViewById(R.id.settings_text_type);
        TextView textStatus = findViewById(R.id.settings_text_status);
        //TextView textTotalTrials = findViewById(R.id.numTrials);



        // set the fields
        textDescription.setText("Description: " + "\n" + experiment.getSettings().getDescription());
        textType.setText("Type: " + "\n" + experiment.getTrialManager().getType());
        textStatus.setText("Status: " + "\n" + (experiment.getTrialManager().getIsOpen()  ? "OPEN" : "CLOSED"));
        //textTotalTrials.setText("Total: " + "\n" + trialList.size());

        // get the owner's username and set the appropriate fields
        userManager.getUserById(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Owner: " + "\n" + user.getUsername());
            }
        });

    }

    private String getTrialResult(Trial trial) {
        if (ExperimentTypeUtility.isBinomial(experimentType)) {
            return ("Result: " + ((BinomialTrial) trial).getIsSuccess());
        } else if (ExperimentTypeUtility.isMeasurement(experimentType)) {
            return ("Result: " + ((MeasurementTrial) trial).getMeasurement() + " " + ((MeasurementTrial) trial).getUnit());
        } else if (ExperimentTypeUtility.isCount(experimentType)) {
            return ("Result: " + ((CountTrial) trial).getCount());
        } else if (ExperimentTypeUtility.isNonNegative(experimentType)){
            return ("Result: " + ((NonNegativeTrial) trial).getNonNegCount());
        }
        return "null";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        trialsMap = googleMap;

        uiSettings = trialsMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(false);

        if (!trialList.isEmpty()) {
            for (int i=0; i < trialList.size(); i++) {
                trialsMap.addMarker(new MarkerOptions().position(new LatLng(trialList.get(i).getLocation().getLatitude(), trialList.get(i).getLocation().getLongitude())).title(getTrialResult(trialList.get(i))));
            }

            trialsMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(trialList.get(0).getLocation().getLatitude(), trialList.get(0).getLocation().getLongitude())));
        }
    }
}
