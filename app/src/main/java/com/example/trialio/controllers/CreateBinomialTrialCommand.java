package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.models.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

/**
 * Command object that creates a BinomialTrial object for the current user. This class inherits from
 * CreateTrialCommand which provides the main execute() function for this command class.
 */
public class CreateBinomialTrialCommand extends CreateTrialCommand {

    /**
     * The result of a binomial trial
     */
    private final boolean trialResult;

    /**
     * Creates a CreateBinomialTrialCommand
     *
     * @param context            the context from which the create trial command was invoked
     * @param isLocationRequired true is location is to be recorded for the Trial, false otherwise
     * @param trialResult        the result of the trial to create
     * @param listener           callback for when the Trial is created
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
            // Signal that trial could not be created
            listener.onResult(null);
        }
    }

    @Override
    protected void createTrialWithoutLocation(User user) {
        Location location = new Location();
        Trial trial = new BinomialTrial(user.getId(), location, date, trialResult);
        listener.onResult(trial);
    }

}
