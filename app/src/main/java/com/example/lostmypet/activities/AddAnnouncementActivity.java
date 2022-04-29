package com.example.lostmypet.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.example.lostmypet.R;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.Pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddAnnouncementActivity extends AppCompatActivity {

    private Spinner genderSpinner;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        List<String> enumGender = new ArrayList<String>(
                Arrays.asList(Pet.Gender.Female.name(),
                        Pet.Gender.Male.name(),
                        Pet.Gender.Unknown.name()));

        List<String> enumType = new ArrayList<String>(
                Arrays.asList("Lost", "Found", "Give away"));

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, enumNames);
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                findViewById(R.id.ac_edt_gender);
//        textView.setAdapter(adapter);

        genderSpinner = (Spinner)findViewById(R.id.spn_gender);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, enumGender);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        typeSpinner = (Spinner)findViewById(R.id.spn_type);
        ArrayAdapter<String>adapterType = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, enumType);
        adapterType.setDropDownViewResource(R.layout.spinner_layout);
        typeSpinner.setAdapter(adapterType);
    }
}