package com.example.lostmypet.models;

public class Announcement {

    private String type;
    private String userID;
    private String announcementID;
    private String name;
    private String gender;
    private String description;
    private String breed;
    private String animal;

    public Announcement(){};

    public Announcement(String type, String userID, String name,
                        String gender, String description, String breed, String animal) {
        this.type = type;
        this.userID = userID;
        this.name = name;
        this.gender = gender;
        this.description = description;
        this.breed = breed;
        this.animal = animal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
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
}
