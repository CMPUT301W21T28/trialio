package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterExperiment;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.TrialManager;

public class TrialActivity extends AppCompatActivity {
    private TrialManager trialManager;
    private ExperimentManager experimentManager;
    private ArrayAdapterExperiment experimentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);
        Intent intent = getIntent();
        experimentManager = new ExperimentManager();
        trialManager = new TrialManager();

        ListView trialListView = findViewById(R.id.list_trials);

    }
}