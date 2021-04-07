package com.example.trialio;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.utils.ExperimentTypeUtility;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for Experiment class
 */
public class ExperimentTest {

    /**
     * Test constructors for experiments
     */
    @Test
    void testCreateExperiment() {
        Experiment e = new Experiment();
        assertEquals(Experiment.class, e.getClass());

        assertNull(e.getExperimentID());

        assertNotNull(e.getSettings());
        assertEquals(ExperimentSettings.class, e.getSettings().getClass());

        assertNotNull(e.getTrialManager());
        assertEquals(ExperimentSettings.class, e.getSettings().getClass());

        assertNotNull(e.getKeywords());
        ArrayList<String> keywords = e.getKeywords();
        assertEquals(0, keywords.size());

        String type = ExperimentTypeUtility.getCountType();
        ExperimentSettings es = new ExperimentSettings();
        Experiment e2 = new Experiment("experiment2", es, type, true, 12, true);

        assertEquals(e2.getExperimentID(), "experiment2");
        assertTrue(e2.getTrialManager().getIsOpen());
        assertEquals(e2.getTrialManager().getMinNumOfTrials(), 12);

    }

    /**
     * Test the generation of experiment keywords
     */
    @Test
    void testGeneratingKeywords() {
        ExperimentSettings es = new ExperimentSettings();
        String desc = "a b c d and also some e";
        es.setDescription(desc);
        Experiment e = new Experiment();
        e.setSettings(es);

        String[] expectedArr = {"a", "b", "c", "d", "and", "also", "some", "e"};
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(expectedArr));
        assertEquals(expected, e.getKeywords());
    }

    /**
     * Test the generation of experiment keywords when description is empty
     */
    @Test
    void testGeneratingKeywordsEmptyDesc() {
        ExperimentSettings es = new ExperimentSettings();
        String desc = "";
        es.setDescription(desc);
        Experiment e = new Experiment();
        e.setSettings(es);

        String[] expectedArr = {};
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(expectedArr));
        assertNotNull(e.getKeywords());
        assertEquals(expected.size(), e.getKeywords().size());
        assertEquals(expected, e.getKeywords());
        assertFalse(e.getKeywords().contains(""));
    }

    /**
     * Test the generation of experiment keywords when description has bad characters
     */
    @Test
    void testGeneratingKeywordsBadChars() {
        ExperimentSettings es = new ExperimentSettings();
        String desc = " . ; ; ' ";
        es.setDescription(desc);
        Experiment e = new Experiment();
        e.setSettings(es);

        String[] expectedArr = {};
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(expectedArr));
        assertNotNull(e.getKeywords());
        assertEquals(expected.size(), e.getKeywords().size());
        assertEquals(expected, e.getKeywords());
        assertFalse(e.getKeywords().contains(""));
    }

    /**
     * Test checking against keywords
     */
    @Test
    void testCheckingAgainstKeywords() {
        ExperimentSettings es = new ExperimentSettings();
        String desc = "apple bottom jeans";
        es.setDescription(desc);
        Experiment e = new Experiment();
        e.setSettings(es);

        // Check values that should be there
        ArrayList<String> keywords = e.getKeywords();
        assertTrue(keywords.contains("apple"));
        assertTrue(keywords.contains("bottom"));
        assertTrue(keywords.contains("jeans"));

        // Check against values that should not be there
        assertFalse(keywords.contains("appl"));
        assertFalse(keywords.contains(" "));
        assertFalse(keywords.contains(""));
    }

}
