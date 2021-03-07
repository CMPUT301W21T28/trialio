package com.example.trialio;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserManager {
    private final String TAG = "UserManager";

    private static final CollectionReference userCollection = FirebaseFirestore.getInstance().collection("users");

    private User currentUser;
    private ArrayList<User> userList;

    /**
     * Creates a UserManager
     */
    public UserManager() {
        userList = new ArrayList<User>();
        currentUser = new User("TEMP");
        refreshCurrentUser();

        // Set up a listener which is called whenever the collection is updated
        userCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // Update the userList
                assert value != null;
                ArrayList<User> updatedUserList = (ArrayList<User>) value.toObjects(User.class);
                refreshUserList(updatedUserList);

                // Update the current user
                // TODO: this can be set up to listen to only the current user document
                refreshCurrentUser();
            }

        });

    }

    /**
     * Gets the current user profile
     *
     * @return The User associated with the device
     */
    public User getCurrentUser() {
        return currentUser;
    }

    public User generateNewUser(String id) {
        Log.d(TAG, String.format("Generating new User id %s.", id));
        User user = new User(id);
        userCollection.document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, String.format("User %s created successfully.", id));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, String.format("Failed to create User %s.", id));
                    }
                });
        return user;
    }

    public void editUserProfile(String username, User user) {
        //...
    }

    public void deleteUser(String username) {
        //...
    }

//    public Collection<User> getAllUsers() { }

    /**
     * Gets the Firebase Installation ID of the device installation
     *
     * @return The Task containing the FID
     */
    private Task<String> getFID() {
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
     * Update the local userList
     *
     * @param updatedUserList the list of updated User's
     */
    private void refreshUserList(ArrayList<User> updatedUserList) {
        Log.d(TAG, "Fetching users from cloud storage:");
        userList.clear();
        for (User user : updatedUserList) {
            Log.d(TAG, "User=" + user.getId());
            userList.add(user);
        }
    }

    /**
     * Update the local currentUser
     */
    private void refreshCurrentUser() {
        // TODO: this can most likely be refactored with Task#continueWithTask
        Task<String> userIdTask = getFID();         // Get the unique identifier for this device

        userIdTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                // Once FID has been received, query the database for this FID
                 String userId = task.getResult();
//                String userId = "3333";
                assert userId != null;
                Task<DocumentSnapshot> doc = userCollection.document(userId).get();
                doc.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // Once the query result has been received, set currentUser
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if (document.exists()) {
                                currentUser = document.toObject(User.class);
                                Log.d(TAG, "Current User found: " + document.getData());
                            } else {
                                Log.d(TAG, "No Current User found with id=" + userId);
                                currentUser = generateNewUser(userId);
                            }
                        }
                    }
                });
            }
        });

    }

}
