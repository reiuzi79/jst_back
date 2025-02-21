package com.su.JobTracker.request;

public class LocationRequest {
    private String[] location;  // ["都道府県", "市", "区"]

    // Getters and Setters
    public String[] getLocation() {
        return location;
    }

    public void setLocation(String[] location) {
        this.location = location;
    }
}