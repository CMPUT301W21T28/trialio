package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;

/**
 * Command object that creates a CountTrial object for the current user. This class inherits from
 * CreateTrialCommand which provides the main execute() function for this command class.
 */
public class CreateCountTrialCommand extends CreateTrialCommand {

    /**
     * Creates a CreateCountTrialCommand object
     * @param context The context from which the create trial command was invoked
     * @param isLocationRequired true is location is to be recorded for the Trial, false otherwise
     * @param listener callback for when the Trial is created
     */
    public CreateCountTrialCommand(Context context, boolean isLocationRequired, OnResultListener listener) {
        super(context, isLocationRequired, listener);
    }

    @Override
    protected void createTrialWithLocation(User user, Context context) {
        Task<android.location.Location> locTask = Location.requestLocation(context);
        if (locTask != null) {
            locTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location loc) {
                    Location location = new Location(loc.getLatitude(), loc.getLongitude());
                    Trial trial = new CountTrial(user.getId(), location, date);
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
        Trial trial = new CountTrial(user.getId(), location, date);
        listener.onResult(trial);
    }
}
