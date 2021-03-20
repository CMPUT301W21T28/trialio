package com.example.trialio.controllers;

import android.widget.ArrayAdapter;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

import java.io.Serializable;
import java.util.ArrayList;

public class TrialManager implements Serializable {
    private String type;
    private ArrayList<Trial> trials;
    private ArrayAdapter<Experiment> trialAdapter;
    private ArrayList<String> ignoredUserIds;
    private int minNumOfTrials;
    private boolean isOpen;

    public TrialManager() { }

    public TrialManager(String type, boolean isOpen, int minNumOfTrials) {
        this.type = type;
        this.trials = new ArrayList<Trial>();
        this.ignoredUserIds = new ArrayList<String>();
        this.minNumOfTrials = minNumOfTrials;
        this.isOpen = isOpen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addTrial(Trial trial) {
        trials.add(trial);
    }

    public ArrayList<Trial> getTrials() {
        return trials;
    }

    public void setTrials(ArrayList<Trial> trials) {
        this.trials = trials;
    }

    public ArrayList<String> getIgnoredUserIds() {
        return ignoredUserIds;
    }

    public void setIgnoredUserIds(ArrayList<String> ignoredUserIds) {
        this.ignoredUserIds = ignoredUserIds;
    }

    public int getMinNumOfTrials() {
        return minNumOfTrials;
    }

    public void setMinNumOfTrials(int minNumOfTrials) {
        this.minNumOfTrials = minNumOfTrials;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean open) {
        this.isOpen = open;
    }

    public void setAdapter(ArrayAdapter adapter) {
        trialAdapter = adapter;
    }

    /**
     * This finds all of the trials which are not ignored.
     * @return Returns the list of trials completed by users who are not in the ignored list
     */
    public ArrayList<Trial> getVisibleTrials() {
        ArrayList<Trial> visible = new ArrayList<Trial>();
        for (Trial trial : trials) {
            if (!ignoredUserIds.contains(trial.getExperimenterID())) {
                visible.add(trial);
            }
        }
        return visible;
    }
}
