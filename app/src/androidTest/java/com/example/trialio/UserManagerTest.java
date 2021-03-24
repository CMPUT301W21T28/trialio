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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    /* All use of CountDownLatch to manage testing of asynchronous methods follows the below citation
     * Martin, https://stackoverflow.com/users/187492/martin, 2009-12-02,
     * Evin1_, https://stackoverflow.com/users/2503185/evin1, 2017-04-05,
     * "How to use JUnit to test asynchronous processes", date, CC BY-SA 3.0
     * https://stackoverflow.com/a/1829949/15048024
     */

    /**
     * Test the updating of User info in the database
     */
    @Test
    public void testCreateNewUser() throws Exception {
        String deviceId = "100003";
        User user = userManager.createNewUser(deviceId);
        CountDownLatch lock = new CountDownLatch(1);

        FirebaseFirestore.getInstance().collection(testCollection).whereEqualTo("device_id", deviceId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check a single User was created
                            QuerySnapshot query = task.getResult();
                            assertEquals(1, query.size());
                            fail();

                            // Check User document has expected data
                            DocumentSnapshot doc = query.getDocuments().get(0);
                            assertEquals(deviceId, doc.get("device_id"));
                        } else {
                            // Error occurred connecting to firebase
                            fail();
                        }
                    }
                });

        lock.await(2000, TimeUnit.MILLISECONDS);
    }

    /***
     * Test the fetching of a User from the datbase
     */
    @Test
    public void testGetCurrentUser() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        String deviceId = "100000";
        String username = "NkwmOGk7T76Z6f5MiEiO";
        UserManager.setFid(deviceId);
        userManager.getCurrentUser(new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                assertEquals(deviceId, user.getId());
                assertEquals(username, user.getUsername());
                lock.countDown();
            }
        });

        lock.await();
    }

    /***
     * Test the fetching of a User from the datbase
     */
    @Test
    public void testGetUser() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        String deviceId = "100003";
        String username = "M0t37Y1DJPSJZNHbizUM";
        UserManager.setFid(deviceId);
        userManager.getUser(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                assertEquals(deviceId, user.getId());
                assertEquals(username, user.getUsername());
                lock.countDown();
            }
        });

        lock.await();
    }
}
