package com.example.trialio;

import java.util.ArrayList;

public class TrialManager {
    private String type;
    private ArrayList<Trial> trials;
    private ArrayList<User> ignored;
    private int minNumOfTrials;
    private Boolean open;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Trial> getTrials() {
        return trials;
    }

    public void setTrials(ArrayList<Trial> trials) {
        this.trials = trials;
    }

    public ArrayList<User> getIgnored() {
        return ignored;
    }

    public void setIgnored(ArrayList<User> ignored) {
        this.ignored = ignored;
    }

    public int getMinNumOfTrials() {
        return minNumOfTrials;
    }

    public void setMinNumOfTrials(int minNumOfTrials) {
        this.minNumOfTrials = minNumOfTrials;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
