package com.example.lostmypet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lostmypet.DAO.DAOFavorite;
import com.example.lostmypet.R;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.example.lostmypet.models.Favorite;
import com.example.lostmypet.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ViewAnnouncementActivity extends AppCompatActivity {

    private ImageView petImageView;
    private ImageView genderImageView;
    private ImageView userImageView;
    private ImageButton favoriteImageButton;
    private Button addLoccationButton;
    private TextView nameTextView;
    private TextView animalTextView;
    private TextView breedTextView;
    private TextView cityTextView;
    private TextView typeTextView;
    private TextView usernameTextView;
    private TextView phoneTextView;
    private TextView descriptionTextView;
    private TextView messageTextView;

    private User user;
    private AnnouncementItemRV announcementItemRV;

    private FirebaseStorage firebaseStorage;
    private DAOFavorite daoFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_announcement);

        //get the database object for  favorites announcements
        daoFavorite = new DAOFavorite();

        //get firebase storage for photos
        firebaseStorage = FirebaseStorage.getInstance();

        //get the announcement from the AllAnnouncementsActivity
        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();
        announcementItemRV = (AnnouncementItemRV) bundle.getSerializable("announcement");

        getUIElements();
        getUser();

        //set UI elements
        setPetImageView();
        setUserImageView();
        setTextViews();
        setGenderImageView();

        //set the add to favorite button
        if(announcementItemRV.getFavoriteID()!=null) {
            favoriteImageButton.setColorFilter(ContextCompat.getColor(this, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            favoriteImageButton.setColorFilter(ContextCompat.getColor(this,
                    R.color.dark_orange_alpha_2), android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    public void setGenderImageView(){
        if(announcementItemRV.getGender().equals(this.getResources().getString(R.string.female))){
            genderImageView.setImageResource(R.drawable.female_icon);
        } else if(announcementItemRV.getGender().equals(this.getResources().getString(R.string.male))){
            genderImageView.setImageResource(R.drawable.male_icon);
        } else {
            genderImageView.setVisibility(View.GONE);
        }
    }

    public void setTextViews(){
        nameTextView.setText(announcementItemRV.getName());
        animalTextView.setText(announcementItemRV.getAnimal());
        cityTextView.setText(announcementItemRV.getCity());
        typeTextView.setText(announcementItemRV.getType());
        descriptionTextView.setText(announcementItemRV.getDescription());
        if(announcementItemRV.getBreed().isEmpty()){
            breedTextView.setVisibility(View.GONE);
        } else {
            breedTextView.setText(String.format("/  %s", announcementItemRV.getBreed()));
        }
        if(!announcementItemRV.getType().equals(getString(R.string.lost))){
            messageTextView.setVisibility(View.GONE);
            addLoccationButton.setText(R.string.see_location);
        }
    }

    public void getUIElements(){
        petImageView = findViewById(R.id.imv_pet);
        genderImageView= findViewById(R.id.imv_gender);
        userImageView = findViewById(R.id.imv_user);
        favoriteImageButton = findViewById(R.id.imbtn_favorite);
        addLoccationButton = findViewById(R.id.btn_add_location);
        nameTextView = findViewById(R.id.tv_name);
        animalTextView = findViewById(R.id.tv_animal);
        breedTextView = findViewById(R.id.tv_breed);
        cityTextView = findViewById(R.id.tv_city);
        typeTextView = findViewById(R.id.tv_type);
        usernameTextView = findViewById(R.id.tv_username);
        phoneTextView = findViewById(R.id.tv_phone);
        descriptionTextView = findViewById(R.id.tv_description);
        messageTextView = findViewById(R.id.tv_message);
    }

    public void setPetImageView(){
        StorageReference storageReference = firebaseStorage.getReference("Announcements/")
                .child(announcementItemRV.getAnnouncementId());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(this).load(imageURL).into(petImageView);
        }).addOnFailureListener(exception -> Toast.makeText(this, "The pet image could not be loaded.",
                Toast.LENGTH_SHORT).show());
    }

    public void setUserImageView(){
        StorageReference storageReference = firebaseStorage.getReference("Users/")
                .child(announcementItemRV.getUserId());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(this).load(imageURL).into(userImageView);
        }).addOnFailureListener(exception -> Toast.makeText(this, "The user image could not be loaded.",
                Toast.LENGTH_SHORT).show());
    }

    public void getUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference databaseReferenceLocations = database.getReference(User.class.getSimpleName());

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(Objects.equals(dataSnapshot.getKey(), announcementItemRV.getUserId())) {
                        user = dataSnapshot.getValue(User.class);
                        phoneTextView.setText(Objects.requireNonNull(user).getPhone());
                        usernameTextView.setText(user.getUsername());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onIMBFavouriteClick(View view) {
        if(announcementItemRV.getFavoriteID()==null){
            favoriteImageButton.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
            addToFavorite();
        } else {
            favoriteImageButton.setColorFilter(ContextCompat.getColor(this, R.color.dark_orange_alpha_2), android.graphics.PorterDuff.Mode.SRC_IN);
            deleteFromFavorite();
        }
    }

    //add the announcement to favorites
    public void addToFavorite(){
        daoFavorite = new DAOFavorite();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Favorite favorite = new Favorite(Objects.requireNonNull(currentUser).getUid(),
                announcementItemRV.getAnnouncementId());

        daoFavorite.add(favorite).
                addOnSuccessListener(succes -> Toast.makeText(this,
                        "Announcement added to favorite",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(this,
                        "Insertion failed",
                        Toast.LENGTH_SHORT).show());
        announcementItemRV.setFavoriteID(daoFavorite.getId());
    }

    public void deleteFromFavorite(){
        daoFavorite = new DAOFavorite();
        daoFavorite.remove(announcementItemRV.getFavoriteID()).
                addOnSuccessListener(succes -> Toast.makeText(this,
                        "Announcement removed from favorite",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(this,
                        "Removal failed",
                        Toast.LENGTH_SHORT).show());

        announcementItemRV.setFavoriteID(null);
    }

    public void onAddLocationClick(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        intent.putExtra("announcementID", announcementItemRV.getAnnouncementId());
        this.startActivity(intent);
    }
}