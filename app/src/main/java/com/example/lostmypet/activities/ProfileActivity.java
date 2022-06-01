package com.example.lostmypet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.AnnouncementsAdapter;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.example.lostmypet.models.Favorite;
import com.example.lostmypet.models.LocationPoint;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private AnnouncementsAdapter announcementsAdapter;
    private ArrayList<AnnouncementItemRV> recyclerViewList;
    private ArrayList<Announcement> announcements;
    private ArrayList<LocationPoint> locationPoints;
    private ArrayList<Favorite> favorites;
    private Geocoder geocoder;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;

    private ImageView userImageView;
    private TextView usernameTextView;
    private TextView phoneTextView;
    private String userID;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get the current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //get userID from the sender activity
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        //get the database from firebase
        database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        //get the geocoder for getting the city in which is the location
        geocoder = new Geocoder(this, Locale.getDefault());

        //set the recyclerview
        RecyclerView recyclerView = findViewById(R.id.rv_announcements);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewList = new ArrayList<>();
        announcementsAdapter = new AnnouncementsAdapter(this, recyclerViewList);
        recyclerView.setAdapter(announcementsAdapter);

        announcements = new ArrayList<>();
        locationPoints = new ArrayList<>();
        favorites = new ArrayList<>();

        getUser();
        getLocations();
        getAnnouncements();
        getFavorites();
    }

    private void setUIElements() {
        usernameTextView = findViewById(R.id.tv_username);
        userImageView = findViewById(R.id.civ_profile_image);
        phoneTextView = findViewById(R.id.tv_phone);

        usernameTextView.setText(user.getUsername());
        phoneTextView.setText(user.getPhone());

        //Get the firebase storage reference and verify if the user has a profile pic to get
        storageReference = FirebaseStorage.getInstance().getReference("Users/" + userID);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(getApplicationContext()).load(imageURL).into(userImageView);
        }).addOnFailureListener(exception -> Toast.makeText(getApplicationContext(),
                "The user does not have a profile image or it could not be loaded.",
                Toast.LENGTH_SHORT).show());
    }

    public void getUser() {
        DatabaseReference databaseReferenceLocations = database.getReference(User.class.getSimpleName());

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot.getKey(), userID)) {
                        user = dataSnapshot.getValue(User.class);
                        setUIElements();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getFavorites(){

        DatabaseReference databaseReferenceFavorites = database.getReference(Favorite.class.getSimpleName());

        databaseReferenceFavorites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favorites.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(Objects.equals(Objects.requireNonNull(dataSnapshot.getValue(Favorite.class))
                            .getUserID(), currentUser.getUid())) {
                        Favorite favorite = dataSnapshot.getValue(Favorite.class);
                        favorites.add(favorite);
                    }
                }
                setAnnouncements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getAnnouncements(){
        DatabaseReference databaseReferenceAnnouncements = database.getReference(
                Announcement.class.getSimpleName());

        databaseReferenceAnnouncements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcements.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    if(Objects.equals(Objects.requireNonNull(announcement).getUserID(), userID)){
                        announcements.add(announcement);
                    }
                }
                setAnnouncements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceAnnouncements.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcements.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    if(Objects.equals(Objects.requireNonNull(announcement).getUserID(), userID)){
                        announcements.add(announcement);
                    }
                }
                setAnnouncements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getLocations(){
        DatabaseReference databaseReferenceLocations = database.getReference(LocationPoint.class.getSimpleName());

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationPoints.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LocationPoint locationPoint = dataSnapshot.getValue(LocationPoint.class);
                    locationPoints.add(locationPoint);
                }
                setAnnouncements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAnnouncements(){
        recyclerViewList.clear();

        for(Announcement announcement: announcements) {

            AnnouncementItemRV announcementItemRV = new AnnouncementItemRV();
            announcementItemRV.setType(announcement.getType());
            announcementItemRV.setAnnouncementId(announcement.getAnnouncementID());
            announcementItemRV.setAnimal(announcement.getAnimal());
            announcementItemRV.setGender(announcement.getGender());
            announcementItemRV.setName(announcement.getName());
            announcementItemRV.setBreed(announcement.getBreed());
            announcementItemRV.setDescription(announcement.getDescription());
            announcementItemRV.setUserId(announcement.getUserID());
            announcementItemRV.setDate(announcement.getDate());

            ArrayList<Map<Double, Double>> locationsList = new ArrayList<>();
            for(LocationPoint locationPoint: locationPoints){
                if(locationPoint.getAnnouncementID().equals(announcement.getAnnouncementID())){
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(locationPoint.getLatitude(),
                                locationPoint.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String cityName = addresses != null ? addresses.get(0).getLocality() : null;
                    announcementItemRV.setCity(cityName);

                    Map<Double, Double> location = new HashMap<>();
                    location.put(locationPoint.getLatitude(), locationPoint.getLongitude());
                    locationsList.add(location);
                }
            }

            for(Favorite favorite: favorites){
                if(favorite.getAnnouncementID().equals(announcement.getAnnouncementID())){
                    announcementItemRV.setFavoriteID(favorite.getFavoriteID());
                    break;
                }
            }

            announcementItemRV.setLocations(locationsList);
            recyclerViewList.add(announcementItemRV);
        }

        announcementsAdapter.notifyDataSetChanged();

    }

}