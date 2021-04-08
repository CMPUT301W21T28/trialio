package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.models.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

public class CreateBinomialTrialCommand extends CreateTrialCommand {

    /**
     * The result of a binomial trial
     */
    private final boolean trialResult;

    /**
     * Creates a CreateBinomialTrialCommand
     */
    public CreateBinomialTrialCommand(Context context, boolean isLocationRequired, boolean trialResult, OnResultListener listener) {
        super(context, isLocationRequired, listener);
        this.trialResult = trialResult;
    }

    @Override
    protected void createTrialWithLocation(User user, Context context) {
        Task<android.location.Location> locTask = Location.requestLocation(context);
        if (locTask != null) {
            locTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location loc) {
                    Location location = new Location(loc.getLatitude(), loc.getLongitude());
                    Trial trial = new BinomialTrial(user.getId(), location, date, trialResult);
                    listener.onResult(trial);
                }
            });
        } else {
            // Failed to get Location
            // TODO: do not upload trial, send message to UI
            createTrialWithoutLocation(user);
        }
    }

    @Override
    protected void createTrialWithoutLocation(User user) {
        Location location = new Location();
        Trial trial = new BinomialTrial(user.getId(), location, date, trialResult);
        listener.onResult(trial);
    }

}
