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
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostmypet.R;
import com.example.lostmypet.helpers.Animal;
import com.example.lostmypet.helpers.AnnouncementsAdapter;
import com.example.lostmypet.helpers.Type;
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
    private RadioGroup typeRadioGroup;


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
                for(AnnouncementItemRV announcementItemRV: recyclerViewList) {
                    setFavorite(announcementItemRV);
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
                setAllAnnouncements();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceAnnouncements.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcements.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Announcement announcement = dataSnapshot.getValue(Announcement.class);
                    announcements.add(announcement);
                }
                for(AnnouncementItemRV announcementItemRV: recyclerViewList){
                    setAnnouncement(announcementItemRV);
                }
                announcementsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getLocations(){
        DatabaseReference databaseReferenceLocations = database.getReference(LocationPoint.class.getSimpleName());

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationPoints.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LocationPoint locationPoint = dataSnapshot.getValue(LocationPoint.class);
                    locationPoints.add(locationPoint);
                }
                for(AnnouncementItemRV announcementItemRV: recyclerViewList) {
                    setLocation(announcementItemRV);
                }
                announcementsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setFavorite(AnnouncementItemRV announcementItemRV){
        for (Favorite favorite : favorites) {
            if (favorite.getAnnouncementID().equals(announcementItemRV.getAnnouncementId())) {
                announcementItemRV.setFavoriteID(favorite.getFavoriteID());
                return;
            }
        }
        announcementItemRV.setFavoriteID(null);
    }

    private void setLocation(AnnouncementItemRV announcementItemRV){
            ArrayList<Map<Double, Double>> locationsList = new ArrayList<>();
            for (LocationPoint locationPoint : locationPoints) {
                if (locationPoint.getAnnouncementID().equals(announcementItemRV.getAnnouncementId())) {
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
                announcementItemRV.setLocations(locationsList);
            }
    }

    private void setAnnouncement(AnnouncementItemRV announcementItemRV){
             for(Announcement announcement: announcements) {
                if (announcement.getAnnouncementID().equals(announcementItemRV.getAnnouncementId())) {
                    announcementItemRV.setType(announcement.getType());
                    announcementItemRV.setAnnouncementId(announcement.getAnnouncementID());
                    announcementItemRV.setAnimal(announcement.getAnimal());
                    announcementItemRV.setGender(announcement.getGender());
                    announcementItemRV.setName(announcement.getName());
                    announcementItemRV.setBreed(announcement.getBreed());
                    announcementItemRV.setDescription(announcement.getDescription());
                    announcementItemRV.setDate(announcement.getDate());
                    announcementItemRV.setUserId(announcement.getUserID());
                    setLocation(announcementItemRV);
                    setFavorite(announcementItemRV);
                    return;
                }
            }
             recyclerViewList.remove(announcementItemRV);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAllAnnouncements(){
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

            setLocation(announcementItemRV);
            setFavorite(announcementItemRV);

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
        typeRadioGroup = findViewById(R.id.rg_type);

        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            setAllAnnouncements();
            if(!cityEditText.getText().toString().isEmpty()) {
                removeAnnouncementsWithOtherCity(cityEditText.getText().toString());
            }
            filterByAnimal();
            filterByType(checkedId);
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //get the list with filtered announcements(except for this filter)
                setAllAnnouncements();
                filterByType(typeRadioGroup.getCheckedRadioButtonId());
                filterByAnimal();
                removeAnnouncementsWithOtherCity(s);
                announcementsAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeAnnouncementsWithOtherCity(CharSequence s){
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

    private void removeAnnouncementsWithOtherAnimals(int animalIndex){
        for (int i = 0; i < recyclerViewList.size(); i++) {
            if (!recyclerViewList.get(i).getAnimal()
                    .equals(Animal.values()[animalIndex].toString())) {
                recyclerViewList.remove(i);
                i--;
            }
        }
    }

    private void removeAnnouncementsWithOtherType(String type){
        for (int i = 0; i < recyclerViewList.size(); i++) {
            if (!recyclerViewList.get(i).getType().equals(type)) {
                recyclerViewList.remove(i);
                i--;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void filterByType(int radioButtonResId){
        switch(radioButtonResId) {
            case R.id.rb_found:
                removeAnnouncementsWithOtherType(Type.Found.toString());
                break;
            case R.id.rb_lost:
                removeAnnouncementsWithOtherType(Type.Lost.toString());
                break;
            case R.id.rb_give_away:
                removeAnnouncementsWithOtherType(Type.GiveAway.toString());
                break;
        }
    }

    private void filterByAnimal(){
        int color = ContextCompat
                .getColor(AllAnnouncementsActivity.this, R.color.light_orange_1);
        if(dogTextView.getCurrentTextColor() == color){
            removeAnnouncementsWithOtherAnimals(Animal.Dog.ordinal());
            return;}
        if(catTextView.getCurrentTextColor() == color){
            removeAnnouncementsWithOtherAnimals(Animal.Cat.ordinal());
            return;}
        if(rabbitTextView.getCurrentTextColor() == color){
            removeAnnouncementsWithOtherAnimals(Animal.Rabbit.ordinal());
            return;}
        if(birdTextView.getCurrentTextColor() == color){
            removeAnnouncementsWithOtherAnimals(Animal.Bird.ordinal());
            return;}
        if(otherTextView.getCurrentTextColor() == color){
            removeAnnouncementsWithOtherAnimals(Animal.Other.ordinal());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onAnimalClick(TextView animalTextView, ImageButton animalImageButton,
                               int animalIndex){
        //get all announcements in the recyclerview
        setAllAnnouncements();
        if(!cityEditText.getText().toString().isEmpty()) {
            removeAnnouncementsWithOtherCity(cityEditText.getText().toString());
        }
        filterByType(typeRadioGroup.getCheckedRadioButtonId());

        //verify if the button is clicked
        if(animalTextView.getCurrentTextColor()==ContextCompat.getColor(this, R.color.dark_orange)) {
            //set colors on the buttons and text views to emphasize the clicked ones
            setAnimalLayoutColors(animalIndex);

            //remove the announcements with other animals from recyclerview
            removeAnnouncementsWithOtherAnimals(animalIndex);

            //update the recyclerview
            announcementsAdapter.notifyDataSetChanged();
        } else {
            animalTextView.setTextColor(ContextCompat.getColor(this, R.color.dark_orange));
            animalImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat
                    .getColor(this, R.color.dark_orange)));
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setAnimalLayoutColors(int animalIndex){
        switch (animalIndex){
            case 0:
                setAnimalButtonsColors(dogImageButton, catImageButton, rabbitImageButton,
                        birdImageButton, otherImageButton);
                setAnimalTextViewsColors(dogTextView, catTextView, rabbitTextView,
                        birdTextView, otherTextView);
                break;
            case 1:
                setAnimalButtonsColors(catImageButton, dogImageButton, rabbitImageButton,
                        birdImageButton, otherImageButton);
                setAnimalTextViewsColors(catTextView, dogTextView, rabbitTextView,
                        birdTextView, otherTextView);
                break;
            case 2:
                setAnimalButtonsColors(rabbitImageButton, dogImageButton, catImageButton,
                        birdImageButton, otherImageButton);
                setAnimalTextViewsColors(rabbitTextView, dogTextView, catTextView,
                        birdTextView, otherTextView);
                break;
            case 3:
                setAnimalButtonsColors(birdImageButton, dogImageButton, catImageButton,
                        rabbitImageButton, otherImageButton);
                setAnimalTextViewsColors(birdTextView, dogTextView, catTextView,
                        rabbitTextView, otherTextView);
                break;
            case 4:
                setAnimalButtonsColors(otherImageButton, dogImageButton, catImageButton,
                        rabbitImageButton, birdImageButton);
                setAnimalTextViewsColors(otherTextView, dogTextView, catTextView,
                        rabbitTextView, birdTextView);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + animalIndex);
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

    public void onDogImBtnClick(View view) {
        onAnimalClick(dogTextView, dogImageButton, Animal.Dog.ordinal());
    }

    public void onCatImBtnClick(View view) {
        onAnimalClick(catTextView, catImageButton,Animal.Cat.ordinal());
    }

    public void onRabbitImBtnClick(View view) {
        onAnimalClick(rabbitTextView, rabbitImageButton, Animal.Rabbit.ordinal());
    }

    public void onBirdImBtnClick(View view) {
        onAnimalClick(birdTextView, birdImageButton, Animal.Bird.ordinal());
    }

    public void onOtherImBtnClick(View view) {
        onAnimalClick(otherTextView, otherImageButton, Animal.Other.ordinal());
    }
}