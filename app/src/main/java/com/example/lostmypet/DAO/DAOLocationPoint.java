package com.example.lostmypet.DAO;

import androidx.annotation.NonNull;

import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.LocationPoint;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DAOLocationPoint {
    private DatabaseReference databaseReference;

    public DAOLocationPoint(){
        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(LocationPoint.class.getSimpleName());
    }

    public Task<Void> add(LocationPoint locationPoint){
        return databaseReference.push().setValue(locationPoint);
    }

}
