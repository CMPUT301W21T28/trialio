package com.example.trialio.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.UserAdapter;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.controllers.ViewUserProfileCommand;
import com.example.trialio.fragments.AddIgnoredFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * This activity allows an experiment owner to modify the settings of an experiment they own
 * <p>
 * This activity navigates to no other activities.
 */
public class ExperimentSettingsActivity extends AppCompatActivity implements AddIgnoredFragment.OnFragmentInteractionListener {
    private final String TAG = "ExpSettingsActivity";
    private Context context;

    private ExperimentManager experimentManager;
    private Experiment experiment;
    private UserManager userManager;
    private Button deleteButton;
    private Switch isOpenSwitch;
    private Switch isPublishedSwitch;
    private ListView ignoredListView;
    private ArrayList<String> ignoredIDList;
    private UserAdapter ignoredAdapter;
    private Button addIgnoredButton;


    private TextView experimentDescriptionTextView;
    private ImageView experimentLocationImageView;
    private TextView experimentTypeTextView;
    private TextView experimentOwnerTextView;
    private TextView experimentStatusTextView;

    /**
     * the On create the takes in the saved instance from the experiment activity
     *
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
        ignoredIDList = new ArrayList<String>();

        // get managers
        experimentManager = new ExperimentManager();
        userManager = new UserManager();
        ignoredAdapter = new UserAdapter(context, ignoredIDList);

        // get views
        experimentDescriptionTextView = findViewById(R.id.settings_description);
        experimentLocationImageView = findViewById(R.id.settings_location);
        experimentTypeTextView = findViewById(R.id.settings_text_type);
        experimentOwnerTextView = findViewById(R.id.settings_text_owner);
        experimentStatusTextView = findViewById(R.id.settings_text_status);

        deleteButton = (Button) findViewById(R.id.button_delete_experiment);
        isOpenSwitch = (Switch) findViewById(R.id.switch_isopen_settings);
        isPublishedSwitch = (Switch) findViewById(R.id.switch_ispublished_settings);
        ignoredListView = (ListView) findViewById(R.id.list_ignored_experimenters);
        addIgnoredButton = (Button) findViewById(R.id.button_add_ignored);

        // set experiment info
        experimentDescriptionTextView.setText(experiment.getSettings().getDescription());
        experimentTypeTextView.setText(experiment.getTrialManager().getType());

        // get the username of the owner
        userManager.getUserById(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                if (user != null) {
                    experimentOwnerTextView.setText(user.getUsername());
                } else {
                    Log.e(TAG, "Could not load user");
                }
            }
        });

        if (experiment.getTrialManager().getIsOpen()) {
            experimentStatusTextView.setText(R.string.experiment_status_open);
        } else {
            experimentStatusTextView.setText(R.string.experiment_status_closed);
        }

        if (!experiment.getSettings().getGeoLocationRequired()) {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_off_24);
        } else {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_on_24);
        }

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
        isPublishedSwitch.setChecked(experiment.getIsPublished());
    }

    /**
     * This sets the on click listeners for the ExperimentSettingsActivity.
     */
    public void setOnClickListeners() {

        // when the isOpen switch is switched onCheckedChanged is called
        isOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // get the most updated experiment from firebase, update it's isOpen field, and set it back in firebase
                experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment new_experiment) {
                        if (new_experiment != null) {
                            experiment = new_experiment;
                            experiment.getTrialManager().setIsOpen(b);
                            experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                        } else {
                            Log.e(TAG, "Could not load experiment");
                        }
                    }
                });
            }
        });

        // when the isPublished switch is switched onCheckedChanged is called
        isPublishedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // get the most updated experiment from firebase, update it's isPublished field, and set it back in firebase
                experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment new_experiment) {
                        if (new_experiment != null) {
                            experiment = new_experiment;
                            experiment.setIsPublished(b);
                            experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                        } else {
                            Log.d(TAG, "Failed to load experiment");
                        }
                    }
                });
            }
        });

        // remove the experiment from firebase and return to the home page
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experimentManager.deleteExperiment(experiment.getExperimentID(), userManager);
                Intent intent = new Intent(context, MainActivity.class);

                // start an ExperimentActivity
                startActivity(intent);
                finish();
            }
        });

        // give option to remove ignored username when user long clicks in the listView
        ignoredListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get clicked username
                String clickedUserID = ignoredIDList.get(i);

                // create the popup menu
                PopupMenu popup = new PopupMenu(context, view);
                popup.inflate(R.layout.menu_remove_ignored);

                // listener for menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_unignore:
                                Log.d(TAG, "UnIgnore user: " + clickedUserID);
                                menuUnignoreID(clickedUserID);
                                break;
                            default:
                                Log.d(TAG, "onMenuItemClick: Invalid item.");
                                assert (false);
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

        experimentOwnerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create and execute a ViewUserProfileCommand
                ViewUserProfileCommand command = new ViewUserProfileCommand(context, experiment.getSettings().getOwnerID());
                command.execute();
            }
        });

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    /**
     * This gets the updated experiment from firebase, and updates the views of the activity.
     */
    public void updateActivityData() {

        // get experiment and update views of the activity
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {

                if (new_experiment != null) {
                    // update experiment
                    experiment = new_experiment;

                    // update ignored listView
                    ignoredIDList.clear();
                    ignoredIDList.addAll(experiment.getTrialManager().getIgnoredUserIDs());
                    ignoredAdapter.notifyDataSetChanged();

                    // set fields
                    setFields();

                    // set on click listeners
                    setOnClickListeners();
                } else {
                    Log.e(TAG, "Failed to fetch experiment");
                }
            }
        });
    }

    /**
     * This removes a username from the ignore list of the experiment.
     *
     * @param userID The string of the userID to remove from the ignore list of the experiment.
     */
    public void menuUnignoreID(String userID) {

        // update the experiment and remove a
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {

                if (newExperiment != null) {
                    // update experiment
                    experiment = newExperiment;

                    // edit experiment in firebase
                    experiment.getTrialManager().removeIgnoredUsers(userID);
                    experimentManager.editExperiment(experiment.getExperimentID(), experiment);

                    // update data in activity
                    updateActivityData();
                } else {
                    Log.e(TAG, "Failed to fetch experiments");
                }
            }
        });
    }

    @Override
    public void onOkPressed(String userID) {
        // update the experiment and remove a
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment newExperiment) {
                if (newExperiment !=  null) {
                    // update experiment
                    experiment = newExperiment;

                    // edit experiment in firebase
                    experiment.getTrialManager().addIgnoredUser(userID);
                    experimentManager.editExperiment(experiment.getExperimentID(), experiment);

                    // update data in activity
                    updateActivityData();
                } else {
                    Log.e(TAG, "Failed to fetch experiment");
                }
            }
        });
    }
}