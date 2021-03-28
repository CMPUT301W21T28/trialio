package com.example.trialio.activities;

/*
Location permissions method from youtube video
Video Title: Runtime Permissions Android | Required from API 23 and above
Link to Video: https://www.youtube.com/watch?v=WZhEroL4P7s
Video uploader: yoursTRULY
Uploader's channel: https://www.youtube.com/channel/UCr0y1P0-zH2o3cFJyBSfAKg
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.trialio.utils.StatisticsUtility;

import java.util.ArrayList;

/**
 * This activity opens an experiment when clicked from the main activity, and displays information about it
 */

public class ExperimentActivity extends AppCompatActivity implements NonNegativeTrialFragment.OnFragmentInteractionListener, BinomialTrialFragment.OnFragmentInteractionListener, CountTrialFragment.OnFragmentInteractionListener, MeasurementTrialFragment.OnFragmentInteractionListener {
    private final String TAG = "ExperimentActivity";
    private Experiment experiment;
    private String trialType;
    private ExperimentManager experimentManager;
    private final Context context = this;
    final int REQUEST_CODE_FINE_PERMISSION = 99;
    private ImageButton settingsButton;
    private UserManager userManager;

    private Button showTrials;
    private ImageButton addTrial;
    private ImageButton scanQR;
    private Button showQR;
    private StatisticsUtility statisticsUtility;
    private User currentUser;

    /**
     * the On create the takes in the saved instance from the main activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);


        // get the experiment that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");
        currentUser = (User) bundle.getSerializable("user_exp");

        // create managers important to this activity
        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // create statistics utility
        statisticsUtility = new StatisticsUtility();

        // get the important views in this activity
        settingsButton = (ImageButton) findViewById(R.id.editUserBtn);
        showTrials = (Button) findViewById(R.id.btnTrials);
        addTrial = (ImageButton) findViewById(R.id.btnAddTrial);
        showQR = (Button) findViewById(R.id.btnQRCode);
        scanQR = (ImageButton) findViewById(R.id.btnCamera);
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

                // set the visibility of certain views in this activity
                setViewVisibility();

                // initialize all of the fields in the activity
                setFields();

                // set the onclick listeners for this activity
                setOnClickListeners();
            }
        });
    }

    /**
     * This method gets permission from the user to share their location
     */
    public void getLocationPermissions() {
        //getting location permission from the user
        /**
         * this if-else loop checks if the user has already been asked for permission once before, and displays an appropriate explanation
         * describing why location permissions are needed. If the user hasn't been asked for permission before, it simply asks for permission
         */
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
     * This method takes action after the user has responded to the dialog that asks for location permission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FINE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //user has granted permission
            } else {
                /**
                 * This section of the if-else loop is to take action if the user had been asked permission before
                 * and chose the "deny" and "do not ask again" options
                 */
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
    public void setFields() {
        // get TextViews

        // TODO: maybe make some adjustments here
        TextView textDescription = findViewById(R.id.experiment_description);
        TextView textType = findViewById(R.id.experiment_text_type);
        TextView textRegion = findViewById(R.id.experiment_region);
        TextView textOwner = findViewById(R.id.experiment_text_owner);
        TextView textStatus = findViewById(R.id.experiment_text_status);
        TextView textMinTrials = findViewById(R.id.experiment_min_num);
        TextView textStats = findViewById(R.id.txtStatsSummary);
        ImageView experimentLocationImageView = findViewById(R.id.experiment_location);


        Button subBtn = findViewById(R.id.btnSubscribe);

        // set TextViews
        textDescription.setText("Description: " + experiment.getSettings().getDescription());
        textType.setText("Type: " + experiment.getTrialManager().getType());
        textRegion.setText("Region: " + experiment.getSettings().getRegion().getDescription());

        userManager.getUserById(experiment.getSettings().getOwnerId(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText(user.getUsername());

            }
        });

        if ( experiment.getTrialManager().getIsOpen() ) {
            textStatus.setText("Open");
        } else {
            textStatus.setText("Closed");
        }

        if (!experiment.getSettings().getGeoLocationRequired()) {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_off_24);
        } else {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_on_24);
        }

        textMinTrials.setText("Min # Trials: " + experiment.getTrialManager().getMinNumOfTrials());

        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                if (user.isSubscribed(experiment)) {
                    subBtn.setText(R.string.experiment_action_unsubscribe);
                } else {
                    subBtn.setText(R.string.experiment_action_subscribe);
                }
            }
        });

        // set Stats Summary
        ArrayList<Double> stats = statisticsUtility.getExperimentStatistics(experiment.getTrialManager().getType(), experiment);
        statisticsUtility.displaySummaryStats(stats, textStats);
    }

    /**
     * This is called when the user presses confirm on one of the Trial creation fragments
     *
     * @param newTrial The new trial that was created in the fragment
     */
    @Override
    public void onOkPressed(Trial newTrial) {
        if (experiment.getSettings().getGeoLocationRequired() && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //upload trial
            experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment updated_experiment) {
                    experiment = updated_experiment;
                    experiment.getTrialManager().addTrial(newTrial);
                    experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                }
            });
        } else if (!experiment.getSettings().getGeoLocationRequired()) {
            //upload trial
            experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment updated_experiment) {
                    experiment = updated_experiment;
                    experiment.getTrialManager().addTrial(newTrial);
                    experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                }
            });
        } else if (experiment.getSettings().getGeoLocationRequired() && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(ExperimentActivity.this)
                    .setMessage("Your trial was not submitted, please enable location permissions")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
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
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired", experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addCountTrial");
                } else if (ExperimentTypeUtility.isBinomial(trialType)) {
                    BinomialTrialFragment newTrial = new BinomialTrialFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired", experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addBinomial");
                } else if (ExperimentTypeUtility.isNonNegative(trialType)) {
                    NonNegativeTrialFragment newTrial = new NonNegativeTrialFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired", experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addConNegativeTrial");
                } else if (ExperimentTypeUtility.isMeasurement(trialType)) {
                    MeasurementTrialFragment newTrial = new MeasurementTrialFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired", experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addMeasurementTrial");
                } else {
                    Log.d(TAG, "Error: invalid experiment type, see ExperimentTypeUtility.c");
                    assert (false);
                }
            }
        });

        /**
         * This sets the onClickListener for an QRCodeActivity
         */
        showQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (experiment.getTrialManager().getType().equals("BINOMIAL")){
                    Intent intent = new Intent(context, QRBinomialActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("experiment_qr", experiment);
                    intent.putExtras(args);
                    startActivity(intent);
                }else if (experiment.getTrialManager().getType().equals("COUNT")){
                    Intent intent = new Intent(context, QRCountActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("experiment_qr", experiment);
                    intent.putExtras(args);
                    startActivity(intent);
                }else if (experiment.getTrialManager().getType().equals("NONNEGATIVE")){
                    Intent intent = new Intent(context, QRNonnegActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("experiment_qr", experiment);
                    intent.putExtras(args);
                    startActivity(intent);
                }else if (experiment.getTrialManager().getType().equals("MEASUREMENT")){
                    Intent intent = new Intent(context, QRMeasurementActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("experiment_qr", experiment);
                    intent.putExtras(args);
                    startActivity(intent);
                }
            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ScanningActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("user_scan", currentUser);
                intent.putExtras(args);
                startActivity(intent);
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

        // Called when the user clicks the subscribe button
        Button subBtn = findViewById(R.id.btnSubscribe);
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User user) {
                        if (user.isSubscribed(experiment)) {
                            user.removeSubscription(experiment);
                            subBtn.setText(R.string.experiment_action_subscribe);
                        } else {
                            user.addSubscription(experiment);
                            subBtn.setText(R.string.experiment_action_unsubscribe);
                        }
                        userManager.updateUser(user);
                    }
                });
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ExperimentSettingsActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment", experiment);
                intent.putExtras(args);

                // start an ExperimentSettingsActivity
                startActivity(intent);
            }
        });

        Button statsButton = findViewById(R.id.btnStats);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StatActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment_stat", experiment);
                intent.putExtras(args);

                // start a StatActivity
                startActivity(intent);
            }
        });

        Button questionForumButton = findViewById(R.id.btnQA); // move me up
        questionForumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionForumActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment", experiment);
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
        settingsButton.setVisibility(View.INVISIBLE);


        // if the current user is the owner, set the experiment settings button as visible.
        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                Log.d(TAG, "currentUser: " + user.getUsername());
                Log.d(TAG, "owner: " + experiment.getSettings().getOwnerId());
                if (user.getId().equals(experiment.getSettings().getOwnerId())) {
                    settingsButton.setVisibility(View.VISIBLE);
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