package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.Location;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class CreateNonNegativeTrialCommand extends CreateTrialCommand {

    private final int nonNegCount;

    /**
     * Create a CreateNonNegativeTrialCommand cobject
     * @param context The context from which the create trial command was invoked
     * @param isLocationRequired true is location is to be recorded for the Trial, false otherwise
     * @param nonNegCount the result of the trial to create
     * @param listener callback for when the Trial is created
     */
    public CreateNonNegativeTrialCommand(Context context, boolean isLocationRequired, int nonNegCount, OnResultListener listener) {
        super(context, isLocationRequired, listener);
        this.nonNegCount = nonNegCount;
    }

    @Override
    protected void createTrialWithLocation(User user, Context context) {
        Task<android.location.Location> locTask = Location.requestLocation(context);
        if (locTask != null) {
            locTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location loc) {
                    Location location = new Location(loc.getLatitude(), loc.getLongitude());
                    Trial trial = new NonNegativeTrial(user.getId(), location, date, nonNegCount);
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
        Trial trial = new NonNegativeTrial(user.getId(), location, date, nonNegCount);
        listener.onResult(trial);
    }
}
