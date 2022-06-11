package com.example.lostmypet.DAO;

import com.example.lostmypet.models.Announcement;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOAnnouncement {
    private final DatabaseReference databaseReference;
    private String id;

    public DAOAnnouncement(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("\n" +
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(Announcement.class.getSimpleName());
    }

    public Task<Void> add(Announcement announcement){
        id = databaseReference.push().getKey();
        announcement.setAnnouncementID(id);
        return databaseReference.child(id).setValue(announcement);
    }

    public Task<Void> update (String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }

    public String getId() {
        return id;
    }
}
