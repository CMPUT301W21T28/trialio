package com.example.trialio;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.trialio.activities.ExperimentActivity;
import com.example.trialio.activities.ExperimentCreateActivity;
import com.example.trialio.activities.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for ExperimentCreateActivity.
 */
public class ExperimentCreateActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Tests creating an experiment.
     */
    @Test
    public void createExperiment() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click add experiment button
        FloatingActionButton addExperimentButton = (FloatingActionButton) solo.getView("btnNewExperiment");
        solo.clickOnView(addExperimentButton);

        // Asserts that the current activity is the ExperimentCreateActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ExperimentCreateActivity.class);

        // set text fields
        solo.enterText((EditText) solo.getView(R.id.descriptionEditText), "CreateExperimentTest Description");
        solo.enterText((EditText) solo.getView(R.id.regionEditText), "CreateExperimentTest Region");
        solo.enterText((EditText) solo.getView(R.id.numTrialsEditText), "1");

        // set type
        AppCompatSpinner typeDropdown = (AppCompatSpinner) solo.getView("typeDropdown");
        solo.clickOnView(typeDropdown);
        assertTrue(solo.waitForText("BINOMIAL", 1, 2000));
        solo.clickOnText("BINOMIAL");

        // set switches
        Switch geoSwitch = (Switch) solo.getView("geo_switch");
        solo.clickOnView(geoSwitch);
        Switch openSwitch = (Switch) solo.getView("open_switch");
        solo.clickOnView(openSwitch);

        // click on create button
        Button createButton = (Button) solo.getView("btnAddNewExperiment");
        solo.clickOnView(createButton);

        // Asserts that the current activity is the ExperimentActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);

        // look for fields
        assertTrue(solo.waitForText("CreateExperimentTest Description", 1, 2000));
        assertTrue(solo.waitForText("CreateExperimentTest Region", 1, 2000));
        assertTrue(solo.waitForText("1", 1, 2000));
        assertTrue(solo.waitForText("BINOMIAL", 1, 2000));
        assertTrue(solo.waitForText("yes", 1, 2000));
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}

