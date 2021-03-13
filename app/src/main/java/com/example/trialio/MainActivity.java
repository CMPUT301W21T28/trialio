package com.example.trialio;

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

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private final Context context = this;

    private ExperimentManager experimentManager;
    private ArrayAdapterExperiment experimentAdapter;
    private ImageButton editProfile;
    private Button activeToggleButton;

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

        // Get the button for the active list
        Button button = (Button)findViewById(R.id.btnAll);

        experimentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, ExperimentActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("experiment", experimentManager.getExperimentList().get(i));
                intent.putExtras(args);

                // start an ExperimentActivity
                startActivity(intent);
            }
        });

        editProfile = (ImageButton) findViewById(R.id.editUserBtn);

        /** Called when the user taps the profile icon on the top right of main activity */
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewUserActivity.class);
                startActivity(intent);
            }
        });

        // Called when the Subs button is clicked
        Button subsToggleButton = (Button) findViewById(R.id.btnSubs);
        subsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExperimentListToSubs();
            }
        });
    }

    private void setExperimentListToSubs() {
        Log.d(TAG, "Subs was clicked");
    }
}