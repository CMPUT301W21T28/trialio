package com.example.trialio;

import androidx.annotation.NonNull;

import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserManagerTest {

    private static final String testCollection = "user-test";
    private ArrayList<String> testUserIds;
    private UserManager userManager;

    @Before
    public void initUserManager() {
        FirebaseFirestore.getInstance().collection(testCollection).get();
        userManager = new UserManager(testCollection);
        testUserIds = new ArrayList<>();
        testUserIds.add("100000");
        testUserIds.add("100001");
        testUserIds.add("100002");
        testUserIds.add("100003");
        testUserIds.add("100004");
        testUserIds.add("100005");

        for (String id : testUserIds) {
            FirebaseFirestore.getInstance().collection(testCollection).document(id).delete();
        }
    }

    // https://stackoverflow.com/a/1829949/15048024

    /**
     * Test the updating of User info in the database
     */
    @Test
    public void testCreateNewUser() throws Exception {
        String deviceId = "100000";
        User user = userManager.createNewUser(deviceId);

//        FirebaseFirestore.getInstance().collection(testCollection).whereEqualTo("device_id", deviceId).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // Check a single User was created
//                            QuerySnapshot query = task.getResult();
//                            assertEquals(1, query.size());
//
//                            // Check User document has expected data
//                            DocumentSnapshot doc = query.getDocuments().get(0);
//                            assertEquals(deviceId, doc.get("device_id"));
//                        } else {
//                            // Error occurred connecting to firebase
//                            fail();
//                        }
//                    }
//                });
    }

    /***
     * Test the fetching of a User from the datbase
     */
    @Test
    public void testGetUser() {
        String userId = "100001";
        String username = "username";
        String phone = "780-123-4567";
        String email = "duderio@email";

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("id", userId);
        data.put("username", username);

        FirebaseFirestore.getInstance().collection(testCollection).document(userId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userManager.getUser(userId, new UserManager.OnUserFetchListener() {
                            @Override
                            public void onUserFetch(User user) {
                                assertEquals(userId, user.getId());
                                assertEquals(username, user.getUsername());
                            }
                        });
                    }
                });
    }
}
