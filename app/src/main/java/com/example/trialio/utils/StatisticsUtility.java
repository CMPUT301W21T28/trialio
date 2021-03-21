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
        if(size == 0) {
            stats.add(1.0);
            stats.add(size);
        } else if(type.equals("COUNT")) {
            stats.add(1.0);
            stats.add(size);

            // TODO: do we actually want number of contributors? Just going to leave this for now as
            // TODO: getting the foundation working first is more crucial I think
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

            double median;
            int len = counts.size();
            if(len % 2 == 0) {
                median = (counts.get(len / 2) + counts.get(len / 2 - 1)) / 2;
            } else {
                median = counts.get((int)Math.floor(counts.size() / 2));
            }

            double counts_sum = 0;
            for(int i=0; i<size; i++) {
                counts_sum += counts.get(i);
            }

            double mean = counts_sum / size;

            double stdev = standard_deviation(counts, mean);

            ArrayList<Double> modes = mode(counts);

            // data stored in format of: ID = 3.0, median, mean, standard deviation, variance, mode(s)
            stats.add(3.0);
            stats.add(size);
            stats.add(mean);
            stats.add(median);
            stats.add(stdev);
            stats.add(Math.pow(stdev, 2));
            for(int i=0; i<modes.size(); i++) {
                stats.add(modes.get(i));
            }
        } else if(type.equals("MEASUREMENT")) {
            ArrayList<Double> counts = new ArrayList<>();
            MeasurementTrial measurement;
            for(int i=0; i<size; i++) {
                measurement = (MeasurementTrial) trials.get(i);
                counts.add(measurement.getMeasurement());
            }
            Collections.sort(counts);

            double median;
            int len = counts.size();
            if(len % 2 == 0) {
                median = (counts.get(len / 2) + counts.get(len / 2 - 1)) / 2;
            } else {
                median = counts.get((int)Math.floor(counts.size() / 2));
            }

            double counts_sum = 0;
            for(int i=0; i<size; i++) {
                counts_sum += counts.get(i);
            }

            double mean = counts_sum / size;

            double stdev = standard_deviation(counts, mean);

            // data stored in format of: ID = 4.0, median, mean, standard deviation, variance
            stats.add(4.0);
            stats.add(size);
            stats.add(mean);
            stats.add(median);
            stats.add(stdev);
            stats.add(Math.pow(stdev, 2));
        }

        return stats;
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

}
