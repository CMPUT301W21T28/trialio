package com.example.trialio;

import androidx.annotation.NonNull;

import com.google.firebase.installations.FirebaseInstallations;

/**
 * This class represents a user of the application system.
 */
public class User {

    private String id;
    private String username;
    private UserContactInfo contactInfo;
    // private Collection<Experiment> subscribedExperiments;
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
}
