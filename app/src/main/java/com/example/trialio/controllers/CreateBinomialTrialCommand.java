package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.models.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

public class CreateBinomialTrialCommand {

    private final Context context;
    private final boolean isLocationRequired;
    private final boolean trialResult;
    private final Date date;

    /**
     * The receiving listener that indicates the success of the operation.
     */
    private final OnResultListener listener;

    /**
     * Interface which represent the receiver for the invoker when the command finished execution
     */
    public interface OnResultListener {
        public void onResult(Trial trial);
    }

    /**
     * Creates a ChangeUsernameCommand object.
     */
    public CreateBinomialTrialCommand(Context context, boolean isLocationRequired, boolean trialResult, OnResultListener listener) {
        this.context = context;
        this.isLocationRequired = isLocationRequired;
        this.trialResult = trialResult;
        this.date = new Date();
        this.listener = listener;
    }

    /**
     * Executes the command to create a binomial trial
     */
    public void execute() {
        CurrentUserHandler.getInstance().getCurrentUser(new CurrentUserHandler.OnUserFetchCallback() {
            @Override
            public void onUserFetch(User user) {
                if (isLocationRequired) {
                    createTrialWithLocation(user, context);
                } else {
                    createTrialWithoutLocation(user);
                }
            }
        });
    }

    private void createTrialWithLocation(User user, Context context) {
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

    private void createTrialWithoutLocation(User user) {
        Location location = new Location();
        Trial trial = new BinomialTrial(user.getId(), location, date, trialResult);
        listener.onResult(trial);
    }

}
