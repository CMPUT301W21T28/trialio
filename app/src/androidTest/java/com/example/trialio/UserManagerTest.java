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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserManagerTest {

    String testCollection = "user-test";
    ArrayList<String> testUserIds;
    UserManager userManager;

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

    /**
     * Test the updating of User info in the database
     */
    @Test
    public void testCreateNewUser() {
        String userId = "100000";
        User user = userManager.createNewUser(userId);
        FirebaseFirestore.getInstance().collection(testCollection).document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                assertEquals(userId, user.getId());
                            } else {
                                // User does not have the correct id
                                fail();
                            }
                        } else {
                            // Error occurred connecting to firebase
                            fail();
                        }
                    }
                });
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
