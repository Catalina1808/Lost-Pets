package com.example.lostmypet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.example.lostmypet.R;
import com.example.lostmypet.helpers.AnnouncementsAdapter;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.example.lostmypet.models.LocationPoint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllAnnouncementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReferenceAnnouncements;
    private DatabaseReference databaseReferencePets;
    private DatabaseReference databaseReferenceLocations;
    private AnnouncementsAdapter announcementsAdapter;
    private ArrayList<AnnouncementItemRV> reciclerViewList;
    private ArrayList<Announcement> announcements;
    private ArrayList<LocationPoint> locationPoints;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_announcements);

        geocoder = new Geocoder(this, Locale.getDefault());
        recyclerView = findViewById(R.id.rv_announcements);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reciclerViewList  = new ArrayList<>();
        announcementsAdapter = new AnnouncementsAdapter(this, reciclerViewList);
        recyclerView.setAdapter(announcementsAdapter);

        announcements = new ArrayList<>();
        locationPoints = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        databaseReferenceAnnouncements = database.getReference(Announcement.class.getSimpleName());
        databaseReferenceLocations = database.getReference(LocationPoint.class.getSimpleName());

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

    }

    public void setAnnouncements(){
        reciclerViewList.clear();

        for(Announcement announcement: announcements) {

            AnnouncementItemRV announcementItemRV = new AnnouncementItemRV();
            announcementItemRV.setType(announcement.getType());
            announcementItemRV.setAnnouncementId(announcement.getAnnouncementID());
            announcementItemRV.setAnimal(announcement.getAnimal());
            announcementItemRV.setGender(announcement.getGender());
            announcementItemRV.setName(announcement.getName());

            for(LocationPoint locationPoint: locationPoints){
                if(locationPoint.getAnnouncementID().equals(announcement.getAnnouncementID())){
                    List<Address> addresses = null;
                    try {
                    addresses = geocoder.getFromLocation(locationPoint.getLatitude(),
                            locationPoint.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert addresses != null;
                    String cityName = addresses.get(0).getLocality();
                    announcementItemRV.setCity(cityName);
                    break;
                    }
            }

            reciclerViewList.add(announcementItemRV);
        }

        announcementsAdapter.notifyDataSetChanged();

    }
}