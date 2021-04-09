package com.example.trialio;

import com.example.trialio.models.Location;
import com.example.trialio.models.BinomialTrial;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for BinomialTrial class
 */
public class BinomialTrialTest {

    /**
     * Test constructors for binomial trials
     */
    @Test
    void testCreateBinomialTrial() {
        BinomialTrial binomial = new BinomialTrial();

        assertEquals(BinomialTrial.class, binomial.getClass());
        assertNull(binomial.getExperimenterID());
        assertNull(binomial.getLocation());
        assertNull(binomial.getDate());
        assertFalse(binomial.getIsSuccess());

        Location loc = new Location();
        Date date = new Date();
        BinomialTrial binomial2 = new BinomialTrial("Ryan", loc, date, true);

        assertEquals(BinomialTrial.class, binomial2.getClass());
        assertEquals(binomial2.getExperimenterID(), "Ryan");
        assertEquals(binomial2.getLocation(), loc);
        assertEquals(binomial2.getDate(), date);
        assertTrue(binomial2.getIsSuccess());
    }
}
