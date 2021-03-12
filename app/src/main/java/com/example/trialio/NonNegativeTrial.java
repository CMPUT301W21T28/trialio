package com.example.trialio;

import java.io.Serializable;
import java.util.Date;

public class NonNegativeTrial extends Trial implements Serializable {
    private int nonNegCount;

    public NonNegativeTrial(String experimenterID, com.example.trialio.Location location, Date date, int nonNegCount) {
        super(experimenterID, location, date);
        this.nonNegCount = nonNegCount;
    }

    public int getNonNegCount() {
        return nonNegCount;
    }

    public void setNonNegCount(int nonNegCount) {
        this.nonNegCount = nonNegCount;
    }
}
