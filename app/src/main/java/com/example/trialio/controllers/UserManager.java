package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.User;
import com.example.trialio.models.UserContactInfo;
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
import java.util.Map;

public class UserManager {
    private static final String TAG = "UserManager";
    private static final String COLLECTION_PATH = "users";
    private final CollectionReference userCollection;

    private static String fid;

    /*
     * This interface design was adapted from Android callbacks such as OnCLickListener
     * Android Developer Docs, "View.OnClickListener", 2020-09-30, Apache 2.0,
     * https://developer.android.com/reference/android/view/View.OnClickListener
     */

    /**
     * This interface represents an action to be taken when a User document is updated
     * in the Firestore database.
     */
    public interface OnUserFetchListener {

        /**
         * This method will be called when the User document is updated in the database.
         *
         * @param user The user that has been updated in the database
         */
        public void onUserFetch(User user);

    }

    /**
     * This interface represents an action to be taken when a User document is updated
     * in the Firestore database.
     */
    public interface OnManyUsersFetchListener {

        /**
         * This method will be called when the User document is updated in the database.
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
     * Creates a new User and adds document to the database.
     *
     * @param id The id of the User to create
     * @return The User that was created.
     * <p>
     * Note that this User object will not track changes to the
     * User document. If you need to receive all database updates for the User, fetch the
     * User using UserManager#addUserUpdateListener()
     */
    public User createNewUser(String id) {
        Log.d(TAG, String.format("Generating new User with id=%s.", id));
        User user = new User(id);
        Map<String, Object> comp = compressUser(user);
        userCollection.document(id)
                .set(comp)
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

    /**
     * Gets the current User through a callback.
     *
     * @param listener the callback to be called when the User is retrieved
     */
    public void getCurrentUser(OnUserFetchListener listener) {
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
                    setUserFetchListener(userId, listener);
                }
            });
        } else {
            setUserFetchListener(fid, listener);
        }
    }

    /**
     * Gets a User through a callback
     * @param userId the id of the User to retrieve
     * @param listener the callback to be called when the User is fetched
     */
    public void getUser(String userId, OnUserFetchListener listener) {
        setUserFetchListener(userId, listener);
    }

    /**
     * Sets a callback that will be called every time the current User is updated in the database.
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
                    setListenerToUserId(userId, listener);
                }
            });
        } else {
            setListenerToUserId(fid, listener);
        }
    }

    /**
     * Sets a callback to fetch a User
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
        setListenerToUserId(userId, listener);
    }

    /**
     * Sets a callback to fetch all Users
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
                            }
                        } else {
                            Log.d(TAG, "User fetch failed with " + task.getException());
                        }
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
    private void setListenerToUserId(String userId, OnUserFetchListener listener) {
        Task<DocumentSnapshot> doc = userCollection.document(userId).get();
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
     * @param document the user document to extract from
     * @return the User
     */
    @SuppressWarnings("unchecked")
    private User extractUser(DocumentSnapshot document) {
        User user = new User();
        Map<String, Object> data = document.getData();
        assert data != null;

        String id = document.getString("id");
        String username = document.getString("username");
        String email = document.getString("contactInfo.email");
        String phone = document.getString("contactInfo.phone");
        /* Doug Stevenson, https://stackoverflow.com/users/807126/doug-stevenson, "How to get an array from Firestore?",
         * 2018-05-08, CC BY-SA 4.0, https://stackoverflow.com/questions/50233281/how-to-get-an-array-from-firestore
         */
        ArrayList<String> subs = (ArrayList<String>) document.get("subs");
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
     * @param user The User to be compressed
     * @return A Map of fields to be stored
     */
    private Map<String, Object> compressUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        UserContactInfo info = user.getContactInfo();
        userData.put("contactInfo", info);
        userData.put("subscribedExperimentIds", user.getSubscribedExperiments());
        return userData;
    }
}
