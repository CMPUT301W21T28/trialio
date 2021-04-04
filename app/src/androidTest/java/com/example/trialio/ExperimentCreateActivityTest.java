package com.example.trialio;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.trialio.activities.ExperimentActivity;
import com.example.trialio.activities.ExperimentCreateActivity;
import com.example.trialio.activities.MainActivity;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.User;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test ExperimentCreateActivity class.
 */
public class ExperimentCreateActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests. Create a new experiment.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // collection paths for testing
        String experimentTestPath = "experiments-test";
        String usersTestPath = "users-test";

        // set the static collection paths using injection
        ExperimentManager.setCollectionPath(experimentTestPath);
        QuestionForumManager.setCollectionPath(experimentTestPath);
        TrialManager.setCollectionPath(experimentTestPath);
        UserManager.setCollectionPath(usersTestPath);

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click add experiment button
        Button addExperimentButton = (Button) solo.getView(R.id.btnNewExperiment);
        solo.clickOnView(addExperimentButton);

        // Asserts that the current activity is the ExperimentCreateActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", ExperimentCreateActivity.class);
    }

    /**
     * US 01.01.01
     * As an owner, I want to publish an experiment with a description, a region, and a minimum
     * number of trials.
     */
    @Test
    public void ownerPublishExperiment() {

        // set text fields
        solo.enterText((EditText) solo.getView(R.id.descriptionEditText), "ownerPublishExperiment Description");
        solo.enterText((EditText) solo.getView(R.id.regionEditText), "ownerPublishExperiment Region");
        solo.enterText((EditText) solo.getView(R.id.numTrialsEditText), "1");

        // click the create button
        clickCreate();

        // look for fields
        assertTrue(solo.waitForText("ownerPublishExperiment Description", 1, 2000));
        assertTrue(solo.waitForText("ownerPublishExperiment Region", 1, 2000));
        assertTrue(solo.waitForText("1", 1, 2000));
    }

    /**
     * US 06.01.01
     * As an owner, I want to specify a Geo-location is required or not for trials.
     */
    @Test
    public void ownerSpecifyGeo() {

        solo.enterText((EditText) solo.getView(R.id.numTrialsEditText), "1");

        // set geo switch
        Switch geoSwitch = (Switch) solo.getView(R.id.geo_switch);
        solo.clickOnView(geoSwitch);

        // click the create button
        clickCreate();

        // TODO: check that Geo-location is enabled
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * This clicks the Create Button in the ExperimentCreateActivity and ensures that the activity
     * switches to ExperimentActivity.
     */
    public void clickCreate() {

        // click on create button
        Button createButton = (Button) solo.getView(R.id.btnAddNewExperiment);
        solo.clickOnView(createButton);

        // Asserts that the current activity is the ExperimentActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);
    }
}

