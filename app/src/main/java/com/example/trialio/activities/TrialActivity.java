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
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.models.Experiment;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class TrialActivity extends AppCompatActivity {
    private TrialManager trialManager;
    private ArrayAdapterTrials trialAdapter;
    private final Context context = this;
    private ExperimentManager experimentManager;
    private Experiment experiment;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_trial");

        //trialManager = new TrialManager();
        trialManager = experiment.getTrialManager();
        trialAdapter = new ArrayAdapterTrials(this, trialManager.getTrials());


        // Set up the adapter for the list and experiment manager
        ListView trialListView = findViewById(R.id.list_trials);
        trialListView.setAdapter(trialAdapter);
        trialManager.setAdapter(trialAdapter);


        TextView textDescription = findViewById(R.id.txtExperimentDescriptionTrial);
        textDescription.setText("Description: " + experiment.getSettings().getDescription());

        TextView textOwner = findViewById(R.id.txtExperimentOwnerTrial);
        textOwner.setText("Owner: " + experiment.getSettings().getOwner());

        TextView textType = findViewById(R.id.txtExperimentTypeTrial);
        textType.setText("Type: " + experiment.getTrialManager().getType());
    }
    //@Override
    //protected void onStart() {
    //    super.onStart();
    //
    //    // Fetch data for the list view
    //    experimentManager.setOnExperimentFetchCallback(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
    //        @Override
    //        public void onExperimentFetch(Experiment experiment) {
    //            this.experiment = experiment;
    //        }
    //
    //        @Override
    //        public void onManyExperimentsFetch(ArrayList<Experiment> experiments) {
    //            experimentList.clear();
    //            experimentList.addAll(experiments);
    //            experimentAdapter.notifyDataSetChanged();
    //        }
    //    });
    //}
}





















