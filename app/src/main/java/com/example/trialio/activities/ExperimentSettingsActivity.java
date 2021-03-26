package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterUsers;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.fragments.AddIgnoredFragment;
import com.example.trialio.models.Experiment;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * This activity allows an experiment owner to modify the settings of an experiment they own
 */
public class ExperimentSettingsActivity extends AppCompatActivity implements AddIgnoredFragment.OnFragmentInteractionListener{
    private final String TAG = "ExperimentSettingsActivity";
    private Context context;

    private ExperimentManager experimentManager;
    private Experiment experiment;
    private UserManager userManager;
    private Button unpublishButton;
    private Switch isOpenSwitch;
    private ListView ignoredListView;
    private ArrayList<String> ignoredList;
    private ArrayAdapterUsers ignoredAdapter;
    private Button addIgnoredButton;
    private Button removeIgnoredButton;

    /**
     * the On create the takes in the saved instance from the experiment activity
     * @param savedInstanceState
     */
    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings);

        context = this;

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // initialize the ignored list
        ignoredList = new ArrayList<String>();

        // get managers
        experimentManager = new ExperimentManager();
        userManager = new UserManager();
        ignoredAdapter = new ArrayAdapterUsers(context, ignoredList);

        // get views
        unpublishButton = (Button) findViewById(R.id.button_unpublish_experiment);
        isOpenSwitch = (Switch) findViewById(R.id.switch_isopen_settings);
        ignoredListView = (ListView) findViewById(R.id.list_ignored_experimenters);
        addIgnoredButton = (Button) findViewById(R.id.button_add_ignored);

        // set adapter
        ignoredListView.setAdapter(ignoredAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateActivityData();
    }

    /**
     * This sets the fields of the ExperimentSettingsActivity.
     */
    public void setFields() {
        isOpenSwitch.setChecked(experiment.getTrialManager().getIsOpen());
    }

    /**
     * This sets the on click listeners for the ExperimentSettingsActivity.
     */
    public void setOnClickListeners() {

        // when the switch is switched onCheckedChanged is called
        isOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // get the most updated experiment from firebase, update it's isOpen field, and set it back in firebase
                experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment new_experiment) {
                        experiment = new_experiment;
                        experiment.getTrialManager().setIsOpen(b);
                        experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                    }
                });
            }
        });

        // remove the experiment from firebase and return to the home page
        unpublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experimentManager.unpublishExperiment(experiment.getExperimentID());
                Intent intent = new Intent(context, MainActivity.class);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        // give option to remove ignored username when user long clicks in the listView
        ignoredListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get clicked username
                String clickedUsername = ignoredList.get(i);

                // create the popup menu
                PopupMenu popup = new PopupMenu(context, view);
                popup.inflate(R.layout.menu_remove_ignored);

                // listener for menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_unignore:
                                Log.d(TAG, "UnIgnore user: " + clickedUsername);
                                menuUnignoreUsername(clickedUsername);
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
                return false;
            }
        });

        addIgnoredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddIgnoredFragment addIgnored = new AddIgnoredFragment();
                addIgnored.show(getSupportFragmentManager(), "addIgnored");
            }
        });
    }

    /**
     * This gets the updated experiment from firebase, and updates the views of the activity.
     */
    public void updateActivityData() {

        // get experiment and update views of the activity
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {

                // update experiment
                experiment = new_experiment;

                // update ignored listView
                ignoredList.clear();
                ignoredList.addAll(experiment.getTrialManager().getIgnoredUserIds());
                ignoredAdapter.notifyDataSetChanged();

                // set fields
                setFields();

                // set on click listeners
                setOnClickListeners();
            }
        });
    }

    /**
     * This removes a username from the ignore list of the experiment.
     * @param username The string of the userID to remove from the ignore list of the experiment.
     */
    public void menuUnignoreUsername(String username) {

        // update the experiment and remove a
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {

                // update experiment
                experiment = newExperiment;

                // edit experiment in firebase
                experiment.getTrialManager().removeIgnoredUsers(username);
                experimentManager.editExperiment(experiment.getExperimentID(), experiment);

                // update data in activity
                updateActivityData();
            }
        });
    }

    @Override
    public void onOkPressed(String username) {
        // update the experiment and remove a
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {

                // update experiment
                experiment = newExperiment;

                // edit experiment in firebase
                experiment.getTrialManager().addIgnoredUser(username);
                experimentManager.editExperiment(experiment.getExperimentID(), experiment);

                // update data in activity
                updateActivityData();
            }
        });
    }
}
