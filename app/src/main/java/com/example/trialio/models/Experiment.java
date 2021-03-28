package com.example.trialio.models;

import com.example.trialio.controllers.TrialManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents an experiment that users can create or participate in
 */
public class Experiment implements Serializable {

    /**
     * The unique identifier for an experiment
     */
    private String experimentID;

    /**
     * The settings that describe the status and information about an experiment
     */
    private ExperimentSettings settings;

    /**
     * The manager that keeps track of trials for the experiment
     */
    private TrialManager trialManager;

    /**
     * Keywords that can be searched for when searching for experiments
     */
    private ArrayList<String> keywords;

    /**
     * Constructor for an Experiment
     */
    public Experiment() {
        this.experimentID = null;
        this.settings = new ExperimentSettings();
        this.trialManager = new TrialManager();
        this.keywords = new ArrayList<>();
        parseKeywords();
    }

    /**
     * Constructor for an Experiment
     *
     * @param experimentID   the unique identifier of an experiment
     * @param settings       the settings and description of an experiment
     * @param type           the type for an experiment
     * @param isOpen         the open/close status of an experiment
     * @param minNumOfTrials the minimum number of trials required for an experiment
     */
    public Experiment(String experimentID, ExperimentSettings settings, String type, boolean isOpen, int minNumOfTrials) {
        this.experimentID = experimentID;
        this.settings = settings;
        this.trialManager = new TrialManager(type, isOpen, minNumOfTrials);
        this.keywords = new ArrayList<String>();
        parseKeywords();
    }

    /**
     * Parses the experiment information and populates the keyword field. Currently only experiment
     * descriptions are used to form keywords.
     */
    void parseKeywords() {
        this.keywords.clear();

        if (this.settings.getDescription() != null) {
            String[] descKeywords = this.settings.getDescription().split(" ");
            this.keywords.addAll(Arrays.asList(descKeywords));
            ArrayList<String> removes = new ArrayList<>();
            removes.add("");
            removes.add(".");
            removes.add(";");
            removes.add("'");
            this.keywords.removeAll(removes);
        }
    }

    /**
     * Gets the experiment Id
     *
     * @return the experiment id
     */
    public String getExperimentID() {
        return experimentID;
    }

    /**
     * Sets the experiment
     *
     * @param experimentID the experiment id to be set
     */
    public void setExperimentID(String experimentID) {
        this.experimentID = experimentID;
    }

    /**
     * Gets the experiment settings
     *
     * @return the experiment settings
     */
    public ExperimentSettings getSettings() {
        return settings;
    }

    /**
     * Sets the experiment settings
     *
     * @param settings the experiment settings to be set
     */
    public void setSettings(ExperimentSettings settings) {
        this.settings = settings;
        parseKeywords();
    }

    /**
     * Gets the trial manager for the experiment
     *
     * @return the trial manager
     */
    public TrialManager getTrialManager() {
        return trialManager;
    }

    /**
     * Sets the trial manager for the experiment
     *
     * @param trialManager the trial manager to be set
     */
    public void setTrialManager(TrialManager trialManager) {
        this.trialManager = trialManager;
    }

    /**
     * Gets the list of keywords for this experiment
     *
     * @return the keywords
     */
    public ArrayList<String> getKeywords() {
        return keywords;
    }

    /**
     * Sets the list of experiment keywords
     *
     * @param keywords the list of string keywords to be set
     */
    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
        parseKeywords();
    }

    /**
     * Returns the string representation of the experiment
     *
     * @return string representation of the experiment
     */
    @Override
    public String toString() {
        return "Experiment{" +
                "experimentID='" + experimentID + '\'' +
                ", settings=" + settings +
                ", trialManager=" + trialManager +
                ", keywords=" + keywords +
                '}';
    }
}
