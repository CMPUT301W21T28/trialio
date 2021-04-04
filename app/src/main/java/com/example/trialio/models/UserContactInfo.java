package com.example.trialio.models;

import java.io.Serializable;

/**
 * Represents the contact information for a User.
 */
public class UserContactInfo implements Serializable {

    /**
     * Phone number for a User stored as a string
     */
    private String phone;

    /**
     * Email for a user, stored as a string
     */
    private String email;

    /**
     * Gets the user phone number. This may return NULL if there is no registered phone number
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user phone number
     * @param phone the new phone number to be set
     */
    public void setPhone(String phone) {
        // TODO: Input validation
        this.phone = phone;
    }

    /**
     * Gets the user email. This may return NULL if there is no registered email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user email
     * @param email the email to be set
     */
    public void setEmail(String email) {
        // TODO: Input validation
        this.email = email;
    }
}
