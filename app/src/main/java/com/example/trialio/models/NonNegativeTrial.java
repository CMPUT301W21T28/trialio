package com.example.trialio.models;

import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;

import java.io.Serializable;
import java.util.Date;

public class NonNegativeTrial extends Trial implements Serializable {
    private int nonNegCount;

    public NonNegativeTrial(String experimenterID, Location location, Date date, int nonNegCount) {
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
