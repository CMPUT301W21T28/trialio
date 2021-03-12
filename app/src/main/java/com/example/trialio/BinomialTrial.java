package com.example.trialio;

import java.io.Serializable;
import java.util.Date;

public class BinomialTrial extends Trial implements Serializable {
    private boolean isSuccess;

    public BinomialTrial(String experimenterID, com.example.trialio.Location location, Date date, boolean isSuccess) {
        super(experimenterID, location, date);
        this.isSuccess = isSuccess;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        isSuccess = success;
    }
}
