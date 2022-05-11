package com.example.lostmypet.models;

public class AnnouncementItemRV {
    String name;
    String animal;
    String city;
    String type;
    String gender;
    String announcementId;

    public AnnouncementItemRV() {}

    public AnnouncementItemRV(String name, String animal, String city, String type, String gender, String announcementId) {
        this.name = name;
        this.animal = animal;
        this.city = city;
        this.type = type;
        this.gender = gender;
        this.announcementId = announcementId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
