package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.trialio.adapters.ArrayAdapterExperiment;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.R;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private final Context context = this;
    private Button activeListButton;

    private ExperimentManager experimentManager;
    private ArrayList<Experiment> experimentList;
    private ArrayAdapterExperiment experimentAdapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeListButton = findViewById(R.id.btnAll);

        // Initialize attributes for the activity
        experimentManager = new ExperimentManager();
        experimentList = new ArrayList<>();
        experimentAdapter = new ArrayAdapterExperiment(this, experimentList);
        UserManager userManager = new UserManager();
        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                currentUser = user;
            }
        });


        // Set up the adapter for the ListView
        ListView experimentListView = findViewById(R.id.list_experiment);
        experimentListView.setAdapter(experimentAdapter);

        // Set up onClick listeners
        setUpOnClickListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();


        setExperimentListToAll();

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
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        // Called when the user taps the profile icon on the top right of main activity
        ImageButton editProfile = (ImageButton) findViewById(R.id.editUserBtn);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewUserActivity.class);
                intent.putExtra("User", currentUser);
                startActivity(intent);
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
                toggleListButton(R.id.btnSubs );
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
    }

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

    private void setExperimentListToAll() {
        // Fetch data for the list view
        experimentManager.setOnAllExperimentsFetchCallback(new ExperimentManager.OnManyExperimentsFetchListener() {
            @Override
            public void onManyExperimentsFetch(ArrayList<Experiment> experiments) {
                experimentList.clear();
                experimentList.addAll(experiments);
                experimentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setExperimentListToOwned() {
        // Fetch data for the list view
        if (currentUser != null) {
            experimentManager.getOwnedExperiments(currentUser, new ExperimentManager.OnManyExperimentsFetchListener() {
                @Override
                public void onManyExperimentsFetch(ArrayList<Experiment> experiments) {
                    experimentList.clear();
                    experimentList.addAll(experiments);
                    experimentAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    private void setExperimentListToSubs() {
        // Fetch data for the list view
        if (currentUser != null) {
            ArrayList<String> expIds = currentUser.getSubscribedExperiments();
            experimentList.clear();
            for (String id : expIds) {
                experimentManager.setOnExperimentFetchListener(id, new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment experiment) {
                        experimentList.add(experiment);
                        experimentAdapter.notifyDataSetChanged();
                    }
                });
            }
            experimentAdapter.notifyDataSetChanged();

        }
    }
}