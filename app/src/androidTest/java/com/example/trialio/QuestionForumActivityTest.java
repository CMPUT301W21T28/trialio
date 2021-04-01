package com.example.trialio;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.trialio.activities.ExperimentActivity;
import com.example.trialio.activities.ExperimentCreateActivity;
import com.example.trialio.activities.MainActivity;
import com.example.trialio.activities.QuestionForumActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test QuestionForumActivity class.
 */
public class QuestionForumActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests. Create a new experiment and push the Q&A button.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        // click add experiment button
        Button addExperimentButton = (Button) solo.getView("btnNewExperiment");
        solo.clickOnView(addExperimentButton);

        // Asserts that the current activity is the ExperimentCreateActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ExperimentCreateActivity.class);

        // set required fields
        solo.enterText((EditText) solo.getView(R.id.numTrialsEditText), "1");

        // click on create button
        Button createButton = (Button) solo.getView("btnAddNewExperiment");
        solo.clickOnView(createButton);

        // Asserts that the current activity is the ExperimentActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ExperimentActivity.class);

        // click on Q&A button
        Button qaButton = (Button) solo.getView("btnQA");
        solo.clickOnView(qaButton);

        // Asserts that the current activity is the QuestionForumActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", QuestionForumActivity.class);
    }

    /**
     * US 02.01.01
     * As an experimenter, I want to ask a question about an experiment.
     */
    @Test
    public void experimenterAskQuestion() {

        // click the Neq Question button
        Button newQuestionButton = (Button) solo.getView("newQuestion");
        solo.clickOnView(newQuestionButton);

        // set required fields
        solo.enterText((EditText) solo.getView(R.id.editQuestionTitle), "experimenterAskQuestion Title");
        solo.enterText((EditText) solo.getView(R.id.editQuestionBody), "experimenterAskQuestion Body");

        // click confirm
        solo.clickOnText("CONFIRM");

        // look for fields
        assertTrue(solo.waitForText("experimenterAskQuestion Title", 1, 2000));
        assertTrue(solo.waitForText("experimenterAskQuestion Body", 1, 2000));
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

