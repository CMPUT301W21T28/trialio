package com.example.trialio;

public class Count extends Trial{
    private Integer increment;

    public Count(Integer increment) {
        this.increment = 1;
    }

    public Count(){
        increment += 1;
    }
}
