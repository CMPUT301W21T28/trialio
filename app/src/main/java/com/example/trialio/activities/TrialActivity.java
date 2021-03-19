package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.trialio.R;
import com.example.trialio.adapters.ArrayAdapterTrials;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class TrialActivity extends AppCompatActivity {
    private ArrayAdapterTrials trialAdapter;
    private ArrayList<Trial> trialList;
    private final Context context = this;
    private ExperimentManager experimentManager;
    private Experiment experiment;
    private UserManager userManager;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trials);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_trial");

        trialList = experiment.getTrialManager().getTrials();
        trialAdapter = new ArrayAdapterTrials(this, experiment);

        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // Set up the adapter for the list and experiment manager
        ListView trialListView = findViewById(R.id.list_trials);
        trialListView.setAdapter(trialAdapter);

        setFields();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: swap this with an update listener
        // when the experiment is updated, update our local experiment, reset all fields and clear/rebuild the trialList
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {
                experiment = new_experiment;
                setFields();

                trialList.clear();
                trialList.addAll(experiment.getTrialManager().getTrials());
                trialAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setFields() {
        TextView textDescription = findViewById(R.id.txtExperimentDescriptionTrial);
        textDescription.setText("Description: " + experiment.getSettings().getDescription());

        TextView textOwner = findViewById(R.id.txtExperimentOwnerTrial);

        // get the owner's username
        userManager.getUser(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText("Owner: " + user.getUsername());
            }
        });

        TextView textType = findViewById(R.id.txtExperimentTypeTrial);
        textType.setText("Type: " + experiment.getTrialManager().getType());
    }
}
