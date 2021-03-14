package com.example.trialio.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterTrials;
import com.example.trialio.controllers.TrialManager;

import javax.annotation.Nullable;

public class TrialActivity extends AppCompatActivity {
    private TrialManager trialManager;
    private ArrayAdapterTrials trialAdapter;
    private final Context context = this;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);

        trialManager = new TrialManager();
        trialAdapter = new ArrayAdapterTrials(this, trialManager.getTrials());

        // Set up the adapter for the list and experiment manager
        ListView trialListView = findViewById(R.id.list_trials);
        trialListView.setAdapter(trialAdapter);
        trialManager.setAdapter(trialAdapter);



        // receive experiment information from ExperimentActivity
        Intent intent = getIntent();

        TextView textDescription = findViewById(R.id.txtExperimentDescriptionTrial);
        textDescription.setText("Description: " + intent.getStringExtra("Description"));

        TextView textOwner = findViewById(R.id.txtExperimentOwnerTrial);
        textOwner.setText("Owner: " + intent.getStringExtra("Owner"));

        TextView textType = findViewById(R.id.txtExperimentTypeTrial);
        textType.setText("Type: " + intent.getStringExtra("Type"));

    }
}





















