package com.example.trialio.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a user of the application system.
 */
public class User implements Serializable {

    private String id;
    private String username;
    private UserContactInfo contactInfo;
    private ArrayList<String> subscribedExperimentIds;
    // private BarcodeManager barcodeManager;


    public User() {
        this.username = "experimenter#0001";
        this.contactInfo = new UserContactInfo();
        this.subscribedExperimentIds = new ArrayList<>();
    }

    /**
     * Creates a User object
     *
     * @param id A unique id to identify the user
     */
    public User(String id) {
        this.id = id;
        this.username = "experimenter#0001";
        this.contactInfo = new UserContactInfo();
        this.subscribedExperimentIds = new ArrayList<>();
    }

    /**
     * Creates a User object
     *
     * @param id A unique id to identify the user
     */
    public User(String id, String username) {
        this.id = id;
        this.username = username;
        this.contactInfo = new UserContactInfo();
        this.subscribedExperimentIds = new ArrayList<>();
    }

    /**
     * Gets the id of the User
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the if of the User
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the username of the user
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user
     *
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
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
}
