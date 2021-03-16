package com.example.trialio.models;

import java.io.Serializable;
import java.util.Date;

public class CountTrial extends Trial implements Serializable {
    private int count;

    public CountTrial(String experimenterID, Location location, Date date, String data) {
        super(experimenterID, location, date, data);
        this.count = 1;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
