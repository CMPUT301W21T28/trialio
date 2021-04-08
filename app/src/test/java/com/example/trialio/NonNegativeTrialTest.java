package com.example.trialio;

import com.example.trialio.models.Location;
import com.example.trialio.models.NonNegativeTrial;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for NonNegativeTrial class
 */
public class NonNegativeTrialTest {

    /**
     * Test constructors for nonnegative trials
     */
    @Test
    void testCreateNonNegTrial() {
        NonNegativeTrial nonnegative = new NonNegativeTrial();
        assertEquals(NonNegativeTrial.class, nonnegative.getClass());
        assertNull(nonnegative.getExperimenterID());
        assertNull(nonnegative.getLocation());
        assertNull(nonnegative.getDate());
        assertEquals(nonnegative.getNonNegCount(), 0);

        Location loc = new Location();
        Date date = new Date();
        NonNegativeTrial nonnegative2 = new NonNegativeTrial("Ryan", loc, date, 42);

        assertEquals(NonNegativeTrial.class, nonnegative2.getClass());
        assertEquals(nonnegative2.getExperimenterID(), "Ryan");
        assertEquals(nonnegative2.getLocation(), loc);
        assertEquals(nonnegative2.getDate(), date);
        assertEquals(nonnegative2.getNonNegCount(), 42);
    }
}
