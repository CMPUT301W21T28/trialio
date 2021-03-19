package com.example.trialio.utils;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;

import java.util.ArrayList;
import java.util.Collections;

public class StatisticsUtility {

    public ArrayList<Double> getExperimentStatistics (String type, Experiment experiment) {
        ArrayList<Double> stats = new ArrayList<>();
        ArrayList<Trial> trials = experiment.getTrialManager().getTrials();
        double size = trials.size();

        System.out.println(type);
        if(type.equals("COUNT")) {
            stats.add(1.0);
            stats.add(size);

            // TODO: do we actually want number of contributors? Just going to leave this for now as
            // getting the foundation working first is more crucial I think
            /*
            ArrayList<String> experimenters = new ArrayList<String>();
            ArrayList<Integer> num_contributions = new ArrayList<Integer>();
            String experimenterID;
            for(int i=0; i<trials.size(); i++) {
                experimenterID = trials.get(i).getExperimenterID();

            }
            */
        } else if(type.equals("BINOMIAL")) {
            BinomialTrial binomial;

            double success_count = 0;
            for(int i=0; i<size; i++) {
                binomial = (BinomialTrial) trials.get(i);
                if(binomial.getIsSuccess()) {
                    success_count++;
                }
            }

            // data stored in format of: ID = 2.0, total trials, successes, failures, success rate
            stats.add(2.0);
            stats.add(size);
            stats.add(success_count);
            stats.add(size - success_count);
            stats.add(success_count / size);
        } else if(type.equals("NONNEGATIVE")) {
            ArrayList<Double> counts = new ArrayList<>();
            NonNegativeTrial nonnegative;
            for(int i=0; i<size; i++) {
                nonnegative = (NonNegativeTrial) trials.get(i);
                counts.add((double)nonnegative.getNonNegCount());
            }
            Collections.sort(counts);

            double counts_sum = 0;
            for(int i=0; i<size; i++) {
                counts_sum += counts.get(i);
            }

            double mean = counts_sum / size;

            double stdev = standard_deviation(counts, mean);

            // data stored in format of: ID = 3.0, median, mean, standard deviation, variance, mode
            stats.add(3.0);
            stats.add(counts.get((int)Math.floor(counts.size() / 2)));
            stats.add(mean);
            stats.add(stdev);
            stats.add(Math.pow(stdev, 2));
            // TODO: i do not feel like doing mode tonight

        } else if(type.equals("MEASUREMENT")) {
            ArrayList<Double> counts = new ArrayList<>();
            MeasurementTrial measurement;
            for(int i=0; i<size; i++) {
                measurement = (MeasurementTrial) trials.get(i);
                counts.add(measurement.getMeasurement());
            }
            Collections.sort(counts);

            double counts_sum = 0;
            for(int i=0; i<size; i++) {
                counts_sum += counts.get(i);
            }

            double mean = counts_sum / size;

            double stdev = standard_deviation(counts, mean);

            // data stored in format of: ID = 4.0, median, mean, standard deviation, variance, mode
            stats.add(4.0);
            stats.add(counts.get((int)Math.floor(counts.size() / 2)));
            stats.add(mean);
            stats.add(stdev);
            stats.add(Math.pow(stdev, 2));
            // TODO: i do not feel like doing mode tonight
        } else {
            // for testing
            stats.add(5.0);
            stats.add(.444);
        }

        return stats;
    }

    public void getTrialsHistogram (String type, Experiment experiment) {
        //...
    }

    public void getTrialsPlot (String type, Experiment experiment) {
        //...
    }

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

}
