package com.example.trialio;

import com.example.trialio.models.Location;
import com.example.trialio.models.CountTrial;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for CountTrial class
 */
public class CountTrialTest {

    /**
     * Test constructors for count trials
     */
    @Test
    void testCreateCountTrial() {
        CountTrial count = new CountTrial();

        assertEquals(CountTrial.class, count.getClass());
        assertNull(count.getExperimenterID());
        assertNull(count.getLocation());
        assertNull(count.getDate());
        assertEquals(count.getCount(), 0);

        Location loc = new Location();
        Date date = new Date();
        CountTrial count2 = new CountTrial("Ryan", loc, date);

        assertEquals(CountTrial.class, count2.getClass());
        assertEquals(count2.getExperimenterID(), "Ryan");
        assertEquals(count2.getLocation(), loc);
        assertEquals(count2.getDate(), date);
        assertEquals(count2.getCount(), 1);
    }
}
