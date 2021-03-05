package com.example.trialio;

public class BinomialTrial extends Trial{
    private Boolean success;

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean GetResult(){
        return success;
    }
}
