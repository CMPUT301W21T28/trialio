package com.example.trialio;

public class MeasurementTrial extends Trial{
    private Double measurement;
    private String unit;

    public void trialResult(Double m, String u){
        this.measurement = m;
        this.unit = u;
    }

    public void getMeasurementTrial(){
        //return a result
    }
}
