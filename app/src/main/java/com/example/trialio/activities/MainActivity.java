package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.trialio.adapters.ExperimentAdapter;
import com.example.trialio.controllers.CurrentUserHandler;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.R;
import com.example.trialio.controllers.ViewUserProfileCommand;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This activity is the main entry point into the application and displays a list of available
 * experiments which can be toggled to show different experiment lists.
 * <p>
 * This activity navigates to:
 * <ul>
 *     <li>ExperimentActivity</li>
 *     <li>CreateExperimentActivity</li>
 *     <li>ViewUserActivity</li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private final Context context = this;
    private Button activeListButton;

    private ExperimentManager experimentManager;
    private ArrayList<Experiment> experimentList;
    private ExperimentAdapter experimentAdapter;
    private User currentUser;

    private enum listMode {ALL, OWNED, SUBS}  // enum used to specify which experiment list to show
    private listMode mode;  // specify which list to show at a given time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeListButton = findViewById(R.id.btnAll);

        // Initialize attributes for the activity
        experimentManager = new ExperimentManager();
        experimentList = new ArrayList<>();
        experimentAdapter = new ExperimentAdapter(this, experimentList);

        // Set up the adapter for the ListView
        ListView experimentListView = findViewById(R.id.list_experiment);
        experimentListView.setAdapter(experimentAdapter);

        // set the list mode as default ALL
        mode = listMode.ALL;

        // Set up onClick listeners
        setUpOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set the experiment list and the current user
        setExperimentList();
        setCurrentUser();
    }

    /**
     * Sets the experiment list depending on the mode.
     */
    public void setExperimentList() {
        if (mode == listMode.ALL) {
            setExperimentListToAll();
        } else if (mode == listMode.OWNED) {
            setExperimentListToOwned();
        } else if (mode == listMode.SUBS) {
            setExperimentListToSubs();
        } else {
            Log.d(TAG, "Error: Invalid listMode.");
            assert false;
        }
    }

    /**
     * Sets the current user attribute for the activity
     */
    private void setCurrentUser() {
        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
            @Override
            public void onUserFetch(User user) {
                currentUser = user;
            }
        });
    }

    /**
     * Sets up on click listeners for the activity.
     */
    private void setUpOnClickListeners() {
        // Called when the user clicks item in experiment list
        ListView experimentListView = findViewById(R.id.list_experiment);
        experimentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ExperimentActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment", experimentList.get(i));
                args.putSerializable("user_exp", currentUser);
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        // set up the listener to view the profile of the owner of an experiment in the list view
        experimentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get the userID
                String userID = experimentList.get(i).getSettings().getOwnerID();

                // create the popup menu
                int popupViewID = R.layout.menu_view_profile;
                PopupMenu popup = new PopupMenu(getApplicationContext(), view);
                popup.inflate(popupViewID);

                // listener for menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.item_view_profile) {
                            Log.d(TAG, "View profile: " + userID);

                            // create and execute a ViewUserProfileCommand
                            ViewUserProfileCommand command = new ViewUserProfileCommand(context, userID);
                            command.execute();
                        } else {
                            Log.d(TAG, "onMenuItemClick: Invalid item.");
                        }
                        return false;
                    }
                });
                popup.show();

                // return true so that the regular on click does not occur
                return true;
            }
        });

        // Called when the user taps the profile icon on the top right of main activity
        ImageButton editProfile = (ImageButton) findViewById(R.id.editUserBtn);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the current user and go to view user activity
                CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
                    @Override
                    public void onUserFetch(User user) {
                        currentUser = user;
                        Intent intent = new Intent(context, ViewUserActivity.class);
                        intent.putExtra("user", currentUser);
                        startActivity(intent);
                    }
                });
            }
        });

        // Called when the All button is clicked
        Button allToggleButton = (Button) findViewById(R.id.btnAll);
        allToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "All was clicked");
                toggleListButton(R.id.btnAll);
                setExperimentListToAll();
            }
        });

        // Called when the Owned button is clicked
        Button ownedToggleButton = (Button) findViewById(R.id.btnOwned);
        ownedToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Owned was clicked");
                toggleListButton(R.id.btnOwned);
                setExperimentListToOwned();
            }
        });

        // Called when the Subs button is clicked
        Button subsToggleButton = (Button) findViewById(R.id.btnSubs);
        subsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Subs was clicked");
                toggleListButton(R.id.btnSubs);
                setExperimentListToSubs();
            }
        });

        // Called when the Add button is clicked
        Button addExperiment = (Button) findViewById(R.id.btnNewExperiment);

        addExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add was clicked");

                Intent intent = new Intent(context, ExperimentCreateActivity.class);
                startActivity(intent);
            }
        });

        /* Android Developer Docs, "TextView", 2021-03-17, Apache 2.0,
         * https://developer.android.com/reference/android/widget/TextView.html#setOnEditorActionListener(android.widget.TextView.OnEditorActionListener)
         */
        EditText searchBar = findViewById(R.id.experiment_search_bar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // enter key was pressed
                    String text = v.getText().toString();
                    /* BalusC, https://stackoverflow.com/users/157882/balusc, 2010-08-14, CC BY-SA
                     * https://stackoverflow.com/a/3481842/15048024
                     */
                    String[] words = text.split(" ", -1);
                    List<String> keywords = new ArrayList<String>(Arrays.asList(words));
                    for (String k : keywords) {
                        if (k.equals("")) {
                            keywords.remove(k);
                        }
                    }
                    experimentManager.searchByKeyword(keywords, new ExperimentManager.OnManyExperimentsFetchListener() {
                        @Override
                        public void onManyExperimentsFetch(List<Experiment> experiments) {
                            experimentList.clear();
                            experimentList.addAll(experiments);
                            experimentAdapter.notifyDataSetChanged();
                        }
                    });
                    Log.d(TAG, "Search for " + text);
                }
                return false;
            }
        });

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    /**
     * This method changes the state of the experiment list if the user toggles between the "owned","all" and "subscribed" experiment lists
     *
     * @param btn
     */

    private void toggleListButton(int btn) {
        /* Shayne3000, https://stackoverflow.com/users/8801181/shayne3000,
         * "How to add button tint programmatically", 2018-02-13, CC BY-SA 3.0
         * https://stackoverflow.com/questions/29801031/how-to-add-button-tint-programmatically/49259711#49259711
         */

        // Set old button to grey
        Drawable buttonDrawable = activeListButton.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.button_dark_grey));
        activeListButton.setBackground(buttonDrawable);

        // Set new button to special yellow
        Button selectedBtn = (Button) findViewById(btn);
        buttonDrawable = selectedBtn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, getResources().getColor(R.color.special_yellow));
        selectedBtn.setBackground(buttonDrawable);

        activeListButton = selectedBtn;

    }

    /**
     * these methods change the list of displayed experiments to the appropriate list as the user toggles between the pages
     */

    private void setExperimentListToAll() {

        // set the experiment list mode to ALL
        mode = listMode.ALL;

        // Fetch data for the list view
        experimentList.clear();
        experimentManager.setOnAllPublishedExperimentsFetchCallback(new ExperimentManager.OnManyExperimentsFetchListener() {
            @Override
            public void onManyExperimentsFetch(List<Experiment> experiments) {
                experimentList.addAll(experiments);
                experimentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setExperimentListToOwned() {

        // set the experiment list mode to OWNED
        mode = listMode.OWNED;

        // Fetch data for the list view
        experimentList.clear();
        if (currentUser != null) {
            experimentManager.getOwnedExperiments(currentUser, new ExperimentManager.OnManyExperimentsFetchListener() {
                @Override
                public void onManyExperimentsFetch(List<Experiment> experiments) {
                    experimentList.addAll(experiments);
                    experimentAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void setExperimentListToSubs() {

        // set the experiment list mode to SUBS
        mode = listMode.SUBS;

        // Fetch data for the list view
        if (currentUser != null) {
            ArrayList<String> expIds = currentUser.getSubscribedExperiments();
            experimentList.clear();
            for (String id : expIds) {
                experimentManager.setOnExperimentFetchListener(id, new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment experiment) {
                        // if the experiment is published and the user is subscribed, add it to the list to display
                        if (experiment != null && experiment.getIsPublished()) {
                            experimentList.add(experiment);
                            experimentAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
            experimentAdapter.notifyDataSetChanged();
        }
    }
}
