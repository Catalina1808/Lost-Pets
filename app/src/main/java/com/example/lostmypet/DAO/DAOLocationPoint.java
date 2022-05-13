package com.example.lostmypet.DAO;

import com.example.lostmypet.models.LocationPoint;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOLocationPoint {
    private DatabaseReference databaseReference;
    private String id;

    public DAOLocationPoint(){
        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(LocationPoint.class.getSimpleName());
    }

    public Task<Void> add(LocationPoint locationPoint){
        id = databaseReference.push().getKey();
        locationPoint.setLocationPointID(id);
        return databaseReference.child(id).setValue(locationPoint);
    }

    //get the id after inseting in database
    public String getId() {
        return id;
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }



}
