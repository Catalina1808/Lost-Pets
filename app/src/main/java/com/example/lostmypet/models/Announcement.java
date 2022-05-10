package com.example.lostmypet.models;

public class Announcement {

    private String type;
    private String userID;
    private String petID;
    private String announcementID;

    public Announcement(){};

    public Announcement(String type, String userID, String petID) {
        this.type = type;
        this.userID = userID;
        this.petID = petID;
    }

    public String getAnnouncementID() {
        return announcementID;
    }

    public void setAnnouncementID(String announcementID) {
        this.announcementID = announcementID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }
}
