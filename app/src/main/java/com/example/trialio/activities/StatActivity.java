package com.example.trialio.activities;

// Used Philipp Jahoda's MPAndroidChart library.
// DATE:	2021-03-19
// LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
// SOURCE: 	MPAndroidChart Github repository [https://github.com/PhilJay/MPAndroidChart]
// AUTHOR: 	Philipp Jahoda

import android.os.Bundle;
import android.view.View;
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

public class StatActivity extends AppCompatActivity {
    private final String TAG = "StatActivity";
    private Experiment experiment;
    private StatisticsUtility statisticsUtility;

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

        // calculate the relevant stats based on experiment type
        ArrayList<Double> stats = statisticsUtility.getExperimentStatistics(experiment.getTrialManager().getType(), experiment);

        BarChart histogram = findViewById(R.id.histogramView);
        BarChart timeplot = findViewById(R.id.timePlotView);
        TextView graphTitle = findViewById(R.id.plotTitle);

        // display summary statistics of results
        displaySummaryStats(stats);

        // create and display histogram of results
        displayHistogram(stats, histogram, graphTitle);
        timeplot.setVisibility(View.GONE);

        // create and display time plot of trials
        displayTimePlot(stats, timeplot, graphTitle);
        histogram.setVisibility(View.GONE);
    }

    public void displaySummaryStats(ArrayList<Double> stats) {
        TextView textStats = findViewById(R.id.txtStatsSummaryStatPage);

        // Took rounding code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY-SA 2.5 [https://creativecommons.org/licenses/by-sa/2.5/]
        // SOURCE:  Working with Spinners in Android [https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java]
        // AUTHOR: 	Stack Overflow User: asterite
        switch((stats.get(0).intValue())) {
            case 1:
                textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue());
                break;
            case 2:
                textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                        "\nSuccesses: " + stats.get(2).intValue() + "\nFailures: " +
                        stats.get(3).intValue() + "\nSuccess Rate: " +
                        Math.round(stats.get(4) * 10000d) / 10000d);
                break;
            case 3:
                String modes = Integer.toString(stats.get(6).intValue());
                for(int i=7; i<stats.size(); i++) {
                    modes += ", " + stats.get(i).intValue();
                }

                textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                        "\nMean: " + stats.get(2) + "\nMedian: " +
                        Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                        Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                        Math.round(stats.get(5) * 10000d) / 10000d + "\nMode(s): " + modes);
                break;
            case 4:
                textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                        "\nMean: " + stats.get(2) + "\nMedian: " +
                        Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                        Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                        Math.round(stats.get(5) * 10000d) / 10000d);
        }
    }

    public void displayHistogram(ArrayList<Double> stats, BarChart histogram, TextView histogramTitle) {
        // Adapted from Youtube tutorial code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Creating a Simple Bar Graph for your Android Application (part 1/2) [https://www.youtube.com/watch?v=pi1tq-bp7uA&ab_channel=CodingWithMitch]
        // AUTHOR: 	Youtube account: CodingWithMitch

        ArrayList<BarEntry> histogramEntries = new ArrayList<>();
        ArrayList<String> xTitles = new ArrayList<>();
        ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
        BarDataSet histogramDataSet = new BarDataSet(histogramEntries,"Trials");

        // no graph for COUNT experiments, nothing noteworthy to view
        switch((stats.get(0).intValue())) {
            case 2:
                // add these calculated heights into the histogram
                histogramEntries.add(new BarEntry(stats.get(2).intValue(),0));
                histogramEntries.add(new BarEntry(stats.get(3).intValue(),1));

                // add the titles of both sections
                xTitles.add("Successes");
                xTitles.add("Failures");

                // display histogram titles
                histogramTitle.setText("Successes vs Failures");
                break;
            case 3:
                // find the non-negative count value of each trial
                ArrayList<Double> counts = new ArrayList<>();
                NonNegativeTrial nonnegative;
                for(int i=0; i<trials.size(); i++) {
                    nonnegative = (NonNegativeTrial) trials.get(i);
                    counts.add((double)nonnegative.getNonNegCount());
                }

                // call helper method for further setup
                setupHistogram(counts, histogramEntries, xTitles);

                // display histogram titles
                histogramTitle.setText(experiment.getSettings().getDescription() + " histogram\n" +
                        "X-axis: Non-negative count\nY-axis: Frequency");
                break;
            case 4:
                // find the measurement value of each trial
                ArrayList<Double> measurements = new ArrayList<>();
                MeasurementTrial measurement;
                for(int i=0; i<trials.size(); i++) {
                    measurement = (MeasurementTrial) trials.get(i);
                    measurements.add(measurement.getMeasurement());
                }

                // call helper method for further setup
                setupHistogram(measurements, histogramEntries, xTitles);

                // display histogram titles
                histogramTitle.setText(experiment.getSettings().getDescription() + " histogram\n" +
                        "X-axis: Measurement\nY-axis: Frequency");
        }

        // display the histogram and set certain settings
        BarData data = new BarData(xTitles, histogramDataSet);
        histogram.setData(data);
        histogram.setTouchEnabled(true);
        histogram.setDragEnabled(true);
        histogram.setScaleEnabled(true);
        histogram.setDescription(null);
    }

    public void displayTimePlot(ArrayList<Double> stats, BarChart timeplot, TextView histogramTitle) {
        // Adapted from Youtube tutorial code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Creating a Simple Bar Graph for your Android Application (part 1/2) [https://www.youtube.com/watch?v=pi1tq-bp7uA&ab_channel=CodingWithMitch]
        // AUTHOR: 	Youtube account: CodingWithMitch

        ArrayList<BarEntry> histogramEntries = new ArrayList<>();
        ArrayList<String> xTitles = new ArrayList<>();
        ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
        BarDataSet histogramDataSet = new BarDataSet(histogramEntries,"Trials");

        // no graph for COUNT experiments, nothing noteworthy to view
        switch((stats.get(0).intValue())) {
            case 2:
                // add these calculated heights into the histogram
                histogramEntries.add(new BarEntry(stats.get(2).intValue(),0));
                histogramEntries.add(new BarEntry(stats.get(3).intValue(),1));

                // add the titles of both sections
                xTitles.add("Successes");
                xTitles.add("Failures");

                // display histogram titles
                histogramTitle.setText("Successes vs Failures");
                break;
            case 3:
                // find the non-negative count value of each trial
                ArrayList<Double> counts = new ArrayList<>();
                NonNegativeTrial nonnegative;
                for(int i=0; i<trials.size(); i++) {
                    nonnegative = (NonNegativeTrial) trials.get(i);
                    counts.add((double)nonnegative.getNonNegCount());
                }

                // call helper method for further setup
                setupHistogram(counts, histogramEntries, xTitles);

                // display histogram titles
                histogramTitle.setText(experiment.getSettings().getDescription() + " histogram\n" +
                        "X-axis: Non-negative count\nY-axis: Frequency");
                break;
            case 4:
                // find the measurement value of each trial
                ArrayList<Double> measurements = new ArrayList<>();
                MeasurementTrial measurement;
                for(int i=0; i<trials.size(); i++) {
                    measurement = (MeasurementTrial) trials.get(i);
                    measurements.add(measurement.getMeasurement());
                }

                // call helper method for further setup
                setupHistogram(measurements, histogramEntries, xTitles);

                // display histogram titles
                histogramTitle.setText(experiment.getSettings().getDescription() + " histogram\n" +
                        "X-axis: Measurement\nY-axis: Frequency");
        }

        // display the histogram and set certain settings
        BarData data = new BarData(xTitles, histogramDataSet);
        timeplot.setData(data);
        timeplot.setTouchEnabled(true);
        timeplot.setDragEnabled(true);
        timeplot.setScaleEnabled(true);
        timeplot.setDescription(null);
    }

    public void setupHistogram(ArrayList<Double> results, ArrayList<BarEntry> histogramEntries, ArrayList<String> xTitles) {
        // important values that app administrator may want to change
        // TODO: an extra thing would be to allow the experiment owner to set numSections? That would be cool
        int numSections = 6; // the desired number of vertical bars in the histogram
        double maxBuffer = 1.10; // extra space to the right of maximum result, histogram edge = max * maxBuffer

        // sort the trial results from min to max
        Collections.sort(results);

        // initialize min histogram x-value, max histogram x-value, and the distance between these two
        int min = 0;
        int max = (int)(results.get(results.size()-1) * maxBuffer);
        double diff = max - min;

        // initialize the cutoffs based on number of vertical bars, to sort results into groups
        int[] cutoffs = new int[numSections]; // min is not included
        for(int i=0; i<numSections; i++) {
            cutoffs[i] = (int)Math.round(min + (diff / numSections) * (i + 1));
        }

        // calculate the height of each vertical bar, by finding how many results fit into that bucket
        int[] barHeight = new int[numSections];
        for(int i=0; i<results.size(); i++) {
            for(int j=0; j<cutoffs.length; j++) {
                if(results.get(i) <= cutoffs[j]) {
                    barHeight[j]++;
                    break;
                }
            }
        }

        // add these calculated heights into the histogram
        for(int i=0; i<numSections; i++) {
            histogramEntries.add(new BarEntry(barHeight[i],i));
        }

        // display the min and max values of each section in the histogram
        xTitles.add(min + "-" + cutoffs[0]);
        for(int i=0; i<numSections-1; i++) {
            xTitles.add(cutoffs[i] + "-" + cutoffs[i + 1]);
        }
    }

}