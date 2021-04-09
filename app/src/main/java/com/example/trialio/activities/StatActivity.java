package com.example.trialio.activities;

// Used Philipp Jahoda's MPAndroidChart library.
// DATE:	2021-03-19
// LICENSE:	Apache 2.0 [http://www.apache.org/licenses/LICENSE-2.0]
// SOURCE: 	MPAndroidChart Github repository [https://github.com/PhilJay/MPAndroidChart]
// AUTHOR: 	Philipp Jahoda

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trialio.R;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.controllers.ViewUserProfileCommand;
import com.example.trialio.fragments.PlotSettingsFragment;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.HomeButtonUtility;
import com.example.trialio.utils.StatisticsUtility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * This activity allows a user to view stats about an experiment.
 * <p>
 * This activity navigates to no other activities.
 */
public class StatActivity extends AppCompatActivity {
    private final String TAG = "StatActivity";
    public static Context context;

    private Experiment experiment;
    private StatisticsUtility statisticsUtility;
    private ArrayList<Trial> trialList;
    public static int numData = 12; // for histograms and time plots

    /**
     * the On create the takes in the saved instance from the experiment activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_stats);

        // set the context
        context = this;

        // get the experiment that was passed in as an argument
        Bundle bundle = getIntent().getExtras();
        experiment = (Experiment) bundle.getSerializable("experiment_stat");
        trialList = (ArrayList<Trial>) bundle.getSerializable("trialList_stat");

        // create statistics utility
        statisticsUtility = new StatisticsUtility();

        // calculate the relevant stats based on experiment type
        ArrayList<Double> stats = statisticsUtility.getExperimentStatistics(experiment.getTrialManager().getType(), trialList);

        BarChart histogram = findViewById(R.id.histogramView);
        LineChart timePlot = findViewById(R.id.timePlotView);
        TextView graphTitle = findViewById(R.id.plotTitle);

        // display summary statistics of results
        TextView textStats = findViewById(R.id.txtStatsSummaryStatPage);
        statisticsUtility.displaySummaryStats(stats, textStats);

        if(experiment.getTrialManager().getType().equals("COUNT")) {
            // display time plot by default if count experiment
            displayTimePlot(stats, timePlot, graphTitle);
            histogram.setVisibility(View.GONE);
        } else {
            // otherwise display histogram by default
            displayHistogram(stats, histogram, graphTitle);
            timePlot.setVisibility(View.GONE);
        }

        // SET VIEWS
        TextView textDescription = findViewById(R.id.experiment_description);
        TextView textType = findViewById(R.id.experiment_text_type);
        TextView textOwner = findViewById(R.id.experiment_text_owner);
        TextView textStatus = findViewById(R.id.experiment_text_status);
        ImageView experimentLocationImageView = findViewById(R.id.experiment_location);

        // set TextViews
        textDescription.setText("Description: " + experiment.getSettings().getDescription());
        textType.setText("Type: " + experiment.getTrialManager().getType());

        // get the username of the owner from the usermanager
        UserManager userManager = new UserManager();
        userManager.getUserById(experiment.getSettings().getOwnerID(), new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                textOwner.setText(user.getUsername());
            }
        });

        if ( experiment.getTrialManager().getIsOpen() ) {
            textStatus.setText("Open");
        } else {
            textStatus.setText("Closed");
        }

        if (!experiment.getSettings().getGeoLocationRequired()) {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_off_24);
        } else {
            experimentLocationImageView.setImageResource(R.drawable.ic_baseline_location_on_24);
        }

        // TODO: only owner can view
        // set the addTrial button to invisible by default
        //addTrial.setVisibility(View.INVISIBLE);

        // if the experiment is open, set the addTrial button as visible
        //if (experiment.getTrialManager().getIsOpen()) {
        //addTrial.setVisibility(View.VISIBLE);
        //}

        ImageButton previous = findViewById(R.id.btnPreviousGraph);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // will come in handy if we add more time plots
                if(histogram.getVisibility() == View.VISIBLE) {
                    // if histogram is visible, create and display time plot of trials
                    displayTimePlot(stats, timePlot, graphTitle);
                    histogram.setVisibility(View.GONE);
                    timePlot.setVisibility(View.VISIBLE);
                } else {
                    // else create and display histogram of trials
                    displayHistogram(stats, histogram, graphTitle);
                    timePlot.setVisibility(View.GONE);
                    histogram.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton next = findViewById(R.id.btnNextGraph);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // will come in handy if we add more time plots
                if(histogram.getVisibility() == View.VISIBLE) {
                    // if histogram is visible, create and display time plot of trials
                    displayTimePlot(stats, timePlot, graphTitle);
                    histogram.setVisibility(View.GONE);
                    timePlot.setVisibility(View.VISIBLE);
                } else {
                    // else create and display histogram of trials
                    displayHistogram(stats, histogram, graphTitle);
                    timePlot.setVisibility(View.GONE);
                    histogram.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageButton plotSettings = findViewById(R.id.btnPlotSettings);
        plotSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlotSettingsFragment().show(getSupportFragmentManager(), "FORMAT_PLOT");
            }
        });

        // set on click listeners
        setOnClickListeners();
    }

    /**
     * This sets the on click listeners for a Stat Activity
     */
    public void setOnClickListeners() {

        // set the on click listener for viewing the owner's profile
        TextView textOwner = findViewById(R.id.experiment_text_owner);
        textOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create and execute a ViewUserProfileCommand
                ViewUserProfileCommand command = new ViewUserProfileCommand(context, experiment.getSettings().getOwnerID());
                command.execute();
            }
        });

        // set the home button
        HomeButtonUtility.setHomeButtonListener(findViewById(R.id.button_home));
    }

    /**
     * Displays the histogram
     *
     * @param stats the list of relevant summary statistics
     * @param histogram the histogram object
     * @param histogramTitle the TextView to set the title and axis labels for
     */
    public void displayHistogram(ArrayList<Double> stats, BarChart histogram, TextView histogramTitle) {
        // Adapted from Youtube tutorial code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Creating a Simple Bar Graph for your Android Application (part 1/2) [https://www.youtube.com/watch?v=pi1tq-bp7uA&ab_channel=CodingWithMitch]
        // AUTHOR: 	Youtube account: CodingWithMitch

        ArrayList<BarEntry> histogramEntries = new ArrayList<>();
        ArrayList<String> xTitles = new ArrayList<>();

        experiment.getTrialManager().setAllVisibleTrialsFetchListener(new TrialManager.OnAllVisibleTrialsFetchListener() {
            @Override
            public void onAllVisibleTrialsFetch(ArrayList<Trial> trials) {
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

                        return;
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
                        String unit = experiment.getUnit();
                        if(unit == null) {
                            histogramTitle.setText(experiment.getSettings().getDescription() +
                                    " histogram\n" + "X-axis: Measurement\nY-axis: Frequency");
                        } else {
                            histogramTitle.setText(experiment.getSettings().getDescription() +
                                    " histogram\n" + "X-axis: Measurement(" +
                                    unit + ")\nY-axis: Frequency");
                        }
                }

                // display the histogram and set certain settings
                BarData data = new BarData(xTitles, histogramDataSet);
                histogram.setData(data);
                histogram.setTouchEnabled(true);
                histogram.setDragEnabled(true);
                histogram.setScaleEnabled(true);
                histogram.setDescription(null);
                histogram.getAxisLeft().setAxisMinValue(0);
                histogram.getAxisRight().setAxisMinValue(0);
                histogram.invalidate();
            }
        });
    }

    /**
     * Helper method for displaying the histogram
     *
     * @param results the trial results for the histogram
     * @param histogramEntries the object to place new histogram entries into
     * @param xTitles the x-axis label for each vertical section of the histogram
     */
    public void setupHistogram(ArrayList<Double> results, ArrayList<BarEntry> histogramEntries, ArrayList<String> xTitles) {
        // important values that app administrator may want to change
        int numSections = numData; // the desired number of vertical bars in the histogram
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

    /**
     * Displays the time plot
     *
     * @param stats the list of relevant summary statistics
     * @param timePlot the time plot object
     * @param timePlotTitle the TextView to set the title and axis labels for
     */
    public void displayTimePlot(ArrayList<Double> stats, LineChart timePlot, TextView timePlotTitle) {
        // Also adapted from Youtube tutorial code.
        // DATE:	2021-03-19
        // LICENSE:	CC BY 4.0 [https://creativecommons.org/licenses/by/4.0/]
        // SOURCE:  Creating a Simple Bar Graph for your Android Application (part 1/2) [https://www.youtube.com/watch?v=pi1tq-bp7uA&ab_channel=CodingWithMitch]
        // AUTHOR: 	Youtube account: CodingWithMitch

        ArrayList<Entry> TimePlotEntries = new ArrayList<>();
        ArrayList<String> xTitles = new ArrayList<>();

        experiment.getTrialManager().setAllVisibleTrialsFetchListener(new TrialManager.OnAllVisibleTrialsFetchListener() {
            @Override
            public void onAllVisibleTrialsFetch(ArrayList<Trial> trials) {
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
                int numPoints = numData; // the desired number of points in the time plot
                long max = new Date().getTime();

                // the following line can also be used for max, to see time plot up until the most recent data point
                // instead of the current date/time
                // long max = (dates.get(dates.size()-1)).getTime() + 1000; // + 1 second to ensure max is included

                // find the cutoffs based on number of points
                long[] cutoffs = findCutoffs(dates, numPoints, max);

                // set up point heights array
                double[] pointHeight = new double[numPoints];

                switch((stats.get(0).intValue())) {
                    case 1:
                        timePlotDataSet = new LineDataSet(TimePlotEntries,"Count");

                        // call helper method for further setup
                        pointHeight = setupCountTimePlot(trials, cutoffs, numPoints);

                        // display time plot titles
                        timePlotTitle.setText(experiment.getSettings().getDescription() +
                                " total count over time\nX-axis: Time\nY-axis: Count");

                        break;
                    case 2:
                        timePlotDataSet = new LineDataSet(TimePlotEntries,"Success rate");

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
                SimpleDateFormat dateDisplay = new SimpleDateFormat("MM/dd/yy");
                for(int i=0; i<numPoints; i++) {
                    xTitles.add(dateDisplay.format(new Date(cutoffs[i])));
                }

                // display the time plot and set certain settings
                LineData data = new LineData(xTitles, timePlotDataSet);
                timePlot.setData(data);
                timePlot.setTouchEnabled(true);
                timePlot.setDragEnabled(true);
                timePlot.setScaleEnabled(true);
                timePlot.setDescription(null);
                timePlot.invalidate();
            }
        });
    }

    /**
     * Finds the x-axis cutoffs to sort the trial results into
     *
     * @param dates the sorted list of trial dates
     * @param numPoints the number of points to be plotted on the time plot
     * @param max the final date the experiment owner wants to be included in the time plot (must be after most recent trial)
     *
     * @return a list of the cutoffs based on number of points and date range given
     */
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

    /**
     * Helper method for displaying the time plot of a count experiment
     *
     * @param trials the trial results for the time plot
     * @param cutoffs the date cutoffs that the time plot is divided into
     * @param numPoints the number of points to be plotted on the time plot
     *
     * @return a list of the point heights / y-values
     */
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

    /**
     * Helper method for displaying the time plot of a binomial experiment
     *
     * @param trials the trial results for the time plot
     * @param cutoffs the date cutoffs that the time plot is divided into
     * @param numPoints the number of points to be plotted on the time plot
     *
     * @return a list of the point heights / y-values
     */
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

    /**
     * Helper method for displaying the time plot of a non-negative count experiment
     *
     * @param trials the trial results for the time plot
     * @param cutoffs the date cutoffs that the time plot is divided into
     * @param numPoints the number of points to be plotted on the time plot
     *
     * @return a list of the point heights / y-values
     */
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

    /**
     * Helper method for displaying the time plot of a measurement experiment
     *
     * @param trials the trial results for the time plot
     * @param cutoffs the date cutoffs that the time plot is divided into
     * @param numPoints the number of points to be plotted on the time plot
     *
     * @return a list of the point heights / y-values
     */
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