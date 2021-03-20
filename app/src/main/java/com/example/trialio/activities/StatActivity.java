package com.example.trialio.activities;

// Used Philipp Jahoda's MPAndroidChart library.
// DATE:	2021-03-19
// LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
// SOURCE: 	MPAndroidChart Github repository [https://github.com/PhilJay/MPAndroidChart]
// AUTHOR: 	Philipp Jahoda

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.utils.StatisticsUtility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This activity allows a user to view stats about an experiment
 */

public class StatActivity extends AppCompatActivity {
    private final String TAG = "StatActivity";
    private Experiment experiment;
    private StatisticsUtility statisticsUtility;
    private BarChart statPlot;

    /**
     * the On create the takes in the saved instance from the experiment activity
     * @param savedInstanceState
     */

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

        // create graph of data

        // Adapted from Youtube tutorial code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Creating a Simple Bar Graph for your Android Application (part 1/2) [https://www.youtube.com/watch?v=pi1tq-bp7uA&ab_channel=CodingWithMitch]
        // AUTHOR: 	Youtube account: CodingWithMitch

        statPlot = (BarChart) findViewById(R.id.statPlot);
        TextView plotText = (TextView) findViewById(R.id.plotTitle);


        // no graph for COUNT experiments, nothing interesting to view
        if(stats.get(0) == 2) {
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(stats.get(2).intValue(),0));
            barEntries.add(new BarEntry(stats.get(3).intValue(),1));
            BarDataSet barDataSet = new BarDataSet(barEntries,"Trials");

            ArrayList<String> xTitles = new ArrayList<>();
            xTitles.add("Successes");
            xTitles.add("Failures");

            BarData data = new BarData(xTitles, barDataSet);
            statPlot.setData(data);

            statPlot.setTouchEnabled(true);
            statPlot.setDragEnabled(true);
            statPlot.setScaleEnabled(true);

            plotText.setText("Successes vs Failures");
        } else if(stats.get(0) == 3) {
            ArrayList<BarEntry> barEntries = new ArrayList<>();

            ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
            ArrayList<Double> counts = new ArrayList<>();
            NonNegativeTrial nonnegative;
            for(int i=0; i<trials.size(); i++) {
                nonnegative = (NonNegativeTrial) trials.get(i);
                counts.add((double)nonnegative.getNonNegCount());
            }
            distributionBarGraph(counts, barEntries);

            plotText.setText(experiment.getSettings().getDescription() + " histogram\n" +
                    "X-axis: Non-negative count\nY-axis: Frequency");

            /*
            Collections.sort(counts);

            int min = 0;
            double max_buffer = 1.10; // extra space to the right of maximum count / measurement
            int max = (int)(counts.get(counts.size()-1) * max_buffer);
            double diff = max - min;
            int numSections = 4;
            int[] cutoffs = new int[numSections]; // min is not included
            for(int i=0; i<numSections; i++) {
                cutoffs[i] = (int)Math.round(min + (diff / numSections) * (i + 1));
            }

            int[] barHeight = new int[numSections];
            //int current_cutoff = 0;
            //int start = 0;
            for(int i=0; i<counts.size(); i++) {
                for(int j=0; j<cutoffs.length; j++) {
                    if(counts.get(i) <= cutoffs[j]) {
                        barHeight[j]++;
                        break;
                    }
                }
            }

            for(int i=0; i<numSections; i++) {
                barEntries.add(new BarEntry(barHeight[i],i));
            }

            BarDataSet barDataSet = new BarDataSet(barEntries,"Trials");

            ArrayList<String> xTitles = new ArrayList<>();
            xTitles.add(min + "-" + cutoffs[0]);

            for(int i=0; i<numSections-1; i++) {
                xTitles.add(cutoffs[i] + "-" + cutoffs[i + 1]);
            }

            BarData data = new BarData(xTitles, barDataSet);
            statPlot.setData(data);

            statPlot.setTouchEnabled(true);
            statPlot.setDragEnabled(true);
            statPlot.setScaleEnabled(true);
             */
        } else if(stats.get(0) == 4) {
            ArrayList<BarEntry> barEntries = new ArrayList<>();

            ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
            ArrayList<Double> measurements = new ArrayList<>();
            MeasurementTrial measurement;
            for(int i=0; i<trials.size(); i++) {
                measurement = (MeasurementTrial) trials.get(i);
                measurements.add(measurement.getMeasurement());
            }
            distributionBarGraph(measurements, barEntries);

            plotText.setText(experiment.getSettings().getDescription() + " histogram\n" +
                    "X-axis: Measurement\nY-axis: Frequency");
        }

        statPlot.setDescription(null);
    }

    public void distributionBarGraph(ArrayList<Double> list, ArrayList<BarEntry> barEntries) {
        Collections.sort(list);

        int min = 0;
        double max_buffer = 1.10; // extra space to the right of maximum count / measurement
        int max = (int)(list.get(list.size()-1) * max_buffer);
        double diff = max - min;
        int numSections = 10;
        int[] cutoffs = new int[numSections]; // min is not included
        for(int i=0; i<numSections; i++) {
            cutoffs[i] = (int)Math.round(min + (diff / numSections) * (i + 1));
        }

        int[] barHeight = new int[numSections];
        //int current_cutoff = 0;
        //int start = 0;
        // TODO: would be nice to have a more efficient algorithm to separate data points into the bars
        for(int i=0; i<list.size(); i++) {
                /*
                if(counts.get(i) > cutoffs[current_cutoff + 1]) {
                    if(counts.get(i) > cutoffs[current_cutoff])
                    barHeight[current_cutoff] = i - start;
                    current_cutoff++;
                    start = i;
                }
                 */
            for(int j=0; j<cutoffs.length; j++) {
                if(list.get(i) <= cutoffs[j]) {
                    barHeight[j]++;
                    break;
                }
            }
        }

        for(int i=0; i<numSections; i++) {
            barEntries.add(new BarEntry(barHeight[i],i));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries,"Trials");

        ArrayList<String> xTitles = new ArrayList<>();
        xTitles.add(min + "-" + cutoffs[0]);

        for(int i=0; i<numSections-1; i++) {
            xTitles.add(cutoffs[i] + "-" + cutoffs[i + 1]);
        }

        BarData data = new BarData(xTitles, barDataSet);
        statPlot.setData(data);

        statPlot.setTouchEnabled(true);
        statPlot.setDragEnabled(true);
        statPlot.setScaleEnabled(true);
    }

}