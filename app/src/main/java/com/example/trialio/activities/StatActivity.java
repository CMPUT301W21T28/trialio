package com.example.trialio.activities;

// Used Philipp Jahoda's MPAndroidChart library.
// DATE:	2021-03-19
// LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
// SOURCE: 	MPAndroidChart Github repository [https://github.com/PhilJay/MPAndroidChart]
// AUTHOR: 	Philipp Jahoda

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.utils.StatisticsUtility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
        LineChart timePlot = findViewById(R.id.timePlotView);
        TextView graphTitle = findViewById(R.id.plotTitle);

        // display summary statistics of results
        displaySummaryStats(stats);

        // display histogram by default
        displayHistogram(stats, histogram, graphTitle);
        timePlot.setVisibility(View.GONE);

        Button showHistogram = findViewById(R.id.btnHistogram);
        showHistogram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create and display histogram of results
                displayHistogram(stats, histogram, graphTitle);
                timePlot.setVisibility(View.GONE);
                histogram.setVisibility(View.VISIBLE);
            }
        });

        Button showTimePlot = findViewById(R.id.btnTimePlot);
        showTimePlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create and display time plot of trials
                displayTimePlot(stats, timePlot, graphTitle);
                histogram.setVisibility(View.GONE);
                timePlot.setVisibility(View.VISIBLE);
            }
        });
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

        // cannot display histogram if there are no trials
        if(trials.isEmpty()) {
            histogramTitle.setText("No trial results to display");
            return;
        }

        switch((stats.get(0).intValue())) {
            case 1:
                // no graph for COUNT experiments, nothing noteworthy to view
                histogramTitle.setText("No histogram is available for count experiments");

                break;
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

    public void displayTimePlot(ArrayList<Double> stats, LineChart timePlot, TextView timePlotTitle) {
        // Also adapted from Youtube tutorial code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Creating a Simple Bar Graph for your Android Application (part 1/2) [https://www.youtube.com/watch?v=pi1tq-bp7uA&ab_channel=CodingWithMitch]
        // AUTHOR: 	Youtube account: CodingWithMitch

        ArrayList<Entry> TimePlotEntries = new ArrayList<>();
        ArrayList<String> xTitles = new ArrayList<>();
        ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
        LineDataSet timePlotDataSet = new LineDataSet(TimePlotEntries,"Mean");

        // cannot display time plot if there are no trials
        if(trials.isEmpty()) {
            timePlotTitle.setText("No trial results to display");
            return;
        }

        // find the dates of each trial
        ArrayList<Date> dates = new ArrayList<>();
        for(int i=0; i<trials.size(); i++) {
            dates.add(trials.get(i).getDate());
        }

        // sort the dates from furthest in the past to most recent
        Collections.sort(dates);

        // important values that app administrator may want to change
        // TODO: an extra thing would be to allow the experiment owner to set numPoints and max? That would be cool
        int numPoints = 6; // the desired number of points in the time plot
        //long max = new Date().getTime();

        // the following line can also be used for max, to see time plot up until the most recent data point
        // instead of the current date/time
        long max = (dates.get(dates.size()-1)).getTime() + 1000; // + 1 second to ensure max is included

        // find the cutoffs based on number of points
        long[] cutoffs = findCutoffs(dates, numPoints, max);

        // set up point heights array
        double[] pointHeight = new double[numPoints];

        switch((stats.get(0).intValue())) {
            case 1:
                // call helper method for further setup
                pointHeight = setupCountTimePlot(trials, cutoffs, numPoints);

                // display time plot titles
                timePlotTitle.setText(experiment.getSettings().getDescription() +
                        " total count over time\nX-axis: Time\nY-axis: Count");

                break;
            case 2:
                // call helper method for further setup
                pointHeight = setupBinomialTimePlot(trials, cutoffs, numPoints);

                // display time plot titles
                timePlotTitle.setText(experiment.getSettings().getDescription() +
                        " success rate over time\nX-axis: Time\nY-axis: Success rate");

                break;
            case 3:
                // call helper method for further setup
                pointHeight = setupNonNegativeTimePlot(trials, cutoffs, numPoints);

                // display time plot titles
                timePlotTitle.setText(experiment.getSettings().getDescription() +
                        " average non-negative count over time\nX-axis: Time\n" +
                        "Y-axis: Mean non-negative count");

                break;
            case 4:
                // call helper method for further setup
                pointHeight = setupMeasurementTimePlot(trials, cutoffs, numPoints);

                // display time plot titles
                timePlotTitle.setText(experiment.getSettings().getDescription() +
                        " average measurement over time\nX-axis: Time\nY-axis: Mean measurement");
        }

        // add the calculated heights into the time plot
        for(int i=0; i<numPoints; i++) {
            TimePlotEntries.add(new Entry((float)pointHeight[i],i));
        }

        // display the date cutoff for each point in the time plot
        for(int i=0; i<numPoints; i++) {
            xTitles.add("" + new Date(cutoffs[i]));
        }

        // display the time plot and set certain settings
        LineData data = new LineData(xTitles, timePlotDataSet);
        timePlot.setData(data);
        timePlot.setTouchEnabled(true);
        timePlot.setDragEnabled(true);
        timePlot.setScaleEnabled(true);
        timePlot.setDescription(null);
    }

    public long[] findCutoffs(ArrayList<Date> dates, int numPoints, long max) {
        // initialize min time plot x-value, max time plot x-value, and the distance between these two
        long min = (dates.get(0)).getTime();
        long diff = max - min;

        // initialize the cutoffs based on number of points, to sort results based on date
        long[] cutoffs = new long[numPoints];
        for(int i=0; i<numPoints; i++) {
            cutoffs[i] = min + (diff / numPoints) * (i + 1);
        }

        return cutoffs;
    }

    public double[] setupCountTimePlot(ArrayList<Trial> trials, long[] cutoffs, int numPoints) {
        // cast the trials to count type
        ArrayList<CountTrial> countTrials = new ArrayList<>();
        CountTrial count;
        for(int i=0; i<trials.size(); i++) {
            count = (CountTrial) trials.get(i);
            countTrials.add(count);
        }

        // calculate the height of each point, by finding how many results existed at that date
        double[] pointHeight = new double[numPoints];
        int sum = 0;
        for(int i=0; i<cutoffs.length; i++) {
            for(int j=0; j<countTrials.size(); j++) {
                if(countTrials.get(j).getDate().getTime() <= cutoffs[i]) {
                    sum++;
                }
                if(j == trials.size() - 1) {
                    pointHeight[i] = sum;
                    sum = 0;
                }
            }
        }

        return pointHeight;
    }

    public double[] setupBinomialTimePlot(ArrayList<Trial> trials, long[] cutoffs, int numPoints) {
        // cast the trials to binomial type
        ArrayList<BinomialTrial> binomialTrials = new ArrayList<>();
        BinomialTrial binomial;
        for(int i=0; i<trials.size(); i++) {
            binomial = (BinomialTrial) trials.get(i);
            binomialTrials.add(binomial);
        }

        // calculate the height of each point, by finding how many results existed at that date
        double[] pointHeight = new double[numPoints];
        double total = 0;
        double successes = 0;
        for(int i=0; i<cutoffs.length; i++) {
            for(int j=0; j<binomialTrials.size(); j++) {
                if(binomialTrials.get(j).getDate().getTime() <= cutoffs[i]) {
                    if(binomialTrials.get(j).getIsSuccess()) {
                        successes++;
                    }
                    total++;
                }
                if(j == trials.size() - 1) {
                    pointHeight[i] = successes / total;
                    total = 0;
                    successes = 0;
                }
            }
        }

        return pointHeight;
    }

    public double[] setupNonNegativeTimePlot(ArrayList<Trial> trials, long[] cutoffs, int numPoints) {
        // cast the trials to non-negative type
        ArrayList<NonNegativeTrial> nonNegativeTrials = new ArrayList<>();
        NonNegativeTrial nonnegative;
        for(int i=0; i<trials.size(); i++) {
            nonnegative = (NonNegativeTrial) trials.get(i);
            nonNegativeTrials.add(nonnegative);
        }

        // calculate the height of each point, by finding how many results existed at that date
        double[] pointHeight = new double[numPoints];
        int sum = 0;
        for(int i=0; i<cutoffs.length; i++) {
            for(int j=0; j<nonNegativeTrials.size(); j++) {
                if(nonNegativeTrials.get(j).getDate().getTime() <= cutoffs[i]) {
                    pointHeight[i] += nonNegativeTrials.get(j).getNonNegCount();
                    sum++;
                }
                if(j == trials.size() - 1) {
                    pointHeight[i] /= sum;
                    sum = 0;
                }
            }
        }

        return pointHeight;
    }

    public double[] setupMeasurementTimePlot(ArrayList<Trial> trials, long[] cutoffs, int numPoints) {
        // cast the trials to measurement type
        ArrayList<MeasurementTrial> measurementTrials = new ArrayList<>();
        MeasurementTrial measurement;
        for(int i=0; i<trials.size(); i++) {
            measurement = (MeasurementTrial) trials.get(i);
            measurementTrials.add(measurement);
        }

        // calculate the height of each point, by finding how many results existed at that date
        double[] pointHeight = new double[numPoints];
        int sum = 0;
        for(int i=0; i<cutoffs.length; i++) {
            for(int j=0; j<measurementTrials.size(); j++) {
                if(measurementTrials.get(j).getDate().getTime() <= cutoffs[i]) {
                    pointHeight[i] += measurementTrials.get(j).getMeasurement();
                    sum++;
                }
                if(j == trials.size() - 1) {
                    pointHeight[i] /= sum;
                    sum = 0;
                }
            }
        }

        return pointHeight;
    }

}