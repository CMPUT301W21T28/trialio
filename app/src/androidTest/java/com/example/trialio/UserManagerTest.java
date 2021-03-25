package com.example.trialio;

import androidx.annotation.NonNull;

import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
                assertEquals(deviceId, user.getUsername());
                assertEquals(username, user.getDeviceId());
                lock.countDown();
            }
        });

        lock.await();
    }

    /**
     * Test the fetching of a User from the datbase
     */
    @Test
    public void testGetUser() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        String deviceId = "100003";
        String username = "M0t37Y1DJPSJZNHbizUM";
        userManager.getUser(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                assertEquals(deviceId, user.getUsername());
                assertEquals(username, user.getDeviceId());
                lock.countDown();
            }
        });

        lock.await();
    }


    User retrievedData;

    /**
     * Test the listening of User updates to the database
     */
    @Test
    public void testAddUserUpdateListener() throws InterruptedException {
        String deviceId = "100002";
        String username = "CTUVUuG4P6kkheVUyFtz";

        CountDownLatch lock = new CountDownLatch(1);
        CountDownLatch lock1 = new CountDownLatch(1);
        CountDownLatch lock2 = new CountDownLatch(1);

        String firstPhone = "780-111-1111";
        String firstEmail = "email1@email.com";
        String secondPhone = "780-222-2222";
        String secondEmail = "email2@email.com";

        Map<String, Object> initialData = new HashMap<String, Object>();
        initialData.put("email", firstEmail);
        initialData.put("phone", firstPhone);
        FirebaseFirestore.getInstance().collection(testCollection)
                .document(username)
                .update(initialData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        lock.countDown();
                    }
                });

        lock.await(2000, TimeUnit.MILLISECONDS);

        userManager.addUserUpdateListener(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                String email = user.getContactInfo().getEmail();
                if (email.equals(firstEmail)) {
                    retrievedData = user;
                    lock1.countDown();
                } else if (email.equals(secondEmail)) {
                    retrievedData = user;
                    lock2.countDown();
                } else {
                    fail();
                }
            }
        });

        lock1.await(2000, TimeUnit.MILLISECONDS);
        assertEquals(firstPhone, retrievedData.getContactInfo().getPhone());

        Map<String, Object> updateData = new HashMap<String, Object>();
        updateData.put("email", secondEmail);
        updateData.put("phone", secondPhone);
        FirebaseFirestore.getInstance().collection(testCollection)
                .document(username)
                .update(updateData);

        lock2.await();
        assertEquals(secondPhone, retrievedData.getContactInfo().getPhone());
    }
}
