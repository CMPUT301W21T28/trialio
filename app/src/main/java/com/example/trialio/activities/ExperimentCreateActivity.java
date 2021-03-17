package com.example.trialio.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

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

        Button addNewExperiment = (Button) findViewById(R.id.btnAddNewExperiment);
        addNewExperiment.setOnClickListener(new View.OnClickListener() {
            private AdapterView.OnItemSelectedListener OnItemSelectedListener;

            @Override
            public void onClick(View v) {
                // TODO: testing that this actually works
                Intent intent = new Intent(context, ExperimentActivity.class);

                EditText editDescription = (EditText) findViewById(R.id.descriptionEditText);
                Spinner selectType = (Spinner) findViewById(R.id.typeDropdown);
                EditText editRegion = (EditText) findViewById(R.id.regionEditText);
                EditText editNumTrials = (EditText) findViewById(R.id.numTrialsEditText);

                Switch geoSwitch = (Switch) findViewById(R.id.geo_switch);
                Switch openSwitch = (Switch) findViewById(R.id.open_switch);

                /*
                <!-- Adapted planets_array code.
// DATE:	2021-03-17
// LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
// SOURCE: 	Spinners [https://developer.android.com/training/appbar/up-action]
// AUTHOR: 	Android Developers [https://developer.android.com/]
-->

                 */
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                        R.array.types_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectType.setAdapter(adapter);

                selectType.setOnItemSelectedListener(OnItemSelectedListener);

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
                String type = selectType.getSelectedItem().toString();

                // prepare open
                boolean open = openSwitch.isChecked();

                // prepare minimum number of trials
                int numTrials = Integer.parseInt(editNumTrials.getText().toString());

                // create Experiment object
                experiment = new Experiment(newID, settings, type, open, numTrials);
                experimentManager.publishExperiment(experiment);

                Bundle args = new Bundle();
                args.putSerializable("experiment", experiment);
                intent.putExtras(args);

                startActivity(intent);
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