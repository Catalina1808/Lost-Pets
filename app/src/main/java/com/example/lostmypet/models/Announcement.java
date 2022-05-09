package com.example.lostmypet.models;


import java.util.ArrayList;
import java.util.Map;

public class Announcement {

    private String type;
  //  private ArrayList<Map<String, String>> coordinates;
    private String userID;
    private String petID;

    public Announcement(){};

    public Announcement(String type, String userID, String petID) {
        this.type = type;
        this.userID = userID;
        this.petID = petID;
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
