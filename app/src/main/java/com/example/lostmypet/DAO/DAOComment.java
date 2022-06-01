package com.example.lostmypet.DAO;

import com.example.lostmypet.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOComment  {
    private final DatabaseReference databaseReference;
    private String id;

    public DAOComment(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("\n" +
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = db.getReference(Comment.class.getSimpleName());
    }

    public Task<Void> add(Comment comment){
        id = databaseReference.push().getKey();
        comment.setCommentID(id);
        return databaseReference.child(id).setValue(comment);
    }

    //get the id after inserting in database
    public String getId() {
        return id;
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }


}