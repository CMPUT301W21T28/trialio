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
import com.example.trialio.adapters.TrialAdapter;
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;

import java.util.ArrayList;

/**
 * This activity shows a list of trials for an experiment when a user clicks the "trials" button from the
 * experiment activity
 * <p>
 * This activity navigates to:
 * <ul>
 *     <li>ViewUserActivity</li>
 * </ul>
 */
public class TrialActivity extends AppCompatActivity {
    private final String TAG = "TrialActivity";

    /**
     * The ListView of submitted trials
     */
    private ListView trialListView;

    /**
     * Adapter for trialListView
     */
    private TrialAdapter trialAdapter;

    /**
     * The data source of submitted trials
     */
    private ArrayList<Trial> trialList;

    /**
     * The experiment being viewed in the activity
     */
    private Experiment experiment;

    /**
     * The trial manager for the activity experiment
     */
    private TrialManager trialManager;

    /**
     * Flag indicating if current user owns the experiment
     */
    private Boolean isUserOwner = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_trial");
        trialManager = experiment.getTrialManager();

        // set the trialList and adapter
        trialList = new ArrayList<>();
        trialAdapter = new TrialAdapter(this, trialList, experiment.getTrialManager().getType());

        // set up the adapter for the list and experiment manager
        trialListView = findViewById(R.id.trials_list);
        trialListView.setAdapter(trialAdapter);

        initState();

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // update the experiment from firebase
        updateExperimentData();
        updateTrialData();
    }

    /**
     * Initialize the state for the Activity
     */
    private void initState() {
        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
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
                        } else if (menuItem.getItemId() == R.id.item_register_barcode){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("experiment", experiment);


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
        UserManager userManager = new UserManager();
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
        UserManager userManager = new UserManager();
        userManager.getUserById(userID, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                Intent intent = new Intent(getApplicationContext(), ViewUserActivity.class);

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
        ExperimentManager experimentManager = new ExperimentManager();
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {
                experiment = newExperiment;                                                     // update the local experiment
                trialManager.addIgnoredUser(userId);                            // add the userID to the list of ignored userIDs
                experimentManager.editExperiment(experiment.getExperimentID(), experiment);     // update the experiment
                updateExperimentData();
            }
        });
    }

    /**
     * Updates the experiment data displayed in the activity with database data
     */
    private void updateExperimentData() {
        ExperimentManager experimentManager = new ExperimentManager();
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {
                experiment = new_experiment;        // update local experiment
                setFields();                        // set views with experiment values
            }
        });
    }

    /**
     * Updates the trial data displayed in the activity with database data
     */
    private void updateTrialData() {
        trialManager.setAllVisibleTrialsFetchListener(new TrialManager.OnAllVisibleTrialsFetchListener() {
            @Override
            public void onAllVisibleTrialsFetch(ArrayList<Trial> newTrialList) {
                trialList.clear();
                trialList.addAll(newTrialList);
                trialAdapter.notifyDataSetChanged();
            }
        });
    }
}