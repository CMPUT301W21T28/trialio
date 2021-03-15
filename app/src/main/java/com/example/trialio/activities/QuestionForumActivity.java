package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterExperiment;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForum;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;

import java.util.ArrayList;

public class QuestionForumActivity extends AppCompatActivity {
    private final String TAG = "QuestionForumActivity";
    private final Context context = this;

    private QuestionForum questionForumManager;
    private ArrayList<Question> experimentList;
    private QuestionArrayAdapter experimentAdapter;

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
    }
}
}
