package com.example.lostmypet.models;


import com.mapbox.geojson.Point;

import java.util.List;

public class Announcement {

    public enum Type {
        LOST,
        FOUND,
        GIVE_AWAY
    }

    private Type type;
    private List<Point> location;
    private String userID;
    private String petID;

    public Announcement(Type type, List<Point> location, String userID, String petID) {
        this.type = type;
        this.location = location;
        this.userID = userID;
        this.petID = petID;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Point> getLocation() {
        return location;
    }

    public void setLocation(List<Point> location) {
        this.location = location;
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
