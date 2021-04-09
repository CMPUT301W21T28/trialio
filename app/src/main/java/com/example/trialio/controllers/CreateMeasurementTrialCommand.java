package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Command object that creates a MeasurementTrial object for the current user. This class inherits from
 * CreateTrialCommand which provides the main execute() function for this command class.
 */
public class CreateMeasurementTrialCommand extends CreateTrialCommand {

    /**
     * The measurement result of the trial to create
     */
    private final double measurement;

    /**
     * The units of the measurement
     */
    private final String unit;

    /**
     * Creates a CreateMeasurementTrialCommand object
     *
     * @param context            The context from which the create trial command was invoked
     * @param isLocationRequired true is location is to be recorded for the Trial, false otherwise
     * @param measurement        the result of the trial to create
     * @param listener           callback for when the Trial is created
     */
    public CreateMeasurementTrialCommand(Context context, boolean isLocationRequired, double measurement, OnResultListener listener) {
        super(context, isLocationRequired, listener);
        this.measurement = measurement;
        this.unit = "unit";
    }

    @Override
    protected void createTrialWithLocation(User user, Context context) {
        Task<android.location.Location> locTask = Location.requestLocation(context);
        if (locTask != null) {
            locTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location loc) {
                    Location location = new Location(loc.getLatitude(), loc.getLongitude());
                    Trial trial = new MeasurementTrial(user.getId(), location, date, measurement, unit);
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
        Trial trial = new MeasurementTrial(user.getId(), location, date, measurement, unit);
        listener.onResult(trial);
    }
}
