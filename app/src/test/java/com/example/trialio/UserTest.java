package com.example.trialio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class unit test the User class
 */
public class UserTest {

    User mockUser() {
        return new User("0001");
    }

    Experiment mockExperiment1() {
        String type = ExperimentTypeUtility.getBinomialType();
        ExperimentSettings settings = new ExperimentSettings();
        return new Experiment("00001", settings, type, 10);
    }

    Experiment mockExperiment2() {
        String type = ExperimentTypeUtility.getCountType();
        ExperimentSettings settings = new ExperimentSettings();
        return new Experiment("00002", settings, type, 12);
    }

    /**
     * Basic test for adding experiment to user subscriptions
     */
    @Test
    void testAddSubscription() {
        User user = mockUser();
        Experiment e1 = mockExperiment1();
        Experiment e2 = mockExperiment2();

        // Assert subscribed experiments currently 0
        assertEquals(0, user.getSubscribedExperiments().size());

        // Assert size equals 1 after adding a subscription
        user.addSubscription(e1);
        assertEquals(1, user.getSubscribedExperiments().size());

        // Assert size equals 2 after adding another subscription
        user.addSubscription(e2);
        assertEquals(2, user.getSubscribedExperiments().size());
    }

    /**
     * Test for adding experiment to user subscriptions that already exists
     */
    @Test
    void testAddSubscriptionRepeat() {
        User user = mockUser();
        Experiment experiment = mockExperiment1();

        // Assert subscribed experiments currently 0
        assertEquals(0, user.getSubscribedExperiments().size());

        // Assert size equals 1 after adding a subscription
        user.addSubscription(experiment);
        assertEquals(1, user.getSubscribedExperiments().size());

        // Assert size equals 1 after adding the same experiment to subscriptions
        user.addSubscription(experiment);
        assertEquals(1, user.getSubscribedExperiments().size());
    }

    /**
     * Basic test for removing experiment from user subscriptions
     */
    @Test
    void testRemoveSubscription() {
        User user = mockUser();
        Experiment e1 = mockExperiment1();
        Experiment e2 = mockExperiment1();
        user.addSubscription(e1);
        user.addSubscription(e2);

        // Assert size equals 2 after at start
        assertEquals(2, user.getSubscribedExperiments().size());

        // Assert size equals 1 after removing a subscription
        user.removeSubscription(e1);
        assertEquals(1, user.getSubscribedExperiments().size());

        // Assert size equals 0 after removing another subscription
        user.removeSubscription(e2);
        assertEquals(0, user.getSubscribedExperiments().size());
    }

    /**
     * Test for removing experiment from user subscriptions that user not subscribed to
     */
    @Test
    void testRemoveSubscriptionException() {
        User user = mockUser();
        Experiment e = mockExperiment1();

        // Assert removing experiment from user throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            user.removeSubscription(e);
        });
    }

}
