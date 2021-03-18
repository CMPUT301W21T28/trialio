package com.example.trialio.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.BinomialTrialFragment;
import com.example.trialio.fragments.CountTrialFragment;
import com.example.trialio.fragments.MeasurementTrialFragment;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.example.trialio.fragments.NonNegativeTrialFragment;
import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

public class ExperimentActivity extends AppCompatActivity implements  NonNegativeTrialFragment.OnFragmentInteractionListener, BinomialTrialFragment.OnFragmentInteractionListener, CountTrialFragment.OnFragmentInteractionListener, MeasurementTrialFragment.OnFragmentInteractionListener {
    private final String TAG = "ExperimentActivity";
    private final Context context = this;

    private ExperimentManager experimentManager;
    private ImageButton experimentSettings;
    private UserManager userManager;
    private Experiment experiment;
    private Button showTrials;
    private String trialType;
    private Button addTrial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        // Took ActionBar code.
        // DATE:	2020-12-14
        // LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
        // SOURCE: 	Add an up action [https://developer.android.com/training/appbar/up-action]
        // AUTHOR: 	Android Developers [https://developer.android.com/]
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the experiment that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // create managers important to this activity
        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // get the important views in this activity
        experimentSettings = (ImageButton) findViewById(R.id.button_experiment_settings);
        showTrials = (Button) findViewById(R.id.btnTrials);
        addTrial = (Button) findViewById(R.id.btnAddTrial);

        // set the visibility of certain views in this activity
        setViewVisibility();

        // initialize all of the fields in the activity
        setFields();

        // set the onclick listeners for this activity
        setOnClickListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // update the experiment
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {
                experiment = new_experiment;
                setFields();
            }
        });
    }

    /**
     * This initializes all of the fields of the activity with data from the experiment
     */
    public void setFields(){
        // get TextViews
        TextView textDescription = findViewById(R.id.txtExperimentDesciption);
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

    /**
     * This is called when the user presses confirm on one of the Trial creation fragments
     * @param newTrial The new trial that was created in the fragment
     */
    @Override
    public void onOkPressed(Trial newTrial) {
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment updated_experiment) {
                experiment = updated_experiment;
                experiment.getTrialManager().addTrial(newTrial);
                experimentManager.editExperiment(experiment.getExperimentID(), experiment);
            }
        });
    }

    /**
     * This sets the on click listeners for an Experiment Activity
     */
    public void setOnClickListeners() {
        // get experiment type
        trialType = experiment.getTrialManager().getType();

        // set listener for addTrial button
        addTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExperimentTypeUtility.isCount(trialType)) {
                    CountTrialFragment newTrial = new CountTrialFragment();
                    newTrial.show(getSupportFragmentManager(), "addCountTrial");
                } else if (ExperimentTypeUtility.isBinomial(trialType)) {
                    BinomialTrialFragment newTrial = new BinomialTrialFragment();
                    newTrial.show(getSupportFragmentManager(), "addBinomial");
                } else if (ExperimentTypeUtility.isNonNegative(trialType)) {
                    NonNegativeTrialFragment newTrial = new NonNegativeTrialFragment();
                    newTrial.show(getSupportFragmentManager(), "addConNegativeTrial");
                } else if (ExperimentTypeUtility.isMeasurement(trialType)) {
                    MeasurementTrialFragment newTrial = new MeasurementTrialFragment();
                    newTrial.show(getSupportFragmentManager(), "addMeasurementTrial");
                } else {
                    assert (false);
                }
            }
        });

        // Called when the user clicks item in experiment list
        showTrials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TrialActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment_trial", experiment);
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });
    }

    /**
     * This sets the visibility of particular views in the Experiment Activity
     */
    public void setViewVisibility() {
        // set the experiment settings button to invisible by default
        experimentSettings.setVisibility(View.INVISIBLE);

        // if the current user is the owner, set the experiment settings button as visible.
        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                Log.d(TAG, "currentUser: " + user.getId());
                Log.d(TAG, "owner: " + experiment.getSettings().getOwner().getId());
                if (user.getId() == experiment.getSettings().getOwner().getId()) {
                    experimentSettings.setVisibility(View.VISIBLE);
                }
            }
        });

        // set the addTrial button to invisible by default
        addTrial.setVisibility(View.INVISIBLE);

        // if the experiment is open, set the addTrial button as visible
        if (experiment.getTrialManager().getIsOpen()) {
            addTrial.setVisibility(View.VISIBLE);
        }
    }
}