package com.example.lostmypet.DAO;

import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.Pet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DAOPet {
    private DatabaseReference databaseReference;
    private String id;

    public DAOPet(){
        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(Pet.class.getSimpleName());
    }

    public Task<Void> add(Pet pet){
        id = databaseReference.push().getKey();
        pet.setPetID(id);
        return databaseReference.child(id).setValue(pet);
    }

    public Pet getByID(String id){
        //if(pet == null) throw exc
        ArrayList<Pet> petList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(Objects.equals(dataSnapshot.getKey(), id)) {
                        Pet pet = dataSnapshot.getValue(Pet.class);
                       // return pet;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return new Pet();
    }

    public String getId() {
        return id;
    }
}
