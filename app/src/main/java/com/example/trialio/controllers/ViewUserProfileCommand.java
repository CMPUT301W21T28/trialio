package com.example.trialio.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.trialio.activities.ViewUserActivity;
import com.example.trialio.models.User;

/**
 * This class follows the Command design pattern and represents the tasks to be
 * performed to view a user profile.
 */
public class ViewUserProfileCommand {
    private final String userID;
    private final Context context;

    public ViewUserProfileCommand(Context context, String userID) {
        this.context = context;
        this.userID = userID;
    }

    /**
     * Executes the command to view the user profile.
     */
    public void execute() {
        UserManager userManager = new UserManager();
        userManager.getUserById(userID, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                Intent intent = new Intent(context.getApplicationContext(), ViewUserActivity.class);

                // pass in experiment as an argument
                Bundle args = new Bundle();
                args.putSerializable("user", user);
                intent.putExtras(args);

                // start an ExperimentActivity
                context.startActivity(intent);
            }
        });
    }
}
