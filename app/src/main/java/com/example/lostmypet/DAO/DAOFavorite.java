package com.example.lostmypet.DAO;

import com.example.lostmypet.models.Favorite;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOFavorite {
    private final DatabaseReference databaseReference;
    private String id;

    public DAOFavorite(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("\n" +
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(Favorite.class.getSimpleName());
    }

    public Task<Void> add(Favorite favorite){
        id = databaseReference.push().getKey();
        favorite.setFavoriteID(id);
        return databaseReference.child(id).setValue(favorite);
    }

    //get the id after inseting in database
    public String getId() {
        return id;
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }


}
