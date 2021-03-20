package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a trial of experiment type binomial
 */
public class BinomialTrial extends Trial implements Serializable {
    private boolean isSuccess;

    /**
     * Constructor for a BinomialTrial
     *
     * @param experimenterID the unique identifier of an experimenter
     * @param location       the location of a binomial trial
     * @param date           the date of a binomial trial
     * @param isSuccess      whether the binomial trial was a success or failure
     */
    public BinomialTrial(String experimenterID, Location location, Date date, boolean isSuccess) {
        super(experimenterID, location, date);
        this.isSuccess = isSuccess;
    }

    /**
     * Gets whether the binomial trial was a success or failure
     *
     * @return whether the binomial trial was a success or failure
     */
    public boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * Sets whether the binomial trial was a success or failure
     *
     * @param success the success/failure of the binomial trial to be set
     */
    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }
}