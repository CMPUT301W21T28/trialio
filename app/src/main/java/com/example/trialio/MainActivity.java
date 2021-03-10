package com.example.trialio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "MainActivity";

    private final Context context = this;

    private ExperimentManager experimentManager;
    private ArrayAdapterExperiment experimentAdapter;
    ImageButton editProfile;

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

        editProfile = (ImageButton) findViewById(R.id.editUserBtn);


//        // Some code to test publishing an experiment
//        User u = new User("uid5");
//        Region r = new Region();
//        ExperimentSettings eset = new ExperimentSettings("experimentDescription5", r, u, true);
//        Experiment e = new Experiment(ExperimentManager.getNewExperimentID(), eset, "experimentType5", 5);
//        experimentManager.publishExperiment(e);
    }

    /** Called when the user taps the profile icon on the top right of main activity */
    public void viewUser(View view) {
        Intent intent = new Intent(this, ViewUserActivity.class);
        //User user = UserManager.getCurrentUser();
        //intent.putExtra("CurrentUser", user);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == editProfile.getId()) {
            viewUser(view);
        }
    }
}