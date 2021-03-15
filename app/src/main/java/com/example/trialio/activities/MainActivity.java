package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

    private ExperimentManager experimentManager;
    private ArrayList<Experiment> experimentList;
    private ArrayAdapterExperiment experimentAdapter;

    private UserManager userManager = new UserManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize attributes for the activity
        experimentManager = new ExperimentManager();
        experimentList = new ArrayList<>();
        experimentAdapter = new ArrayAdapterExperiment(this, experimentList);


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
                startActivity(intent);
            }
        });

        // Called when the All button is clicked
        Button allToggleButton = (Button) findViewById(R.id.btnAll);
        allToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "All was clicked");
                setExperimentListToAll();
            }
        });

        // Called when the Owned button is clicked
        Button ownedToggleButton = (Button) findViewById(R.id.btnOwned);
        ownedToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Owned was clicked");
                setExperimentListToOwned();
            }
        });


        // Called when the Subs button is clicked
        Button subsToggleButton = (Button) findViewById(R.id.btnSubs);
        subsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Subs was clicked");

                setExperimentListToSubs();
            }
        });

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
    }

    private void setExperimentListToSubs() {
        Log.d(TAG, "Subs was clicked");

        // Fetch data for the list view
        UserManager userManager = new UserManager();
        userManager.addCurrentUserUpdateListener(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
//                experimentList.clear();
//                experimentList.addAll(user.getSubscribedExperiments());
//                experimentAdapter.notifyDataSetChanged();
            }
        });
    }
}