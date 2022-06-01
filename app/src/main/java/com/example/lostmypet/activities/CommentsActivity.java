package com.example.lostmypet.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostmypet.DAO.DAOComment;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.CommentsAdapter;
import com.example.lostmypet.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {
    private ArrayList<Comment> recyclerViewList;
    private String announcementID;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //get announcementID from the sender activity
        Intent intent = getIntent();
        announcementID = intent.getStringExtra("announcementID");

        //get the database from firebase
        database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        //set the recyclerview
        RecyclerView recyclerView = findViewById(R.id.rv_comments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, recyclerViewList);
        recyclerView.setAdapter(commentsAdapter);

        getComments();

    }

    public void getComments(){

        DatabaseReference databaseReferenceFavorites = database.getReference(Comment.class.getSimpleName());

        databaseReferenceFavorites.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerViewList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(Objects.equals(Objects.requireNonNull(dataSnapshot.getValue(Comment.class))
                            .getAnnouncementID(), announcementID)) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        recyclerViewList.add(comment);
                    }
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onSendClick(View view) {
        DAOComment daoComment = new DAOComment();
        EditText messageEditText = findViewById(R.id.edt_message);
        if(messageEditText.getText().toString().isEmpty()){
            messageEditText.setError("You should enter some text!");
        } else {
            String date = String.valueOf(new java.sql.Date(System.currentTimeMillis()));
            Comment comment = new Comment(currentUser.getUid(), currentUser.getDisplayName(),
                    announcementID, messageEditText.getText().toString(), date);
            daoComment.add(comment)
                    .addOnSuccessListener(success -> Toast.makeText(getApplicationContext(),
                            "Comment inserted",
                            Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(err -> Toast.makeText(getApplicationContext(),
                            "Insertion failed",
                            Toast.LENGTH_SHORT).show());
            messageEditText.getText().clear();
        }
    }
}