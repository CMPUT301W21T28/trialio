package com.example.trialio.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a user of the application system.
 */
public class User implements Serializable {

    private String username;
    private String deviceId;
    private UserContactInfo contactInfo;
    private ArrayList<String> subscribedExperimentIds;
    // private BarcodeManager barcodeManager;

    /**
     * Constructor for a User
     */
    public User() {
        this.username = null;
        this.deviceId = null;
        this.contactInfo = new UserContactInfo();
        this.subscribedExperimentIds = new ArrayList<>();
    }

    /**
     * Constructor for a User
     *
     * @param username A unique username to identify the user
     */
    public User(String username) {
        this.username = username;
        this.deviceId = null;
        this.contactInfo = new UserContactInfo();
        this.subscribedExperimentIds = new ArrayList<>();
    }

    /**
     * Creates a User object
     *
     * @param username a username for the user profile
     * @param id       A unique id to identify the user
     */
    public User(String id, String username) {
        this.username = username;
        this.deviceId = id;
        this.contactInfo = new UserContactInfo();
        this.subscribedExperimentIds = new ArrayList<>();
    }

    /**
     * Gets the id of the User
     *
     * @return the id
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the if of the User
     *
     * @param username the unique id of the User
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the username of the user
     *
     * @return the username
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the username of the user
     *
     * @param id the unique device identifier of the user
     */
    public void setDeviceId(String id) {
        this.deviceId = id;
    }

    /**
     * Gets the contact information for the user
     *
     * @return the user contact information
     */
    public UserContactInfo getContactInfo() {
        return contactInfo;
    }

    /**
     * Gets the ids of the users subscribed experiments
     *
     * @return the collection of subscribed experiments
     */
    public ArrayList<String> getSubscribedExperiments() {
        return this.subscribedExperimentIds;
    }

    /**
     * Sets the users subscribed experiments
     *
     * @param subscribedExperiments the list Experiment ids for set as the subscriptions
     */
    public void setSubscribedExperiments(ArrayList<String> subscribedExperiments) {
        this.subscribedExperimentIds = subscribedExperiments;
    }

    /**
     * Adds an experiment the the users subscriptions if that experiment is not already subscribed.
     * If the user is already subscribed to the experiment, the subscription list is unchanged.
     *
     * @param experiment the experiment to be subscribed
     */
    public void addSubscription(Experiment experiment) {
        String id = experiment.getExperimentID();
        if (!this.subscribedExperimentIds.contains(id)) {
            this.subscribedExperimentIds.add(id);
        }
    }

    /**
     * Remove an experiment from the users subscriptions
     *
     * @param experiment the experiment to delete from subscriptions
     * @throws IllegalArgumentException User is not subscribed to experiment
     */
    public void removeSubscription(Experiment experiment) {
        String id = experiment.getExperimentID();
        if (this.subscribedExperimentIds.contains(id)) {
            this.subscribedExperimentIds.remove(id);
        } else {
            throw new IllegalArgumentException("User not subscribed to given experiment");
        }
    }

    /**
     * Determines whether a User is subscribed to a given experiment.
     *
     * @param experiment the experiment to check
     * @return true if the User is subscribed, false otherwise
     */
    public boolean isSubscribed(Experiment experiment) {
        String id = experiment.getExperimentID();
        return this.subscribedExperimentIds.contains(id);
    }
}
