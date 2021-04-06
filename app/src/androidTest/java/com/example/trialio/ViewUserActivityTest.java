package com.example.trialio;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.trialio.activities.MainActivity;
import com.example.trialio.activities.ViewUserActivity;
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.QuestionForumManager;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test ViewUserActivity class.
 */
public class ViewUserActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);

    /**
     * Runs before all tests. Create a new experiment.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        // collection paths for testing
        String experimentTestPath = "experiments-test";
        String usersTestPath = "users-test";

        // set the static collection paths using injection
        ExperimentManager.setCollectionPath(experimentTestPath);
        QuestionForumManager.setCollectionPath(experimentTestPath);
        TrialManager.setCollectionPath(experimentTestPath);
        UserManager.setCollectionPath(usersTestPath);

        // get solo instance
        rule.launchActivity(new Intent());
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click add experiment button
        ImageButton viewUserButton = (ImageButton) solo.getView(R.id.editUserBtn);
        solo.clickOnView(viewUserButton);

        // Asserts that the current activity is the ExperimentCreateActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity", ViewUserActivity.class);
    }

    /**
     * US 04.02.01
     * As an owner or experimenter, I want to edit the contact information in my profile.
     */
    @Test
    public void editContactInformation() {

        // click the edit contact info button
        Button editContactInfoButton = (Button) solo.getView(R.id.editContactInfoButton);
        solo.clickOnView(editContactInfoButton);

        // clear and set text fields
        solo.clearEditText((EditText) solo.getView(R.id.editUserPhone));
        solo.enterText((EditText) solo.getView(R.id.editUserPhone), "0123456789");
        solo.clearEditText((EditText) solo.getView(R.id.editUserEmail));
        solo.enterText((EditText) solo.getView(R.id.editUserEmail), "editContactInformation Email");

        // click the confirm button
        solo.clickOnText("Confirm");

        // look for fields
        assertTrue(solo.waitForText("0123456789", 1, 2000));
        assertTrue(solo.waitForText("editContactInformation Email", 1, 2000));
    }

    /**
     * Closes the activity after each test.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}

