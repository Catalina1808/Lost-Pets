package com.example.lostmypet.DAO;

import com.example.lostmypet.models.Pet;
import com.example.lostmypet.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOUser {
    private DatabaseReference databaseReference;

    public DAOUser(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("\n" +
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    public Task<Void> add(User user, String id){
        return databaseReference.child(id).setValue(user);
    }
}