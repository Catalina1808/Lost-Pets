package com.example.lostmypet.DAO;

import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.Pet;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOAnnouncement {
    private DatabaseReference databaseReference;

    public DAOAnnouncement(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Announcement.class.getSimpleName());
    }

    public Task<Void> add(Announcement announcement){
        //if(pet == null) throw exc
        return databaseReference.push().setValue(announcement);
    }
}
