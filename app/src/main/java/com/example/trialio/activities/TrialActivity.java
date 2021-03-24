package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import javax.annotation.Nullable;

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

    /**
     * the On create the takes in the saved instance from the experiment activity
     * @param savedInstanceState
     */
    @Override
    @Nullable
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

        // Set up the adapter for the list and experiment manager
        trialListView = findViewById(R.id.list_trials);
        trialListView.setAdapter(trialAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                trialList.addAll(experiment.getTrialManager().getVisibleTrials());
                trialAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This sets the on click listeners for the TrialActivity
     */
    public void setOnClickListeners() {
        trialListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // check if the current user is the owner
                userManager.getUser(trialList.get(i).getExperimenterID(), new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User user) {
                        int popupViewID = R.layout.menu_trials_experimenter;
                        if (user.getId().equals(experiment.getSettings().getOwnerID())) {
                            popupViewID = R.layout.menu_trials_owner;
                        }
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.inflate(popupViewID);

                        // listener for menu
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.item_ignore_user:

                                        break;
                                    case R.id.item_view_profile:
                                        Intent intent = new Intent(context, ViewUserActivity.class);

                                        // pass in experiment as an argument
                                        Bundle args = new Bundle();
                                        args.putSerializable("user", user);
                                        intent.putExtras(args);

                                        Log.d(TAG, "View profile: " + user.getId());

                                        // start an ExperimentActivity
                                        startActivity(intent);
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
        userManager.getUser(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Owner: " + user.getUsername());
            }
        });
    }
}