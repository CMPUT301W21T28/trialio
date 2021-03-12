package com.example.trialio;

import java.io.Serializable;
import java.util.ArrayList;

public class TrialManager implements Serializable {
    private int type;
    private ArrayList<Trial> trials;
    private ArrayList<User> ignoredUsers;
    private int minNumOfTrials;
    private boolean isOpen;

    public TrialManager() { }

    public TrialManager(int type, boolean isOpen, int minNumOfTrials) {
        this.type = type;
        this.trials = new ArrayList<Trial>();
        this.ignoredUsers = new ArrayList<User>();
        this.minNumOfTrials = minNumOfTrials;
        this.isOpen = isOpen;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public ArrayList<User> getIgnoredUsers() {
        return ignoredUsers;
    }

    public void setIgnoredUsers(ArrayList<User> ignoredUsers) {
        this.ignoredUsers = ignoredUsers;
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
}
