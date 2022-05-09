package com.example.lostmypet.models;

public class Pet {

    private String name;
    private String gender;
    private String description;
    private String breed;
    private String animal;

    public Pet(){};

    public Pet(String name, String gender, String description, String breed, String animal) {
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

}

