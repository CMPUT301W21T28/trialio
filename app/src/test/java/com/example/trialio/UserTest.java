package com.example.trialio;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.models.User;
import com.example.trialio.models.UserContactInfo;
import com.example.trialio.utils.ExperimentTypeUtility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test the User class
 */
public class UserTest {

    User mockUser() {
        return new User("user1");
    }

    Experiment mockExperiment1() {
        String type = ExperimentTypeUtility.getBinomialType();
        ExperimentSettings settings = new ExperimentSettings();
        return new Experiment("exp1", settings, type, true, 10, true);
    }

    Experiment mockExperiment2() {
        String type = ExperimentTypeUtility.getCountType();
        ExperimentSettings settings = new ExperimentSettings();
        return new Experiment("exp2", settings, type, true, 12, true);
    }

    /**
     * Test creating a new User
     */
    @Test
    void testCreateUser() {
        User u1 = new User();
        assertEquals(u1.getClass(), User.class);
        UserContactInfo info = u1.getContactInfo();
        assertEquals(UserContactInfo.class, info.getClass());

        assertNull(u1.getUsername());
        User u2 = new User("1234", "user1");
        assertEquals("1234", u2.getId());
        assertEquals("user1", u2.getUsername());
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
        Experiment e2 = mockExperiment2();
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

    /**
     * Test for checking to see if a user is subscribed to an experiment
     */
    @Test
    void testIsSubscribed() {
        User user = mockUser();
        Experiment e1 = mockExperiment1();
        Experiment e2 = mockExperiment2();

        // Assert user is initially not subscribed
        assertFalse(user.isSubscribed(e1));

        // Check after adding to subs
        user.addSubscription(e1);
        assertTrue(user.isSubscribed(e1));

        // Check with a different experiment
        assertFalse(user.isSubscribed(e2));


    }

}
