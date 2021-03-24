package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.models.User;
import com.example.trialio.models.UserContactInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages Users in the system and is responsible for the persistence of User data. This class
 * is used to perform create, read, update and delete functionality on Users which are to be
 * maintained by the system. This class communicates with the Firebase database where User data is
 * maintained.
 */
public class UserManager {
    private static final String TAG = "UserManager";
    private static final String COLLECTION_PATH = "users-v2";
    private final CollectionReference userCollection;

    private static String fid;

    private static final String USERNAME_FIELD = "username";
    private static final String DEVICE_ID_FIELD = "device_id";
    private static final String EMAIL_FIELD = "email";
    private static final String PHONE_FIELD = "phone";
    private static final String SUBBED_EXPERIMENTS_FIELD = "subscribed_experiments";

    /*
     * This interface design was adapted from Android callbacks such as OnCLickListener
     * Android Developer Docs, "View.OnClickListener", 2020-09-30, Apache 2.0,
     * https://developer.android.com/reference/android/view/View.OnClickListener
     */

    /**
     * This interface represents an action to be taken when a User document is fetched from the
     * Firestore database.
     */
    public interface OnUserFetchListener {

        /**
         * This method will be called when the User document is fetched from the database.
         *
         * @param user The user that has been fetched in the database
         */
        public void onUserFetch(User user);

    }

    /**
     * This interface represents an action to be taken when a multiple User documents have been
     * fetched from the Firestore database.
     */
    public interface OnManyUsersFetchListener {

        /**
         * This method will be called when the User document is fetched from the database.
         *
         * @param userList The user that has been updated in the database
         */
        public void onManyUsersFetch(ArrayList<User> userList);

    }

    /**
     * Creates a UserManager
     */
    public UserManager() {
        userCollection = FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
    }

    /**
     * Creates a UserManager
     *
     * @param collection The collection in which to store User documents
     */
    public UserManager(String collection) {
        userCollection = FirebaseFirestore.getInstance().collection(collection);
    }


    /**
     * Creates a new User in the system.
     *
     * @param deviceId The device id of new User to create in the database
     * @return The User that was created.
     * <p>
     * Note that this User object will not track changes to the
     * User document. If you need to receive all database updates for the User, fetch the
     * User using UserManager#addUserUpdateListener()
     */
    public User createNewUser(String deviceId) {
        Log.d(TAG, String.format("Generating new User with fid=%s.", deviceId));
        User user = new User();
        String username = userCollection.document().getId();
        user.setUsername(username);
        user.setId(deviceId);

        /* Chaining tasks
         * Google Play Services Developer Docs, "Chaining", data, Apache 2.0,
         * https://developers.google.com/android/guides/tasks#chaining
         */

        // Check that device id does not already exist in the system
        Task<QuerySnapshot> queryRef = userCollection.whereEqualTo(DEVICE_ID_FIELD, user.getId()).get();
        queryRef.continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.getResult().size() == 0) {
                    // Proceed with creating new user document
                    Map<String, Object> compressUser = compressUser(user);
                    return userCollection.document(username).set(compressUser);
                } else {
                    // User for this device already exists
                    Log.d(TAG, "Unable to create User: User with fid " + deviceId + " already exists");
                    return null;
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, String.format("User created successfully for device %s.", deviceId));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, String.format("Failed to create User for device %s.", deviceId));
            }
        });

        return user;
    }

    /**
     * Gets the User for the current device from the database. If the current User does not exist
     * in the system already, a new User document is created.
     *
     * @param listener the callback to be called when the User object is retrieved
     */
    public void getCurrentUser(OnUserFetchListener listener) {
        if (fid == null) {
            Task<String> userIdTask = getFID();
            userIdTask.addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    // Once FID has been received, fetch from database with userId=FID
                    fid = task.getResult();
                    fetchUserByDevice(fid, listener);
                }
            });
        } else {
            fetchUserByDevice(fid, listener);
        }
    }

    /**
     * Gets a User from the database. If the current User does not exist in the
     * system already, a new User document is created.
     *
     * @param username the username of the User to retrieve
     * @param listener the callback to be called when the User is fetched
     */
    public void getUser(String username, OnUserFetchListener listener) {
        fetchUserByUsername(username, listener);
    }

    /**
     * Gets a User from the database and calls the specified callback function with the User given
     * as a parameter. If a user does not exist for the given device id, a new user is created.
     *
     * @param deviceId the fid of the User to fetch
     * @param listener the callback listener to be called when the User is fetched
     */
    private void fetchUserByDevice(String deviceId, OnUserFetchListener listener) {
        userCollection.whereEqualTo(DEVICE_ID_FIELD, deviceId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<DocumentSnapshot> docs = (ArrayList<DocumentSnapshot>) task.getResult().getDocuments();
                        if (docs.size() == 0) {
                            // User does not exist, create a new user
                            User newUser = createNewUser(deviceId);
                            listener.onUserFetch(newUser);
                        } else if (docs.size() == 1) {
                            // Successfully fetched the User
                            User user = extractUser(docs.get(0));
                            listener.onUserFetch(user);
                        } else {
                            // Exception, should not be more than 1 user with same id
                            Log.e(TAG, "Found more than one user for device " + deviceId);
                            User user = extractUser(docs.get(0));
                            listener.onUserFetch(user);
                        }
                    }
                });
    }

    /**
     * Gets a User from the database and calls the specified callback function with the User given
     * as a parameter. If the User is not found, null is passed into the callback function.
     *
     * @param username the username of the User to fetch
     * @param listener the callback listener to be called when the User is fetched
     */
    private void fetchUserByUsername(String username, OnUserFetchListener listener) {
        userCollection.document(username).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                User user = extractUser(doc);
                                listener.onUserFetch(user);
                                Log.d(TAG, "User " + username + " fetched successfully.");
                            } else {
                                Log.d(TAG, "No user found with username " + username);
                                listener.onUserFetch(null);
                            }
                        } else {
                            Log.d(TAG, "User fetch failed with " + task.getException());
                        }
                    }
                });
    }

    /**
     * Sets a function to be called when a User is fetched.
     *
     * @param userId   the id of the User to fetch
     * @param listener the callback with the function to call
     */
    private void setUserFetchListener(String userId, OnUserFetchListener listener) {
        userCollection.document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            assert doc != null;
                            if (doc.exists()) {
                                User user = extractUser(doc);
                                listener.onUserFetch(user);
                                Log.d(TAG, "User " + userId + " fetched successfully.");
                            } else {
                                Log.d(TAG, "No user found with id " + userId);
                                User user = createNewUser(userId);
                                listener.onUserFetch(user);
                            }
                        } else {
                            Log.d(TAG, "User fetch failed with " + task.getException());
                        }
                    }
                });
    }

    /**
     * Add a listener to the current User document that will listen for updates to the User data.
     * This method is used to fetch User data from the database and continue to fetch real-time
     * data for the current User.
     *
     * <pre>
     * UserManager manager = new UserManager();
     * manager.addCurrentUserUpdateListener(new UserManager.OnUserUpdateListener() {
     *     &#64;Override
     *     public void onUserUpdate(User user) {
     *         // Do something with the User every time the database is updated
     *     }
     * });
     * </pre>
     *
     * @param listener the listener to be called when the User document is fetched
     */
    public void addCurrentUserUpdateListener(OnUserFetchListener listener) {
        if (fid == null) {
            Task<String> userIdTask = getFID();
            userIdTask.addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    // Once FID has been received, fetch from database with userId=FID
                    String userId = task.getResult();
                    // String userId = "3333"
                    fid = userId;
                    assert userId != null;
                    setOnUpdateFetchListener(userId, listener);
                }
            });
        } else {
            setOnUpdateFetchListener(fid, listener);
        }
    }

    /**
     * Add a listener to a User document that will listen for updates to the User data. This method
     * is used to fetch User data from the database and continue to fetch real-time data for a
     * User.
     *
     * <pre>
     * UserManager manager = new UserManager();
     * manager.addUserUpdateListener(userId, new UserManager.OnUserUpdateListener() {
     *     &#64;Override
     *     public void onUserUpdate(User user) {
     *         // Do something with the User every time the database is updated
     *     }
     * });
     * </pre>
     *
     * @param userId   the id of the User to be fetched
     * @param listener the listener to be called when the User document is fetched
     */
    public void addUserUpdateListener(String userId, OnUserFetchListener listener) {
        setOnUpdateFetchListener(userId, listener);
    }

    /**
     * Add a listener to a all User document that will listen for updates any User. This method is
     * used to fetch User data for all users from the database when any User document in the
     * database is updated.
     *
     * <pre>
     * UserManager manager = new UserManager();
     * manager.addAllUsersUpdateListener(new UserManager.OnAllUserUpdateListener() {
     *     &#64;Override
     *     public void onALlUsersUpdate(User user) {
     *         // Do something with the User every time the database is updated
     *     }
     * });
     * </pre>
     *
     * @param listener the listener to be called when the list of Users is fetched
     */
    public void addAllUserUpdateListener(UserManager.OnManyUsersFetchListener listener) {
        setListenerToCollection(listener);
    }


    /**
     * Updates a User in the system.
     *
     * @param user the User to update
     */
    public void updateUser(User user) {
        String id = user.getId();
        Map<String, Object> cu = compressUser(user);
        userCollection.document(id)
                .set(cu)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, String.format("User %s updated successfully", id));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, String.format("Failed to update User %s", id));
                    }
                });
    }

    /**
     * Deletes a User from the system.
     *
     * @param user the user to be deleted
     */
    public void deleteUser(User user) {
        String id = user.getId();
        userCollection.document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, String.format("User %s deleted successfully", id));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, String.format("Failed to delete User %s", id));
                    }
                });
    }


    /**
     * Sets a listener to a the user identified by userId. This function sets up a listener so that
     * listener.onUserUpdate() is called whenever the User document is updated in the database.
     *
     * @param userId   the id of the User to attach the listener to
     * @param listener the listener with the callback function to be called when the User is updated
     */
    private void setOnUpdateFetchListener(String userId, OnUserFetchListener listener) {
        userCollection.document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User currentUser;
                if (value.exists()) {
                    Log.d(TAG, "User fetched from database: " + value.getData());
                    currentUser = extractUser(value);
                } else {
                    currentUser = createNewUser(userId);
                }
                listener.onUserFetch(currentUser);
            }
        });
    }

    /**
     * Sets a listener to a the User collection. This function sets up a listener so that
     * listener.onAllUsersUpdate() is called whenever the User collection is updated.
     *
     * @param listener the listener with the callback function to be called when the User
     *                 collection is updated
     */
    private void setListenerToCollection(UserManager.OnManyUsersFetchListener listener) {
        userCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<User> users = new ArrayList<>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    users.add(extractUser(doc));
                }
                listener.onManyUsersFetch(users);
            }
        });
    }

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
     * Extracts a User from a Firestore document.
     *
     * @param document the user document to extract from
     * @return the User
     */
    @SuppressWarnings("unchecked")
    private User extractUser(DocumentSnapshot document) {
        User user = new User();
        Map<String, Object> data = document.getData();
        assert data != null;

        String username = document.getString(USERNAME_FIELD);
        String id = document.getString(DEVICE_ID_FIELD);
        String email = document.getString(EMAIL_FIELD);
        String phone = document.getString(PHONE_FIELD);
        /* Doug Stevenson, https://stackoverflow.com/users/807126/doug-stevenson, "How to get an array from Firestore?",
         * 2018-05-08, CC BY-SA 4.0, https://stackoverflow.com/questions/50233281/how-to-get-an-array-from-firestore
         */
        ArrayList<String> subs = (ArrayList<String>) document.get(SUBBED_EXPERIMENTS_FIELD);
        /* End of cited code */

        user.setId(id);
        user.setUsername(username);
        user.getContactInfo().setEmail(email);
        user.getContactInfo().setPhone(phone);
        user.setSubscribedExperiments(subs);

        return user;
    }

    /**
     * Compresses a User into a Map for storage in a Firestore document.
     *
     * @param user The User to be compressed
     * @return A Map of fields to be stored
     */
    private Map<String, Object> compressUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put(USERNAME_FIELD, user.getUsername());
        userData.put(DEVICE_ID_FIELD, user.getId());
        userData.put(EMAIL_FIELD, user.getContactInfo().getEmail());
        userData.put(PHONE_FIELD, user.getContactInfo().getPhone());
        userData.put(SUBBED_EXPERIMENTS_FIELD, user.getSubscribedExperiments());
        return userData;
    }

    public static String getFid() {
        return fid;
    }

    public static void setFid(String fid) {
        UserManager.fid = fid;
    }
}
