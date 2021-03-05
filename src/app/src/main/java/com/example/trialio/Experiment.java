package com.example.trialio;

import java.util.ArrayList;

public class Experiment {
    private String experimentID;
    private ExperimentSettings settings;
    private TrialManager trialManager;
    private ArrayList<String> keywords;
    private QuestionForum questionForum;


    public Experiment(ExperimentSettings settings, String type, int minNumOfTrials) {
        this.settings = settings;
        // .... finish me ...
    }

}
