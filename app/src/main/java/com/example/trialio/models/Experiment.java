package com.example.trialio.models;

import com.example.trialio.controllers.QuestionForum;
import com.example.trialio.controllers.TrialManager;

import java.io.Serializable;
import java.util.ArrayList;

public class Experiment implements Serializable {
    private String experimentID;
    private ExperimentSettings settings;
    private TrialManager trialManager;
    private ArrayList<String> keywords;
    private QuestionForum questionForum;

    public Experiment() { }

    public Experiment(String experimentID, ExperimentSettings settings, String type, boolean isOpen, int minNumOfTrials) {
        this.experimentID = experimentID;
        this.settings = settings;
        this.trialManager = new TrialManager(type, isOpen, minNumOfTrials);
        this.keywords = new ArrayList<String>();  // TODO: how are we seeting keywords? is it just the description?
    }

    public String getExperimentID() {
        return experimentID;
    }

    public void setExperimentID(String experimentID) {
        this.experimentID = experimentID;
    }

    public ExperimentSettings getSettings() {
        return settings;
    }

    public void setSettings(ExperimentSettings settings) {
        this.settings = settings;
    }

    public TrialManager getTrialManager() {
        return trialManager;
    }

    public void setTrialManager(TrialManager trialManager) {
        this.trialManager = trialManager;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public QuestionForum getQuestionForum() {
        return questionForum;
    }

    public void setQuestionForum(QuestionForum questionForum) {
        this.questionForum = questionForum;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "experimentID='" + experimentID + '\'' +
                ", settings=" + settings +
                ", trialManager=" + trialManager +
                ", keywords=" + keywords +
                ", questionForum=" + questionForum +
                '}';
    }
}
