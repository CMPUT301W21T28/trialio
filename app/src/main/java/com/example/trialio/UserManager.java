package com.example.trialio;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class UserManager {
    private static final String TAG = "UserManager";
    private static final String COLLECTION_PATH = "users";
    // private static final String TEST_COLLECTION_PATH = "users";
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
    public interface OnUserUpdateListener {

        /**
         * This method will be called when the User document is updated in the database.
         *
         * @param user The user that has been updated in the database
         */
        public void onUserUpdate(User user);

    }

    /**
     * This interface represents an action to be taken when a User document is updated
     * in the Firestore database.
     */
    public interface OnAllUsersUpdateListener {

        /**
         * This method will be called when the User document is updated in the database.
         *
         * @param userList The user that has been updated in the database
         */
        public void onAllUsersUpdate(ArrayList<User> userList);

    }

    /**
     * Creates a UserManager
     */
    public UserManager() {
        userCollection = FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
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

    /**
     * Sets a callback to fetch the current User
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
    public void addCurrentUserUpdateListener(UserManager.OnUserUpdateListener listener) {
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
    public void addUserUpdateListener(String userId, UserManager.OnUserUpdateListener listener) {
        setListenerToUserId(userId, listener);
    }

    /**
     * TODO
     *
     * @param listener
     */
    public void addAllUserUpdateListener(UserManager.OnAllUsersUpdateListener listener) {

    }


    public void updateUser(User user) {

    }

    public void deleteUser(User user) {

    }

    /**
     * Sets a listener to a the user identified by userId. This function sets up a listener so that
     * listener.onUserUpdate() is called whenever the User document is updated in the database.
     *
     * @param userId   the id of the User to attach the listener to
     * @param listener the listener with the callback function to be called when the User is updated
     */
    private void setListenerToUserId(String userId, UserManager.OnUserUpdateListener listener) {
        Task<DocumentSnapshot> doc = userCollection.document(userId).get();
        userCollection.document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User currentUser;
                if (value.exists()) {
                    Log.d(TAG, "Current User fetched from database: " + value.getData());
                    currentUser = value.toObject(User.class);
                } else {
                    currentUser = createNewUser(userId);
                }
                listener.onUserUpdate(currentUser);
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
    private void setListenerToCollection(UserManager.OnAllUsersUpdateListener listener) {
        userCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<User> users = (ArrayList<User>) value.toObjects(User.class);
                listener.onAllUsersUpdate(users);
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
}
