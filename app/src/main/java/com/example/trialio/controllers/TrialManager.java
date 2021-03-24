package com.example.trialio.controllers;

import android.widget.ArrayAdapter;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class manages trials and handles the persistence of trial data.
 */
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

    /**
     * This gets the type of trials controlled by this trial manager.
     * @return Returns the type fo this trial manager.
     */
    public String getType() {
        return type;
    }

    /**
     * This sets the type of trials controlled by this trial manger.
     * @param type The candidate type to set as the trial manager type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This adds a trial to the trial manager.
     * @param trial The candidate trial to add to the trial manager.
     */
    public void addTrial(Trial trial) {
        trials.add(trial);
    }

    /**
     * This gets the trials of the trial manager.
     * @return Returns the trials of the trial manager.
     */
    public ArrayList<Trial> getTrials() {
        return trials;
    }

    /**
     * This sets the trials of the trial manager.
     * @param trials The candidate list of trials to set as the trials of the trial manager.
     */
    public void setTrials(ArrayList<Trial> trials) {
        this.trials = trials;
    }

    /**
     * This gets the list of user ids which are ignored by the trial manager.
     * @return Returns the list of ignored user ids.
     */
    public ArrayList<String> getIgnoredUserIds() {
        return ignoredUserIds;
    }

    /**
     * This sets the list of user ids ignored by the trial manager.
     * @param ignoredUserIds The candidate list of user ids to be ignored by the trial manager.
     */
    public void setIgnoredUserIds(ArrayList<String> ignoredUserIds) {
        this.ignoredUserIds = ignoredUserIds;
    }

    /**
     * This gets the minimum number of trials of the trial manager.
     * @return Returns the minimum number of trials of the trial manager.
     */
    public int getMinNumOfTrials() {
        return minNumOfTrials;
    }

    /**
     * This sets the minimum number of trials of the trial manager.
     * @param minNumOfTrials The candidate integer to set as the minimum number of trails of the trial manager.
     */
    public void setMinNumOfTrials(int minNumOfTrials) {
        this.minNumOfTrials = minNumOfTrials;
    }

    /**
     * This gets a boolean that signifies if the trial manager is open. ie. can have trials added.
     * @return Returns the boolean isOpen of the trial manager.
     */
    public boolean getIsOpen() {
        return isOpen;
    }

    /**
     * This sets the boolean isOpen of the trial manager.
     * @param open The canidate boolean value to set as isOpen of the trail manager.
     */
    public void setIsOpen(boolean open) {
        this.isOpen = open;
    }

    /**
     * This sets the adapter of the trial manager.
     * @param adapter The candidate adapter to set as the adapter of the trial manager.
     */
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
