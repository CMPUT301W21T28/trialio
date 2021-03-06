package com.example.trialio.controllers;

import android.content.Context;

import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.Date;

/**
 * Command object that creates a Trial object for the current user. This class is inherited to create
 * a Create Trial command object for a particular kind of Trial. This class and all it's subclasses
 * follow the template design pattern. Subclasses of CreateTrialCommand must implement both
 * createTrialWithLocation and createTrialWithoutLocation which specifies how a trial should be
 * created when location is and isn't required, respectively.
 */
public abstract class CreateTrialCommand {

    protected final Context context;
    protected final boolean isLocationRequired;
    protected final Date date;
    protected final OnResultListener listener;

    public interface OnResultListener {
        public void onResult(Trial trial);
    }

    /**
     * Creates a basic (abstract) CreateTrialCommand object
     * @param context The context from which the create trial command was invoked
     * @param isLocationRequired true is location is to be recorded for the Trial, false otherwise
     * @param listener callback for when the Trial is created
     */
    public CreateTrialCommand(Context context, boolean isLocationRequired, OnResultListener listener) {
        this.context = context;
        this.isLocationRequired = isLocationRequired;
        this.listener = listener;
        this.date = new Date();
    }

    /**
     * Executes the command to create a binomial trial
     */
    public final void execute() {
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

    /**
     * Creates a Trial when geolocation information is required
     * @param user The user that created the trial
     * @param context The context fow which the create trial command was invoked
     */
    protected abstract void createTrialWithLocation(User user, Context context);

    /**
     * Creates a Trial when geolocation information is not required
     * @param user The user that created the trial
     */
    protected abstract void createTrialWithoutLocation(User user);

}
