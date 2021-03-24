package com.example.trialio.utils;

import android.widget.TextView;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Utility class for statistics, handles the manipulation and calculation of statistics
 */
public class StatisticsUtility {
    public ArrayList<Double> getExperimentStatistics (String type, Experiment experiment) {
        ArrayList<Double> stats = new ArrayList<>();
        ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
        double size = trials.size();

        System.out.println(type);
        if(size == 0) {
            // if no trials, no statistics are available
            // data stored in format of: ID = 1.0, size = 0
            stats.add(1.0);
            stats.add(size);

            return stats;
        }
        switch(type) {
            case "COUNT":
                // data stored in format of: ID = 1.0, number of trials (also count)
                stats.add(1.0);
                stats.add(size);

                break;
            case "BINOMIAL":
                BinomialTrial binomial;

                double success_count = 0;
                for(int i=0; i<size; i++) {
                    binomial = (BinomialTrial) trials.get(i);
                    if(binomial.getIsSuccess()) {
                        success_count++;
                    }
                }

                // data stored in format of: ID = 2.0, number of trials, successes, failures, success rate
                stats.add(2.0);
                stats.add(size);
                stats.add(success_count);
                stats.add(size - success_count);
                stats.add(success_count / size);

                break;
            case "NONNEGATIVE":
                ArrayList<Double> nonnegative_counts = new ArrayList<>();
                NonNegativeTrial nonnegative;
                for(int i=0; i<size; i++) {
                    nonnegative = (NonNegativeTrial) trials.get(i);
                    nonnegative_counts.add((double)nonnegative.getNonNegCount());
                }

                // data stored in format of: ID = 3.0, number of trials, mean, median, standard deviation, variance, 1st quartile, 3rd quartile, mode(s)
                stats.add(3.0);
                stats = range_stats(stats, nonnegative_counts);

                // add mode(s)
                ArrayList<Double> modes = mode(nonnegative_counts);
                for(int i=0; i<modes.size(); i++) {
                    stats.add(modes.get(i));
                }

                break;
            case "MEASUREMENT":
                ArrayList<Double> measurement_counts = new ArrayList<>();
                MeasurementTrial measurement;
                for(int i=0; i<size; i++) {
                    measurement = (MeasurementTrial) trials.get(i);
                    measurement_counts.add(measurement.getMeasurement());
                }

                // data stored in format of: ID = 4.0, number of trials, mean, median, standard deviation, variance, 1st quartile, 3rd quartile
                stats.add(4.0);
                stats = range_stats(stats, measurement_counts);
        }

        return stats;
    }

    /**
     * Gets the standard deviation of trial values
     *
     * @param stats the list of relevant summary statistics to be filled
     * @param counts the list of non-negative counts or measurements
     *
     * @return the list of relevant summary statistics
     */
    public ArrayList<Double> range_stats(ArrayList<Double> stats, ArrayList<Double> counts) {
        Collections.sort(counts);

        // calculate number of trials
        double size = counts.size();

        // calculate mean
        double counts_sum = 0;
        for(int i=0; i<size; i++) {
            counts_sum += counts.get(i);
        }
        double mean = counts_sum / size;

        // calculate median
        double median;
        if(size % 2 == 0) {
            median = (counts.get((int)size / 2) + counts.get((int)size / 2 - 1)) / 2;
        } else {
            median = counts.get((int)Math.floor(size / 2));
        }

        // calculate standard deviation (variance is standard deviation squared)
        double stdev = standard_deviation(counts, mean);

        // calculate 1st and 3rd quartiles
        double[] quartiles = quartile(counts, (int)size);

        stats.add(size);
        stats.add(mean);
        stats.add(median);
        stats.add(stdev);
        stats.add(Math.pow(stdev, 2));
        stats.add(quartiles[0]);
        stats.add(quartiles[1]);

        return stats;
    }

    /**
     * Gets the standard deviation of trial values
     *
     * @param list the list of trial values
     * @param mean the mean of trial values
     *
     * @return the standard deviation of trial values
     */
    public double standard_deviation(ArrayList<Double> list, double mean) {
        int size = list.size();
        double[] mean_dist = new double[size];
        for(int i=0; i<size; i++) {
            mean_dist[i] = Math.pow(mean - list.get(i), 2);
        }
        double sum = 0;
        for(int i=0; i<size; i++) {
            sum += mean_dist[i];
        }

        return Math.sqrt(sum / size);
    }

    /**
     * Gets the mode(s) of trial values
     *
     * @param list the list of trial values
     *
     * @return the mode(s) of trial values
     */
    public ArrayList<Double> mode(ArrayList<Double> list) {
        ArrayList<Double> modes = new ArrayList<>();
        int size = list.size();
        if(size == 1) {
            modes.add(list.get(0));
            return modes;
        }

        ArrayList<Double> points = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();

        int index = 0;
        int compare;
        for(int i=0; i<size-1; i++) {
            compare = Double.compare(list.get(i), list.get(i + 1));
            if(compare != 0) {
                points.add(list.get(i));
                counts.add(i + 1 - index);
                if(i == size - 2) {
                    points.add(list.get(i + 1));
                    counts.add(1);
                }
                index = i + 1;
            } else if(i == size - 2) {
                points.add(list.get(i));
                counts.add(i + 2 - index);
            }
        }

        int max = 0;
        for(int i=0; i<points.size(); i++) {
            if(counts.get(i) > max) {
                max = counts.get(i);
            }
        }

        for(int i=0; i<points.size(); i++) {
            if(counts.get(i) == max) {
                modes.add(points.get(i));
            }
        }

        Collections.sort(modes);
        return modes;
    }

    /**
     * Gets the first quartile and third quartile of trial values
     *
     * @param counts the list of non-negative counts or measurements
     * @param size the number of trials
     *
     * @return a 2-element array containing the first quartile and third quartile
     */
    public double[] quartile(ArrayList<Double> counts, int size) {
        double q1_median = -1;
        double q3_median = -1;
        if(size >= 4) {
            int q1_len = (int) Math.floor(size / 2);
            if (q1_len % 2 == 0) {
                q1_median = (counts.get(q1_len / 2) + counts.get(q1_len / 2 - 1)) / 2;
            } else {
                q1_median = counts.get((int) Math.floor(q1_len / 2));
            }

            int q3_len = q1_len;
            if (q3_len % 2 == 0) {
                q3_median = (counts.get(size - q3_len / 2) + counts.get(size - (q3_len / 2 + 1))) / 2;
            } else {
                q3_median = counts.get(size - (int) Math.ceil(q3_len / 2));
            }
        }

        double[] q = {q1_median, q3_median};
        return q;
    }

    /**
     * Displays the summary stats, for both ExperimentActivity and StatActivity
     *
     * @param stats the list of relevant summary statistics
     * @param textStats the TextView to place the summary statistics
     */
    public void displaySummaryStats(ArrayList<Double> stats, TextView textStats) {
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
                String firstQuartile = "At least 4 trials required";
                String thirdQuartile = "At least 4 trials required";
                if(stats.get(6) != -1) {
                    firstQuartile = "" + Math.round(stats.get(6) * 10000d) / 10000d;
                    thirdQuartile = "" + Math.round(stats.get(7) * 10000d) / 10000d;
                }

                String modes = Integer.toString(stats.get(8).intValue());
                for(int i=9; i<stats.size(); i++) {
                    modes += ", " + stats.get(i).intValue();
                }

                textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                        "\nMean: " + stats.get(2) + "\nMedian: " +
                        Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                        Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                        Math.round(stats.get(5) * 10000d) / 10000d + "\nFirst quartile: " +
                        firstQuartile + "\nThird quartile: " + thirdQuartile + "\nMode(s): " + modes);
                break;
            case 4:
                textStats.setText("Stats Summary:\nTotal Trials: " + stats.get(1).intValue() +
                        "\nMean: " + stats.get(2) + "\nMedian: " +
                        Math.round(stats.get(3) * 10000d) / 10000d + "\nStandard deviation: " +
                        Math.round(stats.get(4) * 10000d) / 10000d + "\nVariance: " +
                        Math.round(stats.get(5) * 10000d) / 10000d + "\nFirst quartile: " +
                        Math.round(stats.get(6) * 10000d) / 10000d + "\nThird quartile: " +
                        Math.round(stats.get(7) * 10000d) / 10000d);
        }
    }

}
