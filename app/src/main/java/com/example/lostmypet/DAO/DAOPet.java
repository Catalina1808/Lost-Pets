package com.example.lostmypet.DAO;

import android.content.res.Resources;
import android.widget.Toast;

import com.example.lostmypet.models.Pet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOPet {
    private DatabaseReference databaseReference;
    private String id;

    public DAOPet(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("\n" +
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(Pet.class.getSimpleName());
    }

    public Task<Void> add(Pet pet){
        id = databaseReference.push().getKey();
        return databaseReference.child(id).setValue(pet);
    }

    public String getId() {
        return id;
    }
}
