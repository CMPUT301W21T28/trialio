package com.example.trialio;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.firebase.installations.FirebaseInstallations;

/**
 * This class represents a user of the application system.
 */
public class User implements Serializable {

    private String id;
    private String username;
    private UserContactInfo contactInfo;
    private ArrayList<Experiment> subscribedExperiments;
    // private BarcodeManager barcodeManager;

    User() {}

    /**
     * Creates a User object
     * @param id A unique id to identify the user
     */
    public User(String id) {
        this.id = id;
        this.username = "default";
        this.contactInfo = new UserContactInfo();
        this.contactInfo.setEmail("JonDoe@email.com");
        this.contactInfo.setPhone("780-123-4567");
        this.subscribedExperiments = new ArrayList<Experiment>();
    }

    /**
     * Gets the id of the User
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the username of the user
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the contact information for the user
     * @return the user contact information
     */
    public UserContactInfo getContactInfo() {
        return contactInfo;
    }

    /**
     * Gets the users subscribed experiments
     * @return the collection of subscribed experiments
     */
    public ArrayList<Experiment> getSubscribedExperiments() {
        return subscribedExperiments;
    }

    /**
     * Sets the users subscribed experiments
     * @param subscribedExperiments the list of subscribed Experiments
     */
    public void setSubscribedExperiments(ArrayList<Experiment> subscribedExperiments) {
        this.subscribedExperiments = subscribedExperiments;
    }

    /**
     * Adds an experiment the the users subscriptions if that experiment is not already subscribed.
     * If the user is already subscribed to the experiment, the subscription list is unchanged.
     * @param experiment the experiment to be subscribed
     */
    public void addSubscription(Experiment experiment) {
        if (!this.subscribedExperiments.contains(experiment)) {
            this.subscribedExperiments.add(experiment);
        }
    }

    /**
     * Remove an experiment from the users subscriptions
     * @param experiment the experiment to delete from subscriptions
     * @throws IllegalArgumentException User is not subscribed to experiment
     */
    public void removeSubscription(Experiment experiment) {
        if (this.subscribedExperiments.contains(experiment)) {
            this.subscribedExperiments.remove(experiment);
        } else {
            throw new IllegalArgumentException("User not subscribed to experiment");
        }
    }
}
