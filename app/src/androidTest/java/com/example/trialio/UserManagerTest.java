package com.example.trialio;

import androidx.annotation.NonNull;

import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class UserManagerTest {

    private final String testCollection = "user-test";
    private static final ArrayList<String> initTestUserIds = new ArrayList<>();
    private static final ArrayList<String> unInitTestUserIds = new ArrayList<>();

    /* All use of CountDownLatch to manage testing of asynchronous methods follows the below citation
     * Martin, https://stackoverflow.com/users/187492/martin, 2009-12-02,
     * Evin1_, https://stackoverflow.com/users/2503185/evin1, 2017-04-05,
     * "How to use JUnit to test asynchronous processes", date, CC BY-SA 3.0
     * https://stackoverflow.com/a/1829949/15048024
     */

    /**
     * Sets up the test class by populating list of testUserIds
     */
    @BeforeClass
    public static void setUp() {
        initTestUserIds.add("001");
        initTestUserIds.add("002");
        unInitTestUserIds.add("003");
        unInitTestUserIds.add("004");
        unInitTestUserIds.add("005");
    }

    /**
     * Creates a mock UserManager
     * @return a UserManager
     */
    private UserManager mockUserManager() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(initTestUserIds.size());
        UserManager userManager = new UserManager(testCollection);
        for (String id : initTestUserIds) {
            userManager.createNewUser(new User(), id).addOnCompleteListener(task->{lock.countDown();});
        }
        lock.await(5, TimeUnit.SECONDS);
        return userManager;
    }

    /**
     * Creates a mock UserManager with no Users in it
     * @return a UserManager
     */
    private UserManager mockEmptyUserManager() {
        return new UserManager(testCollection);
    }

    /**
     * Deletes all test users in the database
     */
    @After
    public void tearDown() throws InterruptedException {
        // Aggregate the ids of the documents to delete
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> allIds = new ArrayList<>();
        allIds.addAll(initTestUserIds);
        allIds.addAll(unInitTestUserIds);

        // Run delete query on all ids
        /* Frank van Puffelen, https://stackoverflow.com/users/209103/frank-van-puffelen,
         * "How to delete document from firestore using where clause", 2017-11-18, CC BY-SA 3.0
         * https://stackoverflow.com/a/47180442/15048024
         */
        CountDownLatch lock = new CountDownLatch(allIds.size());
        db.collection(testCollection).whereIn("id", allIds).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot result = task.getResult();
                        for (DocumentSnapshot doc : result.getDocuments()) {
                            doc.getReference().delete().addOnCompleteListener(task1 -> lock.countDown());
                        }
                    }
                });

        lock.await(5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Test the updating of User info in the database
     */
    @Test
    public void testCreateNewUser() throws Exception {
        // Create the user
        String userId = unInitTestUserIds.get(0);
        User newUser = new User();
        UserManager userManager = mockEmptyUserManager();

        CountDownLatch createLock = new CountDownLatch(1);
        userManager.createNewUser(newUser, userId).addOnCompleteListener(task -> createLock.countDown());

        // Assert the initialized User is correct
        assertEquals(newUser.getId(), userId);
        assertNotNull(newUser.getUsername());

        // Wait until user create task completes
        createLock.await(10, TimeUnit.SECONDS);

        // Fetch the created user
        CountDownLatch readLock = new CountDownLatch(1);
        FirebaseFirestore.getInstance().collection(testCollection).whereEqualTo("id", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check a single User was created
                            QuerySnapshot query = task.getResult();
                            assertEquals(1, query.size());

                            // Check User document has expected data
                            DocumentSnapshot doc = query.getDocuments().get(0);
                            assertEquals(userId, doc.get("id"));

                            readLock.countDown();
                        } else {
                            // Error occurred connecting to firebase
                            fail();
                        }
                    }
                });

        // Wait until user fetch is complete
        readLock.await(10, TimeUnit.SECONDS);
    }
}
