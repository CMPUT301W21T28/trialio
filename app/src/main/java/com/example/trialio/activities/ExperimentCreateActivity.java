package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.R;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.models.Region;
import com.example.trialio.models.User;

public class ExperimentCreateActivity extends AppCompatActivity {
    private final String TAG = "ExperimentCreateActivity";
    private Experiment experiment;
    private ExperimentManager experimentManager;
    private final Context context = this;
    private String selectedType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_experiment);

        experimentManager = new ExperimentManager();

        // Took ActionBar code.
        // DATE:	2020-12-14
        // LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
        // SOURCE: 	Add an up action [https://developer.android.com/training/appbar/up-action]
        // AUTHOR: 	Android Developers [https://developer.android.com/]
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Spinner selectType = (Spinner) findViewById(R.id.typeDropdown);

        // Adapted from class/division code.
        // DATE:	2021-03-18
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Working with Spinners in Android [https://www.studytonight.com/android/spinner-example-in-android#]
        // AUTHOR: 	Studytonight tutorial developers
        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        Button addNewExperiment = (Button) findViewById(R.id.btnAddNewExperiment);
        addNewExperiment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: testing that this actually works
                Intent intent = new Intent(context, ExperimentActivity.class);

                EditText editDescription = (EditText) findViewById(R.id.descriptionEditText);
                EditText editRegion = (EditText) findViewById(R.id.regionEditText);
                EditText editNumTrials = (EditText) findViewById(R.id.numTrialsEditText);

                Switch geoSwitch = (Switch) findViewById(R.id.geo_switch);
                Switch openSwitch = (Switch) findViewById(R.id.open_switch);

                //----------------------------------
                // prepare ExperimentSettings object
                //----------------------------------

                // prepare description
                String description = editDescription.getText().toString();

                // prepare region
                Region region = new Region();
                region.setDescription(editRegion.getText().toString());

                // prepare owner
                User owner = new User();

                // prepare geo
                boolean geo = geoSwitch.isChecked();

                //--------------------------
                // prepare Experiment object
                //--------------------------

                // generate ID
                String newID = experimentManager.getNewExperimentID();

                // prepare experiment settings
                ExperimentSettings settings = new ExperimentSettings(description, region, owner, geo);

                // prepare type
                String type = selectedType;

                // prepare open
                boolean open = openSwitch.isChecked();

                // prepare minimum number of trials
                String int_popup = "Please enter a positive integer for minimum number of trials";
                try {
                    int numTrials = Integer.parseInt(editNumTrials.getText().toString());

                    if (numTrials < 1) {
                        Toast.makeText(context, int_popup, Toast.LENGTH_LONG).show();
                    } else {
                        // create Experiment object
                        experiment = new Experiment(newID, settings, type, open, numTrials);
                        experimentManager.publishExperiment(experiment);

                        Bundle args = new Bundle();
                        args.putSerializable("experiment", experiment);
                        intent.putExtras(args);

                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, int_popup, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelNewExperiment = (Button) findViewById(R.id.btnCancelNewExperiment);
        cancelNewExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });


    }

}