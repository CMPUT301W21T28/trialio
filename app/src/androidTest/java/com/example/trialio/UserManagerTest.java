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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test suite for the UserManager class. This class tests all public methods of the UserManager
 * class, emphasizing the CRUD functionality.
 */
public class UserManagerTest {

    private final String testCollection = "users-test";
    private static final ArrayList<String> initTestUserIds = new ArrayList<>();
    private static final ArrayList<String> initTestUsernames = new ArrayList<>();
    private static final ArrayList<String> unInitTestUserIds = new ArrayList<>();

    /* All use of CountDownLatch to manage testing of asynchronous methods follows the below citation
     * Martin, https://stackoverflow.com/users/187492/martin, 2009-12-02,
     * Evin1_, https://stackoverflow.com/users/2503185/evin1, 2017-04-05,
     * "How to use JUnit to test asynchronous processes", CC BY-SA 3.0
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
    }

    /**
     * Creates a mock UserManager
     *
     * @return a UserManager
     */
    private UserManager mockUserManager() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(initTestUserIds.size());
        UserManager userManager = new UserManager(testCollection);
        initTestUsernames.clear();
        for (String id : initTestUserIds) {
            User user = new User();
            userManager.createNewUser(user, id).addOnCompleteListener(task -> {
                initTestUsernames.add(user.getUsername());
                lock.countDown();
            });
        }
        lock.await(5, TimeUnit.SECONDS);
        return userManager;
    }

    /**
     * Creates a mock UserManager with no Users in it
     *
     * @return a UserManager
     */
    private UserManager mockEmptyUserManager() {
        return new UserManager(testCollection);
    }

    /**
     * Deletes all test users in the database
     */
    @After
    public void clean() throws Exception {
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
     * Deletes all test users in the database
     */
    @BeforeClass
    @AfterClass
    public static void tearDown() throws Exception {
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
     * Test the creating of a User in the system
     */
    @Test
    public void testCreateNewUser() throws Exception {
        UserManager userManager = mockEmptyUserManager();

        // Create the user
        String userId = unInitTestUserIds.get(0);
        User newUser = new User();

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

    /**
     * Test the getting of a user from the system
     */
    @Test
    public void testGetUserById() throws Exception {
        UserManager userManager = mockUserManager();

        // Get user by id
        CountDownLatch getLock = new CountDownLatch(1);
        String userId = initTestUserIds.get(0);
        final User[] fetchedUserHolder = new User[1];
        userManager.getUserById(userId, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                getLock.countDown();
            }
        });

        // Wait for user fetch to complete, then check the fetch was correct
        getLock.await(5, TimeUnit.SECONDS);
        User user = fetchedUserHolder[0];
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertNotNull(user.getUsername());

    }

    /**
     * Test the getting of a user from the system
     */
    @Test
    public void testGetUserByUsername() throws Exception {
        UserManager userManager = mockUserManager();

        // Get a user using username
        String username = initTestUsernames.get(1);
        String userId = initTestUserIds.get(1);
        CountDownLatch getLock = new CountDownLatch(1);
        User[] fetchedUserHolder = new User[1];
        fetchedUserHolder[0] = null;

        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                getLock.countDown();
            }
        });

        // Wait for user fetch to complete, then check if values of user are as expected
        getLock.await(5, TimeUnit.SECONDS);
        User user = fetchedUserHolder[0];
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(userId, user.getId());
    }

    /**
     * Test the updating of a user in the system
     */
    @Test
    public void testUpdateUser() throws Exception {
        UserManager userManager = mockUserManager();
        String newEmail = "email@email.com";
        String newPhone = "123456789";

        // First get that will be updated
        String username = initTestUsernames.get(1);
        String userId = initTestUserIds.get(1);
        CountDownLatch prepLock = new CountDownLatch(1);
        User[] fetchedUserHolder = new User[1];
        fetchedUserHolder[0] = null;

        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                prepLock.countDown();
            }
        });

        // Wait for user fetch to complete, then check if values of user are as expected
        prepLock.await(5, TimeUnit.SECONDS);
        User user = fetchedUserHolder[0];
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(userId, user.getId());

        // Change values of the user, then update in the system
        user.getContactInfo().setEmail(newEmail);
        user.getContactInfo().setPhone(newPhone);

        CountDownLatch updateLock = new CountDownLatch(1);
        userManager.updateUser(user).addOnCompleteListener(task -> updateLock.countDown());
        updateLock.await(5, TimeUnit.SECONDS);

        // Get the user again and make sure updates persist
        CountDownLatch getLock = new CountDownLatch(1);
        fetchedUserHolder[0] = null;
        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                getLock.countDown();
            }
        });

        // Wait for user fetch to complete, then check if values of user are as expected
        getLock.await(5, TimeUnit.SECONDS);
        User updatedUser = fetchedUserHolder[0];
        assertNotNull(updatedUser);
        assertEquals(user.getUsername(), updatedUser.getUsername());
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(user.getContactInfo().getEmail(), updatedUser.getContactInfo().getEmail());
        assertEquals(user.getContactInfo().getPhone(), updatedUser.getContactInfo().getPhone());
    }

    /**
     * Test the deleting of a user in the system
     */
    @Test
    public void testDeleteUser() throws Exception {
        UserManager userManager = mockUserManager();
        String username = initTestUsernames.get(0);

        // Get the user
        CountDownLatch getLock = new CountDownLatch(1);
        User[] fetchedUserHolder = new User[1];
        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                getLock.countDown();
            }
        });
        getLock.await(5, TimeUnit.SECONDS);
        User user = fetchedUserHolder[0];
        assertNotNull(user);
        assertEquals(username, user.getUsername());

        // Delete the user from the system
        CountDownLatch deleteLock = new CountDownLatch(1);
        userManager.deleteUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                deleteLock.countDown();
            }
        });
        deleteLock.await(5, TimeUnit.SECONDS);

        // Assert the user was actually deleted
        CountDownLatch afterLock = new CountDownLatch(1);
        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                // Fetched user should be null
                fetchedUserHolder[0] = user;
                afterLock.countDown();
            }
        });
        afterLock.await(5, TimeUnit.SECONDS);

        User after = fetchedUserHolder[0];
        assertNull(after);
    }

    /**
     * Test the transferring of a user to a new username
     */
    @Test
    public void testTransferUsername() throws Exception {
        UserManager userManager = mockUserManager();
        String username = initTestUsernames.get(1);
        String newUsername = "new";

        // Get the user
        CountDownLatch getLock = new CountDownLatch(1);
        User[] fetchedUserHolder = new User[1];
        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                getLock.countDown();
            }
        });
        getLock.await(5, TimeUnit.SECONDS);
        User user = fetchedUserHolder[0];
        assertNotNull(user);
        assertEquals(username, user.getUsername());

        // Change the username of the user
        CountDownLatch transferLock = new CountDownLatch(1);
        userManager.transferUsername(user, newUsername).addOnCompleteListener(task -> transferLock.countDown());
        transferLock.await(5, TimeUnit.SECONDS);

        // Fetch from the new username should return the user
        fetchedUserHolder[0] = null;
        CountDownLatch newLock = new CountDownLatch(1);
        userManager.getUserByUsername(newUsername, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                newLock.countDown();
            }
        });
        newLock.await(5, TimeUnit.SECONDS);
        User newUser = fetchedUserHolder[0];
        assertNotNull(newUser);
        assertEquals(newUser.getUsername(), newUsername);

        // Fetch from the old username should return NULL
        CountDownLatch oldLock = new CountDownLatch(1);
        userManager.getUserByUsername(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;
                oldLock.countDown();
            }
        });
        oldLock.await(5, TimeUnit.SECONDS);
        User oldUser = fetchedUserHolder[0];
        assertNull(oldUser);
    }

    /**
     * Test the user update listener
     */
    @Test
    public void testAddUserUpdateListener() throws Exception {
        UserManager userManager = mockUserManager();
        String username = initTestUsernames.get(0);
        String newEmail = "new new new";
        String newPhone = "3243244";

        // Set up synchronization locks and callback result containers
        CountDownLatch setLock = new CountDownLatch(1);
        CountDownLatch updateLock = new CountDownLatch(1);
        User[] fetchedUserHolder = new User[1];
        int[] operationNum = new int[1];

        // Get the user
        operationNum[0] = 1; // indicate we intend to set the listener
        userManager.addUserUpdateListener(username, new UserManager.OnUserFetchListener() {
            @Override
            public void onUserFetch(User user) {
                fetchedUserHolder[0] = user;    // store the fetched user
                if (operationNum[0] == 1) {
                    setLock.countDown();        // indicate set listener operation is done
                } else if (operationNum[0] == 2) {
                    updateLock.countDown();     // indicate updated read operation is done
                }
            }
        });
        setLock.await(5, TimeUnit.SECONDS);
        assertNotNull(fetchedUserHolder[0]);
        assertEquals(username, fetchedUserHolder[0].getUsername());

        // Create a copy of the User object and update some values
        User copy = new User();
        copy.setUsername(fetchedUserHolder[0].getUsername());
        copy.setId(fetchedUserHolder[0].getId());
        copy.getContactInfo().setPhone(fetchedUserHolder[0].getContactInfo().getPhone());
        copy.getContactInfo().setEmail(fetchedUserHolder[0].getContactInfo().getEmail());
        copy.getContactInfo().setEmail(newEmail);   // set a new email
        copy.getContactInfo().setPhone(newPhone);   // set a new phone


        // Update the copied object
        operationNum[0] = 2;
        userManager.updateUser(copy);
        updateLock.await(5, TimeUnit.SECONDS);

        // Assert changes observed in original object
        assertNotNull(fetchedUserHolder[0]);
        assertEquals(copy.getUsername(), fetchedUserHolder[0].getUsername());
        assertEquals(copy.getId(), fetchedUserHolder[0].getId());
        assertEquals(copy.getContactInfo().getEmail(), fetchedUserHolder[0].getContactInfo().getEmail());
        assertEquals(copy.getContactInfo().getPhone(), fetchedUserHolder[0].getContactInfo().getPhone());

    }


}
