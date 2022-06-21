package com.example.lostmypet.models;

public class LocationPoint {
    private double longitude;
    private double latitude;
    private String announcementID;
    private String userID;
    private String locationPointID;

    public LocationPoint() {
    }

    public LocationPoint(double latitude, double longitude, String announcementID, String userID) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.announcementID = announcementID;
        this.userID = userID;
    }

    public String getLocationPointID() {
        return locationPointID;
    }

    public void setLocationPointID(String locationPointID) {
        this.locationPointID = locationPointID;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAnnouncementID() {
        return announcementID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
