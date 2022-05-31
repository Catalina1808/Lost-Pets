package com.example.lostmypet.activities;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostmypet.R;
import com.example.lostmypet.helpers.AnnouncementsAdapter;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.example.lostmypet.models.Favorite;
import com.example.lostmypet.models.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class AllAnnouncementsActivity extends AppCompatActivity {

    private AnnouncementsAdapter announcementsAdapter;
    private ArrayList<AnnouncementItemRV> recyclerViewList;
    private ArrayList<Announcement> announcements;
    private ArrayList<LocationPoint> locationPoints;
    private ArrayList<Favorite> favorites;
    private Geocoder geocoder;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;

    private ImageButton dogImageButton;
    private ImageButton catImageButton;
    private ImageButton rabbitImageButton;
    private ImageButton birdImageButton;
    private ImageButton otherImageButton;
    private TextView dogTextView;
    private TextView catTextView;
    private TextView rabbitTextView;
    private TextView birdTextView;
    private TextView otherTextView;
    private EditText cityEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_announcements);

        //get the current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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

        getLocations();
        getAnnouncements();
        getFavorites();

        getUIElements();

    }

    public void getFavorites(){

        DatabaseReference databaseReferenceFavorites = database.getReference(Favorite.class.getSimpleName());

        databaseReferenceFavorites.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
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
                //setAnnouncements();

                for(AnnouncementItemRV announcementItemRV: recyclerViewList) {
                    for (Favorite favorite : favorites) {
                        if (favorite.getAnnouncementID().equals(announcementItemRV.getAnnouncementId())) {
                            announcementItemRV.setFavoriteID(favorite.getFavoriteID());
                            break;
                        }
                    }
                }
                announcementsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getAnnouncements(){
        DatabaseReference databaseReferenceAnnouncements = database.getReference(Announcement.class.getSimpleName());

        databaseReferenceAnnouncements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcements.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    announcements.add(announcement);
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
                    announcements.add(announcement);
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
            announcementItemRV.setDate(announcement.getDate());
            announcementItemRV.setUserId(announcement.getUserID());

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

    private void getUIElements(){
        dogImageButton = findViewById(R.id.imbtn_dog);
        catImageButton = findViewById(R.id.imbtn_cat);
        rabbitImageButton = findViewById(R.id.imbtn_rabbit);
        birdImageButton = findViewById(R.id.imbtn_bird);
        otherImageButton = findViewById(R.id.imbtn_other);
        dogTextView = findViewById(R.id.tv_dog);
        catTextView = findViewById(R.id.tv_cat);
        rabbitTextView = findViewById(R.id.tv_rabbit);
        birdTextView = findViewById(R.id.tv_bird);
        otherTextView = findViewById(R.id.tv_other);
        cityEditText = findViewById(R.id.edt_city);

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //get the list with filtered announcements(except for this filter)
                setAnnouncements();

                if(dogTextView.getCurrentTextColor()==ContextCompat
                        .getColor(AllAnnouncementsActivity.this, R.color.light_orange_1)){
                    filterByAnimal(R.string.dog);
                } else if(catTextView.getCurrentTextColor()==ContextCompat
                        .getColor(AllAnnouncementsActivity.this, R.color.light_orange_1)){
                    filterByAnimal(R.string.cat);
                } else if(rabbitTextView.getCurrentTextColor()==ContextCompat
                        .getColor(AllAnnouncementsActivity.this, R.color.light_orange_1)){
                    filterByAnimal(R.string.rabbit);
                } else if(birdTextView.getCurrentTextColor()==ContextCompat
                        .getColor(AllAnnouncementsActivity.this, R.color.light_orange_1)){
                    filterByAnimal(R.string.bird);
                } else if(otherTextView.getCurrentTextColor()==ContextCompat
                        .getColor(AllAnnouncementsActivity.this, R.color.light_orange_1)){
                    filterByAnimal(R.string.other);
                }

                filterByCity(s);

                //update the recyclerview
                announcementsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterByCity(CharSequence s){
        String noDiacriticsString, noDiacriticsSubstring;
        //remove the announcements with other cities from recyclerview
        for (int i = 0; i < recyclerViewList.size(); i++){
            noDiacriticsString = Normalizer
                    .normalize(recyclerViewList.get(i).getCity(), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            noDiacriticsSubstring = Normalizer
                    .normalize(s, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            if(!Pattern.compile(Pattern.quote(noDiacriticsSubstring), Pattern.CASE_INSENSITIVE)
                    .matcher(noDiacriticsString).find()){
                recyclerViewList.remove(i);
                i--;
            }
        }
    }

    private void filterByAnimal(int stringID){
        for (int i = 0; i < recyclerViewList.size(); i++) {
            if (!recyclerViewList.get(i).getAnimal().equals(getString(stringID))) {
                recyclerViewList.remove(i);
                i--;
            }
        }
    }


    private void setAnimalButtonsColors(ImageButton pressedButton, ImageButton button1,
                                        ImageButton button2, ImageButton button3,
                                        ImageButton button4){
        pressedButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(this, R.color.light_orange_1)));
        button1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(this, R.color.dark_orange)));
        button2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(this, R.color.dark_orange)));
        button3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(this, R.color.dark_orange)));
        button4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                .getColor(this, R.color.dark_orange)));
    }

    private void setAnimalTextViewsColors(TextView pressedTextView, TextView textView1,
                                          TextView textView2,TextView textView3,
                                          TextView textView4){
        pressedTextView.setTextColor(ContextCompat.getColor(this, R.color.light_orange_1));
        textView1.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
        textView2.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
        textView3.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
        textView4.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
    }


    @SuppressLint("NotifyDataSetChanged")
    public void onImBtnDogClick(View view) {
        //get all announcements in the recyclerview
        setAnnouncements();
        if(!cityEditText.getText().toString().isEmpty()) {
            filterByCity(cityEditText.getText().toString());
        }

        //verify if the button is clicked
        if(dogTextView.getCurrentTextColor()==ContextCompat.getColor(this, R.color.dark_orange)) {

            //set colors on the buttons and text views to emphasize the clicked ones
            setAnimalButtonsColors(dogImageButton, catImageButton, rabbitImageButton,
                    birdImageButton, otherImageButton);
            setAnimalTextViewsColors(dogTextView, catTextView, rabbitTextView,
                    birdTextView, otherTextView);

            //remove the announcements with other animals from recyclerview
            filterByAnimal(R.string.dog);

            //update the recyclerview
            announcementsAdapter.notifyDataSetChanged();
        } else {
            dogTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
            dogImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.dark_orange)));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onImBtnCatClick(View view) {
        //get all announcements in the recyclerview
        setAnnouncements();
        if(!cityEditText.getText().toString().isEmpty()) {
            filterByCity(cityEditText.getText().toString());
        }

        //verify if the button is clicked
        if(catTextView.getCurrentTextColor()==ContextCompat.getColor(this, R.color.dark_orange)) {

            //set colors on the buttons and text views to emphasize the clicked ones
            setAnimalButtonsColors(catImageButton, dogImageButton, rabbitImageButton,
                    birdImageButton, otherImageButton);
            setAnimalTextViewsColors(catTextView, dogTextView, rabbitTextView,
                    birdTextView, otherTextView);

            //remove the announcements with other animals from recyclerview
            filterByAnimal(R.string.cat);

            announcementsAdapter.notifyDataSetChanged();
        } else {
            catTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
            catImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.dark_orange)));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onImBtnRabbitClick(View view) {
        //get all announcements in the recyclerview
        setAnnouncements();
        if(!cityEditText.getText().toString().isEmpty()) {
            filterByCity(cityEditText.getText().toString());
        }

        //verify if the button is clicked
        if(rabbitTextView.getCurrentTextColor()==ContextCompat.getColor(this, R.color.dark_orange)) {

            //set colors on the buttons and text views to emphasize the clicked ones
            setAnimalButtonsColors(rabbitImageButton, catImageButton, dogImageButton,
                    birdImageButton, otherImageButton);
            setAnimalTextViewsColors(rabbitTextView, catTextView, dogTextView,
                    birdTextView, otherTextView);

            //remove the announcements with other animals from recyclerview
            filterByAnimal(R.string.rabbit);

            announcementsAdapter.notifyDataSetChanged();
        } else {
            rabbitTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
            rabbitImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.dark_orange)));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onImBtnBirdClick(View view) {
        //get all announcements in the recyclerview
        setAnnouncements();
        if(!cityEditText.getText().toString().isEmpty()) {
            filterByCity(cityEditText.getText().toString());
        }

        //verify if the button is clicked
        if(birdTextView.getCurrentTextColor()==ContextCompat.getColor(this, R.color.dark_orange)) {

            //set colors on the buttons and text views to emphasize the clicked ones
            setAnimalButtonsColors(birdImageButton, rabbitImageButton, catImageButton,
                    dogImageButton, otherImageButton);
            setAnimalTextViewsColors(birdTextView, rabbitTextView, catTextView,
                    dogTextView, otherTextView);

            //remove the announcements with other animals from recyclerview
            filterByAnimal(R.string.bird);

            announcementsAdapter.notifyDataSetChanged();
        } else {
            birdTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
            birdImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.dark_orange)));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onImBtnOtherClick(View view) {
        //get all announcements in the recyclerview
        setAnnouncements();
        if(!cityEditText.getText().toString().isEmpty()) {
            filterByCity(cityEditText.getText().toString());
        }

        //verify if the button is clicked
        if(otherTextView.getCurrentTextColor()==ContextCompat.getColor(this, R.color.dark_orange)) {

            //set colors on the buttons and text views to emphasize the clicked ones
            setAnimalButtonsColors(otherImageButton, rabbitImageButton, catImageButton,
                    dogImageButton, birdImageButton);
            setAnimalTextViewsColors(otherTextView, rabbitTextView, catTextView,
                    dogTextView, birdTextView);

            //remove the announcements with other animals from recyclerview
            filterByAnimal(R.string.other);

            announcementsAdapter.notifyDataSetChanged();
        } else {
            otherTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
            otherImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.dark_orange)));
        }
    }
}