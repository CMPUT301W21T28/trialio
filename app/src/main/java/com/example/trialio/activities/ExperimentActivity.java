package com.example.trialio.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trialio.fragments.BinomialTrialFragment;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.example.trialio.fragments.NonNegativeTrialFragment;
import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

public class ExperimentActivity extends AppCompatActivity implements NonNegativeTrialFragment.OnFragmentInteractionListener, BinomialTrialFragment.OnFragmentInteractionListener {
    private Experiment experiment;
    private String trialType;
    private ExperimentManager experimentManager;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        experimentManager = new ExperimentManager();

        // Took ActionBar code.
        // DATE:	2020-12-14
        // LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
        // SOURCE: 	Add an up action [https://developer.android.com/training/appbar/up-action]
        // AUTHOR: 	Android Developers [https://developer.android.com/]
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // initialize all of the fields in the activity
        setFields();

        // store the experiment type to trialType variable
        trialType = experiment.getTrialManager().getType();

        Button addTrial = (Button) findViewById(R.id.btnAddTrial);
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExperimentTypeUtility.isCount(trialType)) {
                    //CountTrialFragment newTrial = new CountTrialFragment();
                    //newTrial.show(getSupportFragmentManager(), "countTrial");
                } else if (ExperimentTypeUtility.isBinomial(trialType)) {
                    BinomialTrialFragment newTrial = new BinomialTrialFragment();
                    newTrial.show(getSupportFragmentManager(), "addBinomial");
                } else if (ExperimentTypeUtility.isNonNegative(trialType)) {
                    NonNegativeTrialFragment newTrial = new NonNegativeTrialFragment();
                    newTrial.show(getSupportFragmentManager(), "nonNegativeTrial");
                } else if (ExperimentTypeUtility.isMeasurement(trialType)) {
                    //MeasurementTrialFragment newTrial = new MeasurementTrialFragment();
                    //newTrial.show(getSupportFragmentManager(), "MeasurementTrial");
                } else {
                    assert (false);
                }
            }
        });

        Button showTrials = (Button) findViewById(R.id.btnTrials);
        showTrials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TrialActivity.class);
                startActivity(intent);
            }
        });



    }

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

    @Override
    public void onOkPressed(Trial newTrial) {
        experiment.getTrialManager().addTrial(newTrial);
        experimentManager.editExperiment(experiment.getExperimentID(), experiment);
    }
}