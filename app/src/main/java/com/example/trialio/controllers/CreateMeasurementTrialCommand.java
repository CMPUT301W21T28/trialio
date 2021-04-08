package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.User;

public class CreateMeasurementTrialCommand extends CreateTrialCommand {

    /**
     * Creates a CreateCountTrialCommand object
     * @param context The context from which the create trial command was invoked
     * @param isLocationRequired true is location is to be recorded for the Trial, false otherwise
     * @param listener callback for when the Trial is created
     */
    public CreateMeasurementTrialCommand(Context context, boolean isLocationRequired, double measurement, OnResultListener listener) {
        super(context, isLocationRequired, listener);
    }

    @Override
    protected void createTrialWithLocation(User user, Context context) {

    }

    @Override
    protected void createTrialWithoutLocation(User user) {

    }
}
