package com.example.lostmypet.models;

public class Favorite {
    String userID;
    String announcementID;
    String favoriteID;

    public Favorite(){};

    public Favorite(String userID, String announcementID) {
        this.userID = userID;
        this.announcementID = announcementID;
    }

    public String getFavoriteID() {
        return favoriteID;
    }

    public void setFavoriteID(String favoriteID) {
        this.favoriteID = favoriteID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAnnouncementID() {
        return announcementID;
    }

}
