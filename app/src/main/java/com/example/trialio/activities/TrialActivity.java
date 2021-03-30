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
import com.example.trialio.controllers.TrialManager;
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

    private ExperimentManager experimentManager;
    private UserManager userManager;

    private ListView trialListView;
    private ArrayAdapterTrials trialAdapter;
    private ArrayList<Trial> trialList;

    private Experiment experiment;
    private Boolean isUserOwner = false;


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

        // set up the adapter for the list and experiment manager
        trialListView = findViewById(R.id.trials_list);
        trialListView.setAdapter(trialAdapter);

        initState();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // update the experiment from firebase
        updateActivityData();
    }

    private void initState() {
        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                // determine if user is the owner
                isUserOwner = user.getId().equals(experiment.getSettings().getOwnerID());
                setOnClickListeners();
            }
        });
    }


    /**
     * This sets the on click listeners for the TrialActivity
     */
    public void setOnClickListeners() {

        // set the onLongClick listener for items in trial list
        trialListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the id of the clicked user
                String clickedUserId = trialList.get(i).getExperimenterId();

                int popupViewID;
                if (isUserOwner) {
                    // if the current user is the owner of the experiment, use owner menu
                    popupViewID = R.layout.menu_trials_owner;
                } else {
                    // if the current user is not the owner, use experimenter menu
                    popupViewID = R.layout.menu_trials_experimenter;
                }

                // create the popup menu
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.inflate(popupViewID);

                // listener for menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.item_ignore_user) {
                            Log.d(TAG, "Ignore user: " + clickedUserId);
                            menuIgnoreUsername(clickedUserId);
                        } else if (menuItem.getItemId() == R.id.item_view_profile) {
                            Log.d(TAG, "View profile: " + clickedUserId);
                            menuViewProfile(clickedUserId);
                        } else {
                            Log.d(TAG, "onMenuItemClick: Invalid item.");
                        }
                        return false;
                    }
                });
                popup.show();

                return false;
            }
        });
    }

    /**
     * This sets the fields for the TrialActivity using the experiment data
     */
    private void setFields() {

        // get reference to text views
        TextView experimentDescriptionTextView = findViewById(R.id.trial_description);
        ImageView experimentLocationImageView = findViewById(R.id.trials_location);
        TextView experimentTypeTextView = findViewById(R.id.trials_text_type);
        TextView experimentOwnerTextView = findViewById(R.id.trials_text_owner);
        TextView experimentStatusTextView = findViewById(R.id.trials_text_status);

        // set text views
        experimentDescriptionTextView.setText(experiment.getSettings().getDescription());
        experimentTypeTextView.setText(experiment.getTrialManager().getType());
        experimentOwnerTextView.setText(experiment.getSettings().getOwnerID());

        // set experiment status
        if (experiment.getTrialManager().getIsOpen()) {
            experimentStatusTextView.setText(R.string.experiment_status_open);
        } else {
            experimentStatusTextView.setText(R.string.experiment_status_closed);
        }

        // set geolocation requirement
        if (experiment.getSettings().getGeoLocationRequired()) {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_on_24);
        } else {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_off_24);
        }

        // set owner username
        userManager.getUserById(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                experimentOwnerTextView.setText(user.getUsername());
            }
        });
    }

    /**
     * This switches to a ViewUserActivity with the given user as the argument.
     */
    private void menuViewProfile(String userID) {
        userManager.getUserById(userID, new UserManager.OnUserFetchListener() {
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
     * <p>
     * TODO: This can be refactored into a command object that takes the input of Experiment and
     * User
     *
     * @param userId String of the user ID to add to the ignored list for the experiment.
     */
    private void menuIgnoreUsername(String userId) {

        // get the experiment from firebase
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {

                // update the experiment
                experiment = newExperiment;

                // add the userID to the list of ignored userIDs
                experiment.getTrialManager().addIgnoredUser(userId);

                // update the experiment
                experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                updateActivityData();
            }
        });
    }

    /**
     * This gets the updated experiment from firebase, and updates the views of the activity.
     */
    private void updateActivityData() {

        // get the experiment, set all the fields and the trial list
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {

                // update local experiment
                experiment = new_experiment;

                // set the fields with the data from the experiment
                setFields();

                // update the trialList
                experiment.getTrialManager().setAllVisibleTrialsFetchListener(new TrialManager.OnAllVisibleTrialsFetchListener() {
                    @Override
                    public void onAllVisibleTrialsFetch(ArrayList<Trial> newTrialList) {
                        trialList.clear();
                        trialList.addAll(newTrialList);
                        trialAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}