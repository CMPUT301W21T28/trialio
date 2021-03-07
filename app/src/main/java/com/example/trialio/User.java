package com.example.trialio;

public class User {

    private String username;
    private UserContactInfo contactInfo;
    // private Collection<Experiment> subscribedExperiments;
    private BarcodeManager barcodeManager;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(UserContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public BarcodeManager getBarcodeManager() {
        return barcodeManager;
    }

    public void setBarcodeManager(BarcodeManager barcodeManager) {
        this.barcodeManager = barcodeManager;
    }
}
