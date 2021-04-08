package com.example.trialio.models;

/**
 * Represents a barcode that a user can scan/register
 */
public class Barcode {

    private Experiment experiment;
    private Trial trial;
    private String barcodeID; // change type later


    public Barcode(Experiment experiment, Trial trial, String barcodeID) {
        this.experiment = experiment;
        this.trial = trial;
        this.barcodeID = barcodeID;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }

    public String getBarcodeID() {
        return barcodeID;
    }

    public void setBarcodeID(String barcodeID) {
        this.barcodeID = barcodeID;
    }


}
