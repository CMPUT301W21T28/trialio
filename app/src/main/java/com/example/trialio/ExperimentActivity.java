package com.example.trialio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExperimentActivity extends AppCompatActivity {
    private Experiment experiment;
    private int trialType;
    private ExperimentManager experimentManager;
    private TrialManager trialManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        experimentManager = new ExperimentManager();
        trialManager = new TrialManager();
      
        // Took ActionBar code.
        // DATE:	2020-12-14
        // LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
        // SOURCE: 	Add an up action [https://developer.android.com/training/appbar/up-action]
        // AUTHOR: 	Android Developers [https://developer.android.com/]
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the experiment that was passed in
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment");

        // initialize all of the fields in the activity
        setFields();

        // store the experiment type to trialType variable
        trialType = experiment.getTrialManager().getType();


        Button addBinomial = (Button) findViewById(R.id.btnAddTrial);
        addBinomial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBinomialTrial();
            }
        });



        /**
         * show fragments that match the experiment type
         */
        //if (trialType == "Binomial") {
        //    Button addBinomial = (Button) findViewById(R.id.btnAddTrial);
        //    addBinomial.setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            addBinomialTrial();
        //        }
        //    });
        //}
        //}else if (trialType == "Measurement"){
        //    Button addMeasurement = (Button) findViewById(R.id.btnAddTrial);
        //    addMeasurement.setOnClickListener(new View.OnClickListener(){
        //        @Override
        //        public void onClick(View v) {
        //            addMeasurementTrial();
        //        }
        //    });
        //}else if (trialType == "NonNegativeCount"){
        //    Button addNonNegativeCount = (Button) findViewById(R.id.btnAddTrial);
        //    addNonNegativeCount.setOnClickListener(new View.OnClickListener(){
        //        @Override
        //        public void onClick(View v) {
        //            addNonNegativeCountTrial();
        //        }
        //    });
        //}else if (trialType == "Count"){
        //    Button addCount = (Button) findViewById(R.id.btnAddTrial);
        //    addCount.setOnClickListener(new View.OnClickListener(){
        //        @Override
        //        public void onClick(View v) {
        //            addCountTrial();
        //        }
        //    });
        //};

        /**
         * function for view trials
         */
        //Button showTrials = (Button) findViewById(R.id.btnTrials);
        //showTrials.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        showTrials();
        //    }
        //});
    }


    /**
     * addBinomialTrial
     */
    public void addBinomialTrial (){
        BinomialTrial newTrial = new BinomialTrial();
        newTrial.show(getSupportFragmentManager(), "addBinomial");
    }

    /**
     * This initializes all of the fields of the activity with data from the experiment
     */
    public void setFields(){
        // get TextViews
        TextView textDescription = findViewById(R.id.txtExperimentDescription);
        TextView textType = findViewById(R.id.txtExperimentType);
        TextView textRegion = findViewById(R.id.txtExperimentRegion);
        TextView textOwner = findViewById(R.id.txtExperimentOwner);
        TextView textStatus = findViewById(R.id.txtExperimentStatus);
        TextView textMinTrials = findViewById(R.id.txtExperimentMinTrials);

        // set TextViews
        textDescription.setText("Description: " + experiment.getSettings().getDescription());
        textType.setText("Type: " + experiment.getTrialManager().getType());
        textRegion.setText("Region: " + experiment.getSettings().getRegion().getDescription());
        textOwner.setText("Owner: " + experiment.getSettings().getOwner());
        textStatus.setText("Open: " + (experiment.getTrialManager().getIsOpen() ? "yes" : "no"));
        textMinTrials.setText("Minimum number of trials: " + experiment.getTrialManager().getMinNumOfTrials());
    }

    /**receives result from trial fragment, create new trial and store it to its experiment's trial manager
     *
     * @param s
     *  s is either True or False for binomial trials
     */
    public void fetchResult (String s) {
        ExperimentManager experimentManager = new ExperimentManager();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formatted = fd.format(c);

        String loc = "Edmonton TESTING";

        Trial newTrial = new Trial();
        // TODO: change the data type to Location and Date
        //newTrial.date = (formatted);
        //newTrial.location = loc;
        newTrial.data = s;
        experiment.getTrialManager().addTrial(newTrial);

        experimentManager.editExperiment(experiment.getExperimentID(), experiment);
    }
}