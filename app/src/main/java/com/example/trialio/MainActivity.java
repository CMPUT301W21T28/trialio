package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private final Context context = this;

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


//        // Some code to test publishing an experiment
//        User u = new User("uid5");
//        Region r = new Region();
//        ExperimentSettings eset = new ExperimentSettings("experimentDescription5", r, u, true);
//        Experiment e = new Experiment(ExperimentManager.getNewExperimentID(), eset, "experimentType5", 5);
//        experimentManager.publishExperiment(e);
    }
}