package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;

import javax.annotation.Nullable;

public class ExperimentSettingsActivity extends AppCompatActivity {
    private final String TAG = "ExperimentSettingsActivity";
    private Context context;
    private ExperimentManager experimentManager;
    private Experiment experiment;
    private UserManager userManager;
    private Button unpublishButton;
    private Switch isOpenSwitch;

    @Override
    @Nullable
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_settings);

        context = this;

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // get managers
        experimentManager = new ExperimentManager();
        userManager = new UserManager();

        // get views
        unpublishButton = (Button) findViewById(R.id.button_unpublish_experiment);
        isOpenSwitch = (Switch) findViewById(R.id.switch_isopen_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // upon starting, ensure that we have the most updated experiment
        experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment new_experiment) {
                experiment = new_experiment;
                setFields();
                setOnClickListeners();
            }
        });
    }

    public void setFields() {
        isOpenSwitch.setChecked(experiment.getTrialManager().getIsOpen());
    }

    public void setOnClickListeners() {
        // when the switch is switched onCheckedChanged is called
        isOpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // get the most updated experiment from firebase, update it's isOpen field, and set it back in firebase
                experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment new_experiment) {
                        experiment = new_experiment;
                        experiment.getTrialManager().setIsOpen(b);
                        experimentManager.editExperiment(experiment.getExperimentID(), experiment);
                    }
                });
            }
        });

        // remove the experiment from firebase and return to the home page
        unpublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                experimentManager.unpublishExperiment(experiment.getExperimentID());
                Intent intent = new Intent(context, MainActivity.class);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });
    }
}
