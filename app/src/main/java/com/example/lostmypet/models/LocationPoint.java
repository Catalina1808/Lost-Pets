package com.example.lostmypet.models;

public class LocationPoint {
    private double longitude;
    private double latitude;
    private String announcementID;
    private String userID;

    public LocationPoint() {
    }


    public LocationPoint(double latitude, double longitude, String announcementID, String userID) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.announcementID = announcementID;
        this.userID = userID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAnnouncementID() {
        return announcementID;
    }

    public void setAnnouncementID(String announcementID) {
        this.announcementID = announcementID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
