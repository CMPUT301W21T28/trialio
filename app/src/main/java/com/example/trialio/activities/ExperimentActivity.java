package com.example.trialio.activities;

/*
Location permissions method from youtube video

Video Title: Runtime Permissions Android | Required from API 23 and above

Link to Video: https://www.youtube.com/watch?v=WZhEroL4P7s

Video uploader: yoursTRULY

Uploader's channel: https://www.youtube.com/channel/UCr0y1P0-zH2o3cFJyBSfAKg

 */

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ExperimentActivity extends AppCompatActivity implements  NonNegativeTrialFragment.OnFragmentInteractionListener, BinomialTrialFragment.OnFragmentInteractionListener, CountTrialFragment.OnFragmentInteractionListener, MeasurementTrialFragment.OnFragmentInteractionListener {
    private final String TAG = "ExperimentActivity";
    private Experiment experiment;
    private String trialType;
    private ExperimentManager experimentManager;
    private final Context context = this;
    final int REQUEST_CODE_FINE_PERMISSION = 99;
    private ImageButton experimentSettings;
    private UserManager userManager;
    private Button showTrials;
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

        Button locPerm = findViewById(R.id.locationPerm);
        if (!experiment.getSettings().getGeoLocationRequired()) {
            locPerm.setVisibility(View.GONE);
        }
        locPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermissions();
            }
        });
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locPerm.setVisibility(View.GONE);
        }

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
     * This gets permission from the user to share their location
     */
    public void getLocationPermissions() {
        //getting location permission from the user
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permission is not granted so request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(ExperimentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show user a message if user refused to give permission
                new AlertDialog.Builder(ExperimentActivity.this)
                        .setMessage("To ensure the accuracy of submitted trials for this experiment, please grant location permission")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(ExperimentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_PERMISSION);
                            }
                        }).show();
            } else {
                // request permission
                ActivityCompat.requestPermissions(ExperimentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_PERMISSION);
            }
        }
    }

    /**
     * This is to take action if the user has denied location permission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FINE_PERMISSION) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //user has granted permission
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ExperimentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //The user has chosen to permanently deny location permissions, so we request them to go to app settings and enable it from there
                    new AlertDialog.Builder(ExperimentActivity.this)
                            .setMessage("Location permissions have been permanently denied, please go to app settings and enable location permissions")
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", ExperimentActivity.this.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show();
                }
            }
        }
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
        textOwner.setText("Owner: " + experiment.getSettings().getOwner().getUsername());
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
        //experimentSettings.setVisibility(View.INVISIBLE);

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