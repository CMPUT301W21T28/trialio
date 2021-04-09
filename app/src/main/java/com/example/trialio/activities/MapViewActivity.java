package com.example.trialio.activities;

//map reference from https://developers.google.com/maps/documentation/android-sdk/start

/*
Map View markers method from Youtube Video

Video Title: How to Create Multiple Markers on Google Maps in Android

Link to Video: https://youtu.be/kcFjBtEVikE

Video uploader: Gadgets and Technical field Android Tech

Uploader's channel: https://www.youtube.com/channel/UCBXE_skWN_eFn0eat7658rA

 */

/*
Custom map markers method from Youtube Video

Video Title: How to add Custom Marker in Google maps in Android

Link to Video: https://youtu.be/26bl4r3VtGQ

Video uploader: Gadgets and Technical field Android Tech

Uploader's channel: https://www.youtube.com/channel/UCBXE_skWN_eFn0eat7658rA
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.trialio.R;

import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.controllers.ViewUserProfileCommand;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Region;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.example.trialio.utils.HomeButtonUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = "Map view activity";
    private Context context;
    private Experiment experiment;
    private UserManager userManager;
    private UiSettings uiSettings;
    private GoogleMap trialsMap;
    private Location experimentRegion;
    private ArrayList<Trial> trialList;
    private String experimentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // set the context
        context = this;

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");
        experimentType = experiment.getTrialManager().getType();
        experimentRegion = experiment.getSettings().getRegion().getGeoLocation();

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

        // set on click listeners
        setOnClickListeners();
    }

    /**
     * This sets the on click listeners for the MapViewActivity
     */
    public void setOnClickListeners() {

        // set the click listener to view the owner profile
        TextView textOwner = findViewById(R.id.settings_text_owner);
        textOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create and execute a ViewUserProfileCommand
                ViewUserProfileCommand command = new ViewUserProfileCommand(context, experiment.getSettings().getOwnerID());
                command.execute();
            }
        });

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
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

        // set the fields
        textDescription.setText("Description: " + "\n" + experiment.getSettings().getDescription());
        textType.setText("Type: " + "\n" + experiment.getTrialManager().getType());
        textStatus.setText("Status: " + "\n" + (experiment.getTrialManager().getIsOpen()  ? "OPEN" : "CLOSED"));

        // get the owner's username and set the appropriate fields
        userManager.getUserById(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                if (user != null) {
                    textOwner.setText("Owner: " + "\n" + user.getUsername());
                } else {
                    Log.e(TAG, "Failed to load user");
                }

            }
        });

    }

    private BitmapDescriptor iconFromDrawable(Context context, int imgID) {
        Drawable icon = ContextCompat.getDrawable(context, imgID);
        icon.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
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

        trialsMap.addMarker(new MarkerOptions().position(new LatLng(experimentRegion.getLatitude(), experimentRegion.getLongitude()))
                .title("Region: " + experiment.getSettings().getRegion().getRegionText()).icon(iconFromDrawable(context, R.drawable.conical_flask_empty)));

        if (!trialList.isEmpty()) {
            for (int i=0; i < trialList.size(); i++) {
                trialsMap.addMarker(new MarkerOptions().position(new LatLng(trialList.get(i).getLocation().getLatitude(), trialList.get(i).getLocation().getLongitude()))
                        .title(getTrialResult(trialList.get(i))));
            }
            trialsMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(trialList.get(0).getLocation().getLatitude(), trialList.get(0).getLocation().getLongitude())));
        }
    }
}
