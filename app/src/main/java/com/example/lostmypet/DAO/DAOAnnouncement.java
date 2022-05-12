package com.example.lostmypet.DAO;

import androidx.annotation.NonNull;

import com.example.lostmypet.models.Announcement;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DAOAnnouncement {
    private DatabaseReference databaseReference;
    private String id;

    public DAOAnnouncement(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("\n" +
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(Announcement.class.getSimpleName());
    }

    public Task<Void> add(Announcement announcement){
        //if(pet == null) throw exc

        id = databaseReference.push().getKey();
        announcement.setAnnouncementID(id);
        return databaseReference.child(id).setValue(announcement);
    }


    public String getId() {
        return id;
    }
}
