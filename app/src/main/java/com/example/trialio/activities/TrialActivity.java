package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterTrials;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.ArrayList;

/**
 * This activity shows a list of trials for an experiment when a user clicks the "trials" button from the
 * experiment activity
 */
public class TrialActivity extends AppCompatActivity {
    private final String TAG = "TrialActivity";
    private final Context context = this;

    private ArrayAdapterTrials trialAdapter;
    private ArrayList<Trial> trialList;
    private ExperimentManager experimentManager;
    private Experiment experiment;
    private UserManager userManager;
    private ListView trialListView;

    private TextView experimentDescriptionTextView;
    private ImageView experimentLocationImageView;
    private TextView experimentTypeTextView;
    private TextView experimentOwnerTextView;
    private TextView experimentStatusTextView;

    /**
     * the On create the takes in the saved instance from the experiment activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_trial");

        // get the managers
        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // set the trialList and adapter
        trialList = new ArrayList<>();
        trialAdapter = new ArrayAdapterTrials(this, trialList, experiment.getTrialManager().getType());

        // views

        experimentDescriptionTextView = findViewById(R.id.trial_description);
        experimentLocationImageView = findViewById(R.id.trials_location);
        experimentTypeTextView = findViewById(R.id.trials_text_type);
        experimentOwnerTextView = findViewById(R.id.trials_text_owner);
        experimentStatusTextView = findViewById(R.id.trials_text_status);


        // set experiment info

        experimentDescriptionTextView.setText(experiment.getSettings().getDescription());
        experimentTypeTextView.setText(experiment.getTrialManager().getType());
        experimentOwnerTextView.setText(experiment.getSettings().getOwnerUsername());

        if ( experiment.getTrialManager().getIsOpen() ) {
            experimentStatusTextView.setText("Open");
        } else {
            experimentStatusTextView.setText("Closed");
        }
        if (!experiment.getSettings().getGeoLocationRequired()) {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_off_24);
        } else {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_on_24);
        }


        // Set up the adapter for the list and experiment manager
        trialListView = findViewById(R.id.trials_list);
        trialListView.setAdapter(trialAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // update the experiment from firebase
        updateActivityData();
    }

    /**
     * This sets the on click listeners for the TrialActivity
     */
    public void setOnClickListeners() {
        trialListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get the username of the clicked user
                String clickedUsername = trialList.get(i).getExperimenterUsername();

                // check if the current user is the owner
                userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User currentUser) {

                        // by default use the experimenter menu
                        int popupViewID = R.layout.menu_trials_experimenter;

                        // if the current user is the owner of the experiment, use the owner menu
                        if (currentUser.getUsername().equals(experiment.getSettings().getOwnerUsername())) {
                            popupViewID = R.layout.menu_trials_owner;
                        }

                        // create the popup menu
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.inflate(popupViewID);

                        // listener for menu
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.item_ignore_user:
                                        Log.d(TAG, "Ignore user: " + clickedUsername);
                                        menuIgnoreUsername(clickedUsername);
                                        break;
                                    case R.id.item_view_profile:
                                        Log.d(TAG, "View profile: " + clickedUsername);
                                        menuViewProfile(clickedUsername);
                                        break;
                                    default:
                                        Log.d(TAG, "onMenuItemClick: Invalid item.");
                                        assert(false);
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
                return false;
            }
        });
    }

    /**
     * This sets the fields for the TrialActivity using the experiment data
     */
    public void setFields() {

        // get the fields
        TextView textDescription = findViewById(R.id.txtExperimentDescriptionTrial);
        TextView textOwner = findViewById(R.id.txtExperimentOwnerTrial);
        TextView textType = findViewById(R.id.txtExperimentTypeTrial);

        // set the fields
        textDescription.setText("Description: " + experiment.getSettings().getDescription());
        textType.setText("Type: " + experiment.getTrialManager().getType());

        // get the owner's username
        userManager.getUser(experiment.getSettings().getOwnerUsername(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Owner: " + user.getUsername());
            }
        });
    }

    /**
     * This switches to a ViewUserActivity with the given user as the argument.
     */
    public void menuViewProfile(String userID) {
        userManager.getUser(userID, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                Intent intent = new Intent(context, ViewUserActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("user", user);
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });
    }

    /**
     * This adds a username to the ignored list for the experiment.
     * @param username String of the user ID to add to the ignored list for the experiment.
     */
    public void menuIgnoreUsername(String username) {

        // get the experiment from firebase
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {

                // update the experiment
                experiment = newExperiment;

                // add the userID to the list of ignored userIDs
                experiment.getTrialManager().addIgnoredUser(username);

                // update the experiment
                experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                updateActivityData();
            }
        });
    }

    /**
     * This gets the updated experiment from firebase, and updates the views of the activity.
     */
    public void updateActivityData() {

        // get the experiment, set all the fields and the trial list
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {

                // update local experiment
                experiment = new_experiment;

                // set the fields with the data from the experiment
                setFields();

                // set listeners
                setOnClickListeners();

                // update the trialList
                trialList.clear();
                trialList.addAll(experiment.getTrialManager().fetchVisibleTrials());
                trialAdapter.notifyDataSetChanged();
            }
        });
    }
}