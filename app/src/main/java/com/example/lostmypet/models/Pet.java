package com.example.lostmypet.models;

public class Pet {

    public enum Gender {
        Male,
        Female,
        Unknown
    }

    private String name;
    private Gender gender;
    private String description;
    private String breed;
    private String animal;
    private String imageURL;

    public Pet(String name, Gender gender, String description, String breed, String animal, String imageURL) {
        this.name = name;
        this.gender = gender;
        this.description = description;
        this.breed = breed;
        this.animal = animal;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
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

}

