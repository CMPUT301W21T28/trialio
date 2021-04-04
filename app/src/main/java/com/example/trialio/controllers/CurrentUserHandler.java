package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;

/**
 * CurrentUserHandler handles accesses to the current user for an app instance. This is a utility class that provides
 * methods to get the state of the User that corresponds to the device the app is running on
 */
public class CurrentUserHandler {

    private static final String TAG = "CurrentUserHandler";

    /**
     * This interface represents an action to be taken when a User object is fetched.
     */
    public interface OnUserFetchCallback {

        void onUserFetch(User user);
    }

    /**
     * The singleton instance that is initialized on startup
     */
    private static final CurrentUserHandler instance = new CurrentUserHandler();

    /**
     * The current user for the application instance
     */
    private User currentUser;

    /**
     * Indicates if the handler is ready and currentUser is initialized
     */
    private Boolean ready;

    /**
     * Holds callbacks that can't be executed until CurrentUserHandler is initialized
     */
    private final ArrayList<OnUserFetchCallback> waiting;

    /**
     * Creates an instance of CurrentUserHandler and initializes the currentUser field
     */
    private CurrentUserHandler() {
        // set basic attributes
        currentUser = null;
        ready = false;
        waiting = new ArrayList<>();

        // get the current user identifier
        Task<String> idTask = getFID();

        idTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                // use the fid to fetch a user from the database
                String currentUserId = task.getResult();
                Log.d(TAG, "ID fetched: " + currentUserId);

                UserManager manager = new UserManager();
                manager.getUserById(currentUserId, new UserManager.OnUserFetchListener() {
                    @Override
                    public void onUserFetch(User user) {
                        if (user == null) {
                            currentUser = new User();
                            manager.createNewUser(currentUser, currentUserId);
                        } else {
                            currentUser = user;
                        }
                        listenForUpdates();
                        setReady();
                    }
                });
            }
        });
    }


    /**
     * Gets the singleton instance of CurrentUserHandler
     *
     * @return the singleton instance
     */
    public static CurrentUserHandler getInstance() {
        return instance;
    }

    /**
     * Gets the current User and passes it to invoker through the use of the callback.
     *
     * @param listener the callback that passes the User back to the invoker
     */
    public void getCurrentUser(OnUserFetchCallback listener) {
        if (ready) {
            listener.onUserFetch(currentUser);
        } else {
            waiting.add(listener);
        }
    }

    /**
     * Gets the Firebase Installation ID of the device installation
     *
     * @return The Task containing the FID
     */
    private Task<String> getFID() {
        /* Firebase Developer Docs, "Manage Firebase installations", 2021-03-23, Apache 2.0,
         * https://firebase.google.com/docs/projects/manage-installations#java_2
         */
        Task<String> task = FirebaseInstallations.getInstance().getId();
        task.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // FID was retrieved successfully
                    Log.d(TAG, "Installation ID: " + task.getResult());
                } else {
                    // Failed to retrieve FID
                    Log.e(TAG, "Unable to get Installation ID");
                }
            }
        });
        return task;
    }


    /**
     * Sets the state of the CurrentUserHandler to READY once the CurrentUser has been fetched
     */
    private void setReady() {
        // set status to ready
        ready = true;
        Log.d(TAG, "Current user initialized");

        // take care of all waiting processes
        for (OnUserFetchCallback listener : waiting) {
            listener.onUserFetch(currentUser);
        }
        waiting.clear();
        Log.d(TAG, "All waiting processes handled");
    }

    /**
     * Sets the CurrentUserHandler to listen for all updates to the new user
     */
    private void listenForUpdates() {
        String username = currentUser.getUsername();
        UserManager manager = new UserManager();
        manager.addUserUpdateListener(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                currentUser = user;
                Log.d(TAG, "Current user updated");
            }
        });
    }
}
