package com.example.trialio.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.BinomialTrialFragment;
import com.example.trialio.fragments.CountTrialFragment;
import com.example.trialio.fragments.MeasurementTrialFragment;
import com.example.trialio.fragments.NonNegativeTrialFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepliesActivity {
}

package com.example.trialio.activities;

/**
 * This activity opens an question (w/ details) with all of it's replies. The activity is opened when a question is clicked in the list view from QuestionForumActivity.
 */

public class QuestionRepliesActivity extends AppCompatActivity {

    private final String TAG = "QuestionRepliesForumActivity";


    private Question selectedQuestion;
    private ArrayList<Reply> replyList;
    private RepliesArrayAdapter replyAdapter;



    // managers
    QuestionForumManager questionForumManager;
    UserManager userManager;
    ExperimentManager experimentManager;

    /**
     * the On create the takes in the saved instance from the question forum activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_forum_detailed);

        // Took ActionBar code.
        // DATE:	2020-12-14
        // LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
        // SOURCE: 	Add an up action [https://developer.android.com/training/appbar/up-action]
        // AUTHOR: 	Android Developers [https://developer.android.com/]
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the experiment that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        selectedQuestion = (Question) bundle.getSerializable("question_details");

        String questionID = selectedQuestion.getPostID();

        QuestionForumManager questionForumManager = new QuestionForumManager(questionID);
        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // get the important views in this activity
        TextView authorID = findViewById(R.id.selectedQuestionAuthorID);
        TextView selectedQuestionTitle = findViewById(R.id.selectedQuestionTitle);
        TextView selectedQuestionBody = findViewById(R.id.selectedQuestionBody);

        EditText replyBox = findViewById(R.id.regionEditText);
        Button replyButton = findViewById(R.id.replyButton);


        // set views with selectedQuestion details
        authorID.setText(selectedQuestion.getPostID());
        selectedQuestionTitle.setText(selectedQuestion.getTitle());
        selectedQuestionBody.setText(selectedQuestion.getBody());

    }

    @Override
    protected void onStart() {
        super.onStart();

        setReplyList();
    }


    private void setReplyList() {
        questionForumManager.setOnAllQuestionsFetchCallback(new QuestionForumManager.OnManyQuestionsFetchListener() {
            @Override
            public void onManyQuestionsFetch(List<Question> questions) {  // TODO: why not ArrayList ***
                questionList.clear();
                if (questions.isEmpty()) {
                    Log.d(TAG, "onManyQuestionsFetch: No question exist, initiate an empty array list to avoid crash "); //TODO: this seems hacky
                    questionList = new ArrayList<>();
                    questionAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "onManyQuestionsFetch: Succesfully fetched questions");
                    questionList.addAll(questions);
                    questionAdapter.notifyDataSetChanged();
                }
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
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        TextView textDescription = findViewById(R.id.txtExperimentDesciption);
        TextView textType = findViewById(R.id.txtExperimentType);
        TextView textRegion = findViewById(R.id.txtExperimentRegion);
        TextView textOwner = findViewById(R.id.txtExperimentOwner);
        TextView textStatus = findViewById(R.id.txtExperimentStatus);
        TextView textMinTrials = findViewById(R.id.txtExperimentMinTrials);
        TextView textStats = findViewById(R.id.txtStatsSummary);
        TextView textGeoWarning = findViewById(R.id.txtExperimentGeoWarning);
        Button subBtn = findViewById(R.id.btnSubscribe);

        // set TextViews
        textDescription.setText("Description: " + experiment.getSettings().getDescription());
        textType.setText("Type: " + experiment.getTrialManager().getType());
        textRegion.setText("Region: " + experiment.getSettings().getRegion().getDescription());

        // get the owner's username
        userManager.getUser(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Owner: " + user.getUsername());
            }
        });

        // if this is a geo experiment, give a warning
        if (experiment.getSettings().getGeoLocationRequired()) {
            textGeoWarning.setText("Warning! Geo-location information is collected with trials for this experiment.");
        } else {
            textGeoWarning.setText("");
        }

        textStatus.setText("Open: " + (experiment.getTrialManager().getIsOpen() ? "yes" : "no"));
        textMinTrials.setText("Minimum number of trials: " + experiment.getTrialManager().getMinNumOfTrials());
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
        // TODO: this code is also used in StatActivity, make it so code only written once
        ArrayList<Double> stats = statisticsUtility.getExperimentStatistics(experiment.getTrialManager().getType(), experiment);

        // Took rounding code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY-SA 2.5 [https://creativecommons.org/licenses/by-sa/2.5/]
        // SOURCE:  Working with Spinners in Android [https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java]
        // AUTHOR: 	Stack Overflow User: asterite
        if(stats.get(0) == 1) {
            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue());
        } else if(stats.get(0) == 2) {
            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                    "\nSuccesses: " + stats.get(2).intValue() + "\nFailures: " +
                    stats.get(3).intValue() + "\nSuccess Rate: " +
                    Math.round(stats.get(4) * 10000d) / 10000d);
        } else if(stats.get(0) == 3) {
            String modes = Integer.toString(stats.get(6).intValue());
            for(int i=7; i<stats.size(); i++) {
                modes += ", " + stats.get(i).intValue();
            }

            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                    "\nMean: " + Math.round(stats.get(2) * 10000d) / 10000d + "\nMedian: " +
                    Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                    Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                    Math.round(stats.get(5) * 10000d) / 10000d + "\nMode(s): " + modes);
        } else if(stats.get(0) == 4) {
            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                    "\nMean: " + Math.round(stats.get(2) * 10000d) / 10000d + "\nMedian: " +
                    Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                    Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                    Math.round(stats.get(5) * 10000d) / 10000d);
        }

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
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
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
                    args.putBoolean("GeoLocationRequired",experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addCountTrial");
                }
                else if (ExperimentTypeUtility.isBinomial(trialType)) {
                    BinomialTrialFragment newTrial = new BinomialTrialFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired",experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addBinomial");
                }
                else if (ExperimentTypeUtility.isNonNegative(trialType)) {
                    NonNegativeTrialFragment newTrial = new NonNegativeTrialFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired",experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addConNegativeTrial");
                }
                else if (ExperimentTypeUtility.isMeasurement(trialType)) {
                    MeasurementTrialFragment newTrial = new MeasurementTrialFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("GeoLocationRequired",experiment.getSettings().getGeoLocationRequired());
                    newTrial.setArguments(args);
                    newTrial.show(getSupportFragmentManager(), "addMeasurementTrial");
                }
                else {
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
                Intent intent = new Intent(context, QRCodeActivity.class);

                Bundle args = new Bundle();
                args.putSerializable("experiment_qr", experiment);
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
                Log.d(TAG, "currentUser: " + user.getId());
                Log.d(TAG, "owner: " + experiment.getSettings().getOwnerID());
                if (user.getId().equals(experiment.getSettings().getOwnerID())) {
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