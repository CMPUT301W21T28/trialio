package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ExperimentActivity extends AppCompatActivity {
    private Experiment experiment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // initialize all of the fields in the activity
        setFields();
    }

    /**
     * This initializes all of the fields of the activity with data from the experiment
     */
    public void setFields() {
        // get TextViews
        TextView textDescription = findViewById(R.id.txtExperimentDescription);
        TextView textType = findViewById(R.id.txtExperimentType);
        TextView textRegion = findViewById(R.id.txtExperimentRegion);
        TextView textOwner = findViewById(R.id.txtExperimentOwner);
        TextView textStatus = findViewById(R.id.txtExperimentStatus);
        TextView textMinTrials = findViewById(R.id.txtExperimentMinTrials);

        // set TextViews
        textDescription.setText("Description: " + experiment.getSettings().getDescription());
        textType.setText("Type: " + experiment.getTrialManager().getType());
        textRegion.setText("Region: " + experiment.getSettings().getRegion().getDescription());
        textOwner.setText("Owner: " + experiment.getSettings().getOwner());
        textStatus.setText("Open: " + (experiment.getTrialManager().getIsOpen() ? "yes" : "no"));
        textMinTrials.setText("Minimum number of trials: " + experiment.getTrialManager().getMinNumOfTrials());
    }
}