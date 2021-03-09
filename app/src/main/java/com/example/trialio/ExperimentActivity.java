package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExperimentActivity extends AppCompatActivity {
    private Experiment experiment;
    private String trialType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // initialize all of the fields in the activity
        setFields();

        // store the experiment type to trialType variable
        trialType = experiment.getTrialManager().getType();

        // show fragments that match the experiment type
        if (trialType == "Binomial"){
            ImageButton addBinomial = (ImageButton) findViewById(R.id.btnAddTrial);
            addBinomial.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    addBinomialTrial();
                }
            });
        //}else if (trialType == "Measurement"){
        //    ImageButton addMeasurement = (ImageButton) findViewById(R.id.btnAddTrial);
        //    addMeasurement.setOnClickListener(new View.OnClickListener(){
        //        @Override
        //        public void onClick(View v) {
        //            addMeasurementTrial();
        //        }
        //    });
        //}else if (trialType == "NonNegativeCount"){
        //    ImageButton addNonNegativeCount = (ImageButton) findViewById(R.id.btnAddTrial);
        //    addNonNegativeCount.setOnClickListener(new View.OnClickListener(){
        //        @Override
        //        public void onClick(View v) {
        //            addNonNegativeCountTrial();
        //        }
        //    });
        //}else if (trialType == "Count"){
        //    ImageButton addCount = (ImageButton) findViewById(R.id.btnAddTrial);
        //    addCount.setOnClickListener(new View.OnClickListener(){
        //        @Override
        //        public void onClick(View v) {
        //            addCountTrial();
        //        }
        //    });
        //};


        //Button showTrials = (Button) findViewById(R.id.btnTrials);
        //showTrials.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        showTrials();
        //    }
        //});
    }


    /**
     * addBinomialTrial
     */
    public void addBinomialTrial (){
        BionomialTrial newTrial = new BionomialTrial();
        newTrial.show(getSupportFragmentManager(), "addBinomial");
    }


    /**
     * This leads to Trials Class
     */
    //public void showTrials(){
    //  Intent intent = new Intent ()
    //}

    /**
     * This initializes all of the fields of the activity with data from the experiment
     */
    public void setFields(){
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