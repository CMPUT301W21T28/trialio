package com.example.trialio.models;

import android.text.TextPaint;

/**
 * Represents a barcode that a user can scan/register
 */
public class Barcode {

    private Experiment experiment;
    private String trialResult;
    private String barcodeID; // change type later


    public Barcode(Experiment experiment, String trialResult, String barcodeID) {
        this.experiment = experiment;
        this.trialResult = trialResult;
        this.barcodeID = barcodeID;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public String getTrialResult() {
        return trialResult;
    }

    public void setTrialResult(String trialResult) {
        this.trialResult = trialResult;
    }

    public String getBarcodeID() {
        return barcodeID;
    }

    public void setBarcodeID(String barcodeID) {
        this.barcodeID = barcodeID;
    }




}
