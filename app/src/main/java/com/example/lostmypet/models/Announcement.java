package com.example.lostmypet.models;


import java.util.ArrayList;
import java.util.Map;

public class Announcement {

    private String type;
    private ArrayList<Map<String, String>> coordinates;
    private String userID;
    private String petID;

    public Announcement(){};

    public Announcement(String type, ArrayList<Map<String, String>> coordinates, String userID, String petID) {
        this.type = type;
        this.coordinates = coordinates;
        this.userID = userID;
        this.petID = petID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Map<String, String>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Map<String, String>> coordinates) {
        this.coordinates = coordinates;
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
