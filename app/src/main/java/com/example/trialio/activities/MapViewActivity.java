package com.example.trialio.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterTrials;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapViewActivity extends AppCompatActivity {
    private Experiment experiment;
    private UserManager userManager;
    ArrayList<Trial> trialList = new ArrayList<Trial>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // get the managers
        userManager = new UserManager();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trialsMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFields();
    }

    public void setFields() {

        // get the fields
        TextView textDescription = findViewById(R.id.settings_description);
        TextView textOwner = findViewById(R.id.settings_text_owner);
        TextView textType = findViewById(R.id.settings_text_type);
        TextView textStatus = findViewById(R.id.settings_text_status);
        TextView textTotalTrials = findViewById(R.id.numTrials);

        // set the fields
         textDescription.setText("Description: " + "\n" + experiment.getSettings().getDescription());
        textType.setText("Type: " + "\n" + experiment.getTrialManager().getType());
        textStatus.setText("Status: " + "\n" + (experiment.getTrialManager().getIsOpen()  ? "OPEN" : "CLOSED"));
        //textTotalTrials.setText("Total: " + experiment.getTrialManager().getTrials().size());
        textOwner.setText("Owner: " + "\n" + "owner");

        /*
        // get the owner's username
        userManager.getUser(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Owner: " + user.getUsername());
            }
        });
            */
    }

}
