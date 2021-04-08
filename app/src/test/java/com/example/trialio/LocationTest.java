package com.example.trialio;

import com.example.trialio.models.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for Location class
 */
public class LocationTest {

    /**
     * Test constructors for locations
     */
    @Test
    void testCreateLocation() {
        Location l = new Location();

        assertEquals(Location.class, l.getClass());
        assertEquals(l.getLatitude(), 0);
        assertEquals(l.getLongitude(), 0);

        Location l2 = new Location(53.426485, -113.664513);
        
        assertEquals(Location.class, l2.getClass());
        assertEquals(l2.getLatitude(), 53.426485);
        assertEquals(l2.getLongitude(), -113.664513);
    }
}
