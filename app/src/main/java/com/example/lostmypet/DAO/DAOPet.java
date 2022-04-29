package com.example.lostmypet.DAO;

import android.content.res.Resources;
import android.widget.Toast;

import com.example.lostmypet.models.Pet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOPet {
    private DatabaseReference databaseReference;

    public DAOPet(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Pet.class.getSimpleName());
    }

    public Task<Void> add(Pet pet){
        //if(pet == null) throw exc
        return databaseReference.push().setValue(pet);
    }
}
