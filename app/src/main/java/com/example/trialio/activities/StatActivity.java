package com.example.trialio.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.utils.StatisticsUtility;

import java.util.ArrayList;

public class StatActivity extends AppCompatActivity {
    private final String TAG = "StatActivity";
    private Experiment experiment;
    private ExperimentManager experimentManager;
    private final Context context = this;
    private StatisticsUtility statisticsUtility;
    private String selectedType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_stats);

        // Took ActionBar code.
        // DATE:	2020-12-14
        // LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
        // SOURCE: 	Add an up action [https://developer.android.com/training/appbar/up-action]
        // AUTHOR: 	Android Developers [https://developer.android.com/]
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // get the experiment that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_stat");

        // create managers important to this activity
        experimentManager = new ExperimentManager();

        // create statistics utility
        statisticsUtility = new StatisticsUtility();

        // set Stats Summary
        TextView textStats = findViewById(R.id.txtStatsSummaryStatPage);

        ArrayList<Double> stats = statisticsUtility.getExperimentStatistics(experiment.getTrialManager().getType(), experiment);

        // Took rounding code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY-SA 2.5 [https://creativecommons.org/licenses/by-sa/2.5/]
        // SOURCE:  Working with Spinners in Android [https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java]
        // AUTHOR: 	Stack Overflow User: asterite
        if(stats.get(0) == 1) {
            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue());
        } else if(stats.get(0) == 2) {
            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                    "\nSuccesses: " + stats.get(2).intValue() + "\nFailures: " +
                    stats.get(3).intValue() + "\nSuccess Rate: " +
                    Math.round(stats.get(4) * 10000d) / 10000d);
        } else if(stats.get(0) == 3) {
            String modes = Integer.toString(stats.get(6).intValue());
            for(int i=7; i<stats.size(); i++) {
                modes += ", " + stats.get(i).intValue();
            }

            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                    "\nMean: " + stats.get(2) + "\nMedian: " +
                    Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                    Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                    Math.round(stats.get(5) * 10000d) / 10000d + "\nMode(s): " + modes);
        } else if(stats.get(0) == 4) {
            textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                    "\nMean: " + stats.get(2) + "\nMedian: " +
                    Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                    Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                    Math.round(stats.get(5) * 10000d) / 10000d);
        }
    }

}