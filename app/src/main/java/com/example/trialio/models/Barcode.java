package com.example.trialio.models;

/**
 * Represents a barcode that a user can scan/register
 */
public class Barcode {

    private Experiment experiment;
    private Trial trial;
    private String barcode; // change type later


    public Barcode(Experiment experiment, Trial trial, String barcode) {
        this.experiment = experiment;
        this.trial = trial;
        this.barcode = barcode;
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


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Sets the barcode image
     *
     * @param barcode the barcode image to be set, given an input barcode string
     */
    public void setBarcodeImage(String barcode) {



    }



}
