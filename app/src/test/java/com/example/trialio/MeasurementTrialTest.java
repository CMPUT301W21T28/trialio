package com.example.trialio;

import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for MeasurementTrial class
 */
public class MeasurementTrialTest {

    /**
     * Test constructors for measurement trials
     */
    @Test
    void testCreateMeasurementTrial() {
        MeasurementTrial measurement = new MeasurementTrial();

        assertEquals(MeasurementTrial.class, measurement.getClass());
        assertNull(measurement.getExperimenterID());
        assertNull(measurement.getLocation());
        assertNull(measurement.getDate());
        assertEquals(measurement.getMeasurement(), 0);
        assertNull(measurement.getUnit());

        Location loc = new Location();
        Date date = new Date();
        MeasurementTrial measurement2 = new MeasurementTrial("Ryan", loc, date, 42, "cm");

        assertEquals(MeasurementTrial.class, measurement2.getClass());
        assertEquals(measurement2.getExperimenterID(), "Ryan");
        assertEquals(measurement2.getLocation(), loc);
        assertEquals(measurement2.getDate(), date);
        assertEquals(measurement2.getMeasurement(), 42);
        assertEquals(measurement2.getUnit(), "cm");
    }
}
