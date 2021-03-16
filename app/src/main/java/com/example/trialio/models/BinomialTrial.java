package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

public class BinomialTrial extends Trial implements Serializable {
    private boolean isSuccess;

    public BinomialTrial(String experimenterID, Location location, Date date, String data) {
        super(experimenterID, location, date, data);
        this.isSuccess = isSuccess;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }
}
