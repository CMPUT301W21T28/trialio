package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private ExperimentManager experimentManager;
    private ArrayAdapterExperiment experimentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an experiment manager for the activity
        experimentManager = new ExperimentManager();
        experimentAdapter = new ArrayAdapterExperiment(this, experimentManager.getExperimentList());

        // Set up the adapter for the list and experiment manager
        ListView experimentListView = findViewById(R.id.list_experiment);
        experimentListView.setAdapter(experimentAdapter);
        experimentManager.setAdapter(experimentAdapter);

    }
}