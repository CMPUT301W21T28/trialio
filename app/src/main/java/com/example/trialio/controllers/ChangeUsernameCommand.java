package com.example.trialio.controllers;

import androidx.annotation.NonNull;

import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ChangeUsernameCommand {

    private final User user;
    private final String requestedUsername;
    private final OnResultListener listener;

    public interface OnResultListener {
        public void onResult(Boolean isSuccess);
    }

    /**
     * Creates a ChangeUsernameCommand object.
     *
     * @param user              the User for which to change their username
     * @param requestedUsername the requested new username
     */
    public ChangeUsernameCommand(User user, String requestedUsername, OnResultListener listener) {
        this.user = user;
        this.requestedUsername = requestedUsername;
        this.listener = listener;
    }

    /**
     * Executes the command to change a username
     */
    public void execute() {
        UserManager userManager = new UserManager();
        userManager.transferUsername(user, requestedUsername).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Username change was successful
                listener.onResult(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Username change failed
                listener.onResult(false);
            }
        });
    }
}
