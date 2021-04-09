package com.example.trialio;

import androidx.annotation.NonNull;

import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.models.Region;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the ExperimentManager class. This class tests all public methods of the ExperimentManager
 * class, emphasizing the CRUD functionality. These tests disable network connectivity to speed things
 * up.
 * <p>
 * WARNING: UserManager interfaces with Firestore, so some tests may fail to due timeout, depending
 * on the speed of the machine. In particular, this seems to be an issue when running the entire
 * test suite at once on a slow emulator. Often the emulator gets slowed down for a while after this
 * too many runs.
 * <p>
 * Troubleshooting tips:
 * - Wipe data on emulator
 * - Run on real device instead of emulator
 * - Run tests one at a time
 * <p>
 * Tests run successfully as of 2021-04-07
 */
public class ExperimentManagerTest {

    private static final String testCollection = "test-em";
    private static final ArrayList<String> initTestExperimentIds = new ArrayList<>();
    private static final ArrayList<String> unInitTestExperimentIds = new ArrayList<>();
    private static final int ASYNC_DELAY = 10;


    /* All use of CountDownLatch to manage testing of asynchronous methods follows the below citation
     * Martin, https://stackoverflow.com/users/187492/martin, 2009-12-02,
     * Evin1_, https://stackoverflow.com/users/2503185/evin1, 2017-04-05,
     * "How to use JUnit to test asynchronous processes", CC BY-SA 3.0
     * https://stackoverflow.com/a/1829949/15048024
     */

    /**
     * Initialize the string ids of Experiments to be used in all tests.
     */
    @BeforeClass
    public static void setUp() {
        initTestExperimentIds.add("em-tests-001");
        initTestExperimentIds.add("em-tests-002");
        unInitTestExperimentIds.add("em-tests-003");
        unInitTestExperimentIds.add("em-tests-004");
    }

    /**
     * Clean up the collection after each test by deleting all documents in the collection.
     */
    @After
    public void cleanAfterTest() throws InterruptedException {
        // Aggregate the ids of the documents to delete
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> allIds = new ArrayList<>();
        allIds.addAll(initTestExperimentIds);
        allIds.addAll(unInitTestExperimentIds);

        // Delete all docs in testCollection
        CountDownLatch lock = new CountDownLatch(allIds.size());
        for (String id : allIds) {
            db.collection(testCollection).document(id).delete()
                    .addOnCompleteListener(task -> lock.countDown());
        }

        lock.await(ASYNC_DELAY, TimeUnit.SECONDS);
    }

    /**
     * Clean up the database before/after all tests by deleting all documents in the collection
     */
    @BeforeClass
    @AfterClass
    public static void tearDown() throws InterruptedException {
        // Aggregate the ids of the documents to delete
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> allIds = new ArrayList<>();
        allIds.addAll(initTestExperimentIds);
        allIds.addAll(unInitTestExperimentIds);

        // Delete all docs in testCollection
        CountDownLatch lock = new CountDownLatch(allIds.size());
        for (String id : allIds) {
            db.collection(testCollection).document(id).delete()
                    .addOnCompleteListener(task -> lock.countDown());
        }

        lock.await(ASYNC_DELAY, TimeUnit.SECONDS);

    }

    /**
     * Creates a mock ExperimentManager with some data.
     *
     * @return the mock ExperimentManager
     */
    private ExperimentManager mockExperimentManager() {
        CountDownLatch lock = new CountDownLatch(initTestExperimentIds.size());
        ExperimentManager experimentManager = new ExperimentManager(testCollection);
        for (String id : initTestExperimentIds) {
            ExperimentSettings settings = new ExperimentSettings(
                    "mock experiment",
                    new Region("region"),
                    "owner",
                    true
            );
            Experiment experiment = new Experiment(
                    id,
                    settings,
                    ExperimentTypeUtility.getCountType(),
                    true,
                    1,
                    true
            );
            experimentManager.publishExperiment(experiment);
        }
        return experimentManager;
    }

    /**
     * Creates an empty mock ExperimentManager
     *
     * @return the mock ExperimentManager
     */
    private ExperimentManager mockEmptyExperimentManager() {
        return new ExperimentManager(testCollection);
    }

    /**
     * Creates a mock experiment for getting owned experiments
     *
     * @param user         the owner of the mock experiment
     * @param experimentId the id of the mock experiment
     * @return the mock experiment
     */
    private Experiment mockOwnedExperiment(User user, String experimentId) {
        ExperimentSettings settings = new ExperimentSettings(
                "owned experiment",
                new Region("region"),
                user.getId(),
                false
        );

        return new Experiment(
                experimentId,
                settings,
                ExperimentTypeUtility.getBinomialType(),
                true,
                1,
                true
        );
    }

    /**
     * Creates a mock experiment for searching experiments
     *
     * @param description  the description the mock experiment
     * @param experimentId the id of the mock experiment
     * @return the mock experiment
     */
    private Experiment mockSearchExperiment(String description, String experimentId) {
        ExperimentSettings settings = new ExperimentSettings(
                description,
                new Region("region"),
                "owner",
                false
        );

        return new Experiment(
                experimentId,
                settings,
                ExperimentTypeUtility.getMeasurementType(),
                false,
                1,
                true
        );
    }

    /**
     * Tests the publishing of experiments
     */
    @Test
    public void testPublishExperiment() throws InterruptedException {
        ExperimentManager experimentManager = mockEmptyExperimentManager();

        // Create the new Experiment
        String id = unInitTestExperimentIds.get(0);
        String type = ExperimentTypeUtility.getBinomialType();
        ExperimentSettings settings = new ExperimentSettings(
                "publish experiment",
                new Region("region"),
                "owner",
                true);
        Experiment newExperiment = new Experiment(
                id,
                settings,
                type,
                true,
                1,
                true
        );

        // Publish experiment
        CountDownLatch createLock = new CountDownLatch(1);
        experimentManager.publishExperiment(newExperiment).addOnCompleteListener(task -> createLock.countDown());
        createLock.await(ASYNC_DELAY, TimeUnit.SECONDS);

        // Fetch the published experiment
        CountDownLatch readLock = new CountDownLatch(1);
        Boolean[] callbackTriggeredFlag = new Boolean[1];
        callbackTriggeredFlag[0] = false;
        FirebaseFirestore.getInstance().collection(testCollection).document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        assertTrue(task.isSuccessful());

                        // Check is document was published
                        DocumentSnapshot doc = task.getResult();
                        assertEquals(id, doc.get("experimentID"));
                        assertEquals(settings.getDescription(), doc.get("description"));
                        assertEquals(settings.getGeoLocationRequired(), doc.get("geoLocationRequired"));
                        assertEquals(settings.getRegion().getRegionText(), doc.get("regionText"));
                        assertEquals(newExperiment.getTrialManager().getIsOpen(), doc.get("isOpen"));
                        assertEquals(newExperiment.getTrialManager().getType(), doc.get("type"));
                        assertEquals(
                                newExperiment.getTrialManager().getMinNumOfTrials(),
                                ((Long) doc.get("minNumOfTrials")).intValue());

                        callbackTriggeredFlag[0] = true;
                        readLock.countDown();
                    }
                });

        // Wait until experiment read and asserts complete
        readLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        assertTrue(callbackTriggeredFlag[0]);


    }

    /**
     * Test getting experiments using the ExperimentManager
     */
    @Test
    public void testSetOnExperimentFetchListener() throws InterruptedException {
        ExperimentManager em = mockExperimentManager();

        // Get an experiment
        CountDownLatch getLock = new CountDownLatch(1);
        String expId = initTestExperimentIds.get(0);
        final Experiment[] fetchedExperimentHolder = new Experiment[1];
        em.setOnExperimentFetchListener(expId, new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment experiment) {
                fetchedExperimentHolder[0] = experiment;
                getLock.countDown();
            }
        });

        // Wait fot experiment fetch to complete, then check the fetch was correct
        getLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        Experiment experiment = fetchedExperimentHolder[0];
        assertNotNull(experiment);
        assertEquals(expId, experiment.getExperimentID());
    }

    /**
     * Test getting several experiments using the ExperimentManager
     */
    @Test
    public void testSetOnAllExperimentsFetchCallback() throws InterruptedException {
        ExperimentManager em = mockExperimentManager();

        // Get an experiment
        CountDownLatch getLock = new CountDownLatch(1);
        final Experiment[] fetchedExperimentHolder = new Experiment[2];
        em.setOnAllPublishedExperimentsFetchCallback(new ExperimentManager.OnManyExperimentsFetchListener() {
            @Override
            public void onManyExperimentsFetch(List<Experiment> experiments) {
                assertEquals(2, experiments.size());
                fetchedExperimentHolder[0] = experiments.get(0);
                fetchedExperimentHolder[1] = experiments.get(1);
                getLock.countDown();
            }
        });

        // Wait fot experiment fetch to complete, then check the fetch was correct
        getLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        assertNotNull(fetchedExperimentHolder[0]);
        assertNotNull(fetchedExperimentHolder[1]);
        assertEquals(initTestExperimentIds.get(0), fetchedExperimentHolder[0].getExperimentID());
        assertEquals(initTestExperimentIds.get(1), fetchedExperimentHolder[1].getExperimentID());
    }

    /**
     * Test editing an experiment using the experiment manager
     */
    @Test
    public void testEditExperiment() throws InterruptedException {
        ExperimentManager userManager = mockExperimentManager();
        String newEmail = "email@email.com";
        String newPhone = "123456789";

        // First get Experiment that will be edited
        String expId = initTestExperimentIds.get(1);
        CountDownLatch prepLock = new CountDownLatch(1);
        Experiment[] fetchedExpHolder = new Experiment[1];
        fetchedExpHolder[0] = null;

        userManager.setOnExperimentFetchListener(expId, new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment experiment) {
                fetchedExpHolder[0] = experiment;
                prepLock.countDown();
            }
        });

        // Wait for user fetch to complete, then check if values of user are as expected
        prepLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        Experiment exp = fetchedExpHolder[0];
        assertNotNull(exp);
        assertEquals(expId, exp.getExperimentID());

        // Change values of the user, then update in the system
        exp.getSettings().setDescription("this is a new description");
        exp.getTrialManager().setMinNumOfTrials(10);
        exp.setIsPublished(false);

        CountDownLatch updateLock = new CountDownLatch(1);
        userManager.editExperiment(expId, exp).addOnCompleteListener(task -> updateLock.countDown());
        updateLock.await(ASYNC_DELAY, TimeUnit.SECONDS);

        // Get the user again and make sure updates persist
        CountDownLatch getLock = new CountDownLatch(1);
        fetchedExpHolder[0] = null;
        userManager.setOnExperimentFetchListener(expId, new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment experiment) {
                fetchedExpHolder[0] = experiment;
                getLock.countDown();
            }
        });

        // Wait for experiment fetch to complete, then check if values of user are as expected
        getLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        Experiment updatedExp = fetchedExpHolder[0];
        assertNotNull(updatedExp);
        assertEquals(
                exp.getSettings().getDescription(),
                updatedExp.getSettings().getDescription()
        );
        assertEquals(
                exp.getTrialManager().getMinNumOfTrials(),
                updatedExp.getTrialManager().getMinNumOfTrials()
        );
        assertEquals(
                exp.getIsPublished(),
                updatedExp.getIsPublished()
        );
    }

    /**
     * Test deleting an experiment with the ExperimentManager
     */
    @Test
    public void testDeleteExperiment() throws InterruptedException {
        ExperimentManager em = mockExperimentManager();
        String id = initTestExperimentIds.get(0);

        // Delete the experiment
        CountDownLatch deleteLock = new CountDownLatch(1);
        em.deleteExperiment(id, null).addOnCompleteListener(task -> deleteLock.countDown());
        deleteLock.await(ASYNC_DELAY, TimeUnit.SECONDS);

        // Fetch the deleted experiment and make sure it was deleted
        Experiment[] fetchedExperimentHolder = new Experiment[1];
        fetchedExperimentHolder[0] = new Experiment();  // ensure callback gets called and this is replaced
        CountDownLatch getLock = new CountDownLatch(1);
        em.setOnExperimentFetchListener(id, new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment experiment) {
                fetchedExperimentHolder[0] = experiment;
                getLock.countDown();
            }
        });
        getLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        Experiment deletedExp = fetchedExperimentHolder[0];
        assertNull(deletedExp);

    }

    /**
     * Test getting experiments owned by a specific user
     */
    @Test
    public void testGetOwnedExperiments() throws InterruptedException {
        ExperimentManager em = mockExperimentManager();

        // Create objects
        User user = new User("userId", "username");
        Experiment ownedExp1 = mockOwnedExperiment(user, unInitTestExperimentIds.get(0));
        Experiment ownedExp2 = mockOwnedExperiment(user, unInitTestExperimentIds.get(1));

        // Create the experiments
        CountDownLatch createLock = new CountDownLatch(2);
        em.publishExperiment(ownedExp1).addOnCompleteListener(task -> createLock.countDown());
        em.publishExperiment(ownedExp2).addOnCompleteListener(task -> createLock.countDown());
        createLock.await(ASYNC_DELAY, TimeUnit.SECONDS);

        // Get owned experiments for the user
        CountDownLatch getLock = new CountDownLatch(1);
        Experiment[] fetchedExperimentHolder = new Experiment[2];
        em.getOwnedExperiments(user, new ExperimentManager.OnManyExperimentsFetchListener() {
            @Override
            public void onManyExperimentsFetch(List<Experiment> experiments) {
                assertEquals(2, experiments.size());
                fetchedExperimentHolder[0] = experiments.get(0);
                fetchedExperimentHolder[1] = experiments.get(1);
                getLock.countDown();
            }
        });
        getLock.await(ASYNC_DELAY, TimeUnit.SECONDS);

        // Make sure the experiments were fetched correctly
        assertNotNull(fetchedExperimentHolder[0]);
        assertNotNull(fetchedExperimentHolder[1]);

        if (fetchedExperimentHolder[0].getExperimentID().equals(ownedExp1.getExperimentID())) {
            // first fetched corresponds to ownedExp1
            assertEquals(ownedExp2.getExperimentID(), fetchedExperimentHolder[1].getExperimentID());

            // checks for first
            assertEquals(user.getId(), fetchedExperimentHolder[0].getSettings().getOwnerID());
            assertEquals(
                    ownedExp1.getSettings().getDescription(),
                    fetchedExperimentHolder[0].getSettings().getDescription()
            );
            assertEquals(ownedExp1.getIsPublished(), fetchedExperimentHolder[0].getIsPublished());

            // checks for second
            assertEquals(user.getId(), fetchedExperimentHolder[1].getSettings().getOwnerID());
            assertEquals(
                    ownedExp2.getSettings().getDescription(),
                    fetchedExperimentHolder[1].getSettings().getDescription()
            );
            assertEquals(ownedExp2.getIsPublished(), fetchedExperimentHolder[1].getIsPublished());

        } else if (fetchedExperimentHolder[0].getExperimentID().equals(ownedExp2.getExperimentID())) {
            // first fetched corresponds to ownedExp2
            assertEquals(ownedExp1.getExperimentID(), fetchedExperimentHolder[1].getExperimentID());

            // checks for first
            assertEquals(user.getId(), fetchedExperimentHolder[0].getSettings().getOwnerID());
            assertEquals(
                    ownedExp2.getSettings().getDescription(),
                    fetchedExperimentHolder[0].getSettings().getDescription()
            );
            assertEquals(ownedExp2.getIsPublished(), fetchedExperimentHolder[0].getIsPublished());

            // checks for second
            assertEquals(user.getId(), fetchedExperimentHolder[1].getSettings().getOwnerID());
            assertEquals(
                    ownedExp1.getSettings().getDescription(),
                    fetchedExperimentHolder[1].getSettings().getDescription()
            );
            assertEquals(ownedExp1.getIsPublished(), fetchedExperimentHolder[1].getIsPublished());

        } else {
            // fetched experiment matched neither of expected values
            fail();
        }
    }

    /**
     * Test searching for experiments given a list of keywords
     */
    @Test
    public void testSearchByKeyword() throws InterruptedException {
        ExperimentManager em = mockEmptyExperimentManager();
        List<String> keywords = new ArrayList<>();
        keywords.add("apple");
        keywords.add("flop");

        // Create the mock experiments
        Experiment search1 = mockSearchExperiment("apple and bananas", initTestExperimentIds.get(0));
        Experiment search2 = mockSearchExperiment("that is a big flop man", initTestExperimentIds.get(1));
        Experiment notSearch1 = mockSearchExperiment("no way jose", unInitTestExperimentIds.get(0));

        // Publish the mock experiments
        CountDownLatch prepLock = new CountDownLatch(3);
        em.publishExperiment(search1).addOnCompleteListener(task -> prepLock.countDown());
        em.publishExperiment(search2).addOnCompleteListener(task -> prepLock.countDown());
        em.publishExperiment(notSearch1).addOnCompleteListener(task -> prepLock.countDown());
        prepLock.await(ASYNC_DELAY, TimeUnit.SECONDS);

        // Search for the experiments
        CountDownLatch searchLock = new CountDownLatch(1);
        boolean[] callbackTriggered = new boolean[1];
        em.searchByKeyword(keywords, new ExperimentManager.OnManyExperimentsFetchListener() {
            @Override
            public void onManyExperimentsFetch(List<Experiment> experiments) {
                // check num of experiments found
                assertEquals(2, experiments.size());

                // check ids of experiments
                if (experiments.get(0).getExperimentID().equals(search1.getExperimentID())) {
                    assertEquals(experiments.get(1).getExperimentID(), search2.getExperimentID());
                } else if (experiments.get(0).getExperimentID().equals(search2.getExperimentID())) {
                    assertEquals(experiments.get(1).getExperimentID(), search1.getExperimentID());
                }

                // done
                callbackTriggered[0] = true;
                searchLock.countDown();
            }
        });
        searchLock.await(ASYNC_DELAY, TimeUnit.SECONDS);
        assertTrue(callbackTriggered[0]);
    }
}
