package com.example.trialio;

import androidx.annotation.NonNull;

import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.models.Region;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExperimentManagerTest {

    private static final String testCollection = "test-em";
    private static final ArrayList<String> initTestExperimentIds = new ArrayList<>();
    private static final ArrayList<String> unInitTestExperimentIds = new ArrayList<>();

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
    public void cleanUpTest() throws InterruptedException {
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

        lock.await(10000, TimeUnit.MILLISECONDS);
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

        lock.await(10000, TimeUnit.MILLISECONDS);

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
        createLock.await(5, TimeUnit.SECONDS);

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
        readLock.await(5, TimeUnit.SECONDS);
        assertTrue(callbackTriggeredFlag[0]);


    }

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
        getLock.await(10, TimeUnit.SECONDS);
        Experiment experiment = fetchedExperimentHolder[0];
        assertNotNull(experiment);
        assertEquals(expId, experiment.getExperimentID());
    }

    @Test
    public void testSetOnAllExperimentsFetchCallback() throws InterruptedException {
        ExperimentManager em = mockExperimentManager();

        // Get an experiment
        CountDownLatch getLock = new CountDownLatch(1);
        final Experiment[] fetchedExperimentHolder = new Experiment[2];
        em.setOnAllExperimentsFetchCallback(new ExperimentManager.OnManyExperimentsFetchListener() {
            @Override
            public void onManyExperimentsFetch(List<Experiment> experiments) {
                assertEquals(2, experiments.size());
                fetchedExperimentHolder[0] = experiments.get(0);
                fetchedExperimentHolder[1] = experiments.get(1);
                getLock.countDown();
            }
        });

        // Wait fot experiment fetch to complete, then check the fetch was correct
        getLock.await(10, TimeUnit.SECONDS);
        assertNotNull(fetchedExperimentHolder[0]);
        assertNotNull(fetchedExperimentHolder[1]);
        assertEquals(initTestExperimentIds.get(0), fetchedExperimentHolder[0].getExperimentID());
        assertEquals(initTestExperimentIds.get(1), fetchedExperimentHolder[1].getExperimentID());
    }

    @Test
    public void testEditExperiment() {
        fail();
    }

    @Test
    public void testUnpublishExperiment() {
        fail();
    }

    @Test
    public void testGetOwnedExperiments() {
        fail();
    }

    @Test
    public void testSearchByKeyword() {
        fail();
    }
}
