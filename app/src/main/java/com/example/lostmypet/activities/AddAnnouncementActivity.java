package com.example.lostmypet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostmypet.DAO.DAOAnnouncement;
import com.example.lostmypet.DAO.DAOLocationPoint;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.UtilsValidators;
import com.example.lostmypet.models.Announcement;
import com.example.lostmypet.models.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddAnnouncementActivity extends AppCompatActivity {

    private Spinner genderSpinner;
    private Spinner typeSpinner;
    private Spinner animalSpinner;
    private ImageView petImageView;
    private EditText nameEditText;
    private EditText breedEditText;
    private EditText descriptionEditText;
    private TextView coordinatesTextView;

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    private PermissionsManager permissionsManager;
    private Uri photoUri;

    //the point on the map
    private double longitude;
    private double latitude;

    //auth elements
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        //Get the firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;


        setGenderSpinner();
        setAnimalSpinner();
        setTypeSpinner();

        petImageView = findViewById(R.id.imv_pet);
        coordinatesTextView = findViewById(R.id.tv_location);
        nameEditText = findViewById(R.id.edt_name);
        breedEditText = findViewById(R.id.edt_breed);
        descriptionEditText = findViewById(R.id.edt_description);



//        if (savedInstanceState != null)
//        {
//            nameEditText.setText ( savedInstanceState.getString ("NAME")) ;
//            breedEditText.setText ( savedInstanceState.getString ("BREED"));
//            descriptionEditText.setText ( savedInstanceState.getString ("DESCRIPTION"));
//            genderSpinner.setSelection(savedInstanceState.getInt ("GENDER"));
//            animalSpinner.setSelection(savedInstanceState.getInt ("ANIMAL"));
//            typeSpinner.setSelection(savedInstanceState.getInt ("TYPE"));
//            petImageView.setImageURI(Uri.parse(savedInstanceState.getString("IMAGE")));
//        }


        //get coordinates from AddFirstLocationActivity
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            getFromSharedPreferences();
            latitude = intent.getExtras().getDouble("LATITUDE");
            longitude = intent.getExtras().getDouble("LONGITUDE");
            String coordinates = "Latitude:" + latitude + "\nLongitude:"+ longitude;
            coordinatesTextView.setText(coordinates);
        }
    }


//    @Override
//    public void onSaveInstanceState( Bundle outState )
//    {
//        super.onSaveInstanceState( outState );
//        outState.putString("NAME", nameEditText.getText().toString());
//        outState.putString("BREED", breedEditText.getText().toString());
//        outState.putString("DESCRIPTION", descriptionEditText.getText().toString());
//        outState.putInt("GENDER", genderSpinner.getSelectedItemPosition());
//        outState.putInt("ANIMAL", animalSpinner.getSelectedItemPosition());
//        outState.putInt("TYPE", typeSpinner.getSelectedItemPosition());
//        outState.putString("IMAGE", String.valueOf(photoUri));
//    }

    private void saveItemsToSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("NAME", nameEditText.getText().toString());
        editor.putString("BREED", breedEditText.getText().toString());
        editor.putString("DESCRIPTION", descriptionEditText.getText().toString());
        editor.putInt("GENDER", genderSpinner.getSelectedItemPosition());
        editor.putInt("TYPE", typeSpinner.getSelectedItemPosition());
        editor.putInt("ANIMAL", animalSpinner.getSelectedItemPosition());
        editor.putString("IMAGE", String.valueOf(photoUri));
        editor.apply();
    }

    private void getFromSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        nameEditText.setText ( sharedPreferences.getString ("NAME", "x")) ;
        breedEditText.setText ( sharedPreferences.getString ("BREED", ""));
        descriptionEditText.setText ( sharedPreferences.getString ("DESCRIPTION", ""));
        genderSpinner.setSelection(sharedPreferences.getInt ("GENDER", 1));
        animalSpinner.setSelection(sharedPreferences.getInt ("ANIMAL", 1));
        typeSpinner.setSelection(sharedPreferences.getInt ("TYPE", 1));

        String uriString = sharedPreferences.getString("IMAGE","x");
        if(Objects.equals(uriString, "null")){
            petImageView.setImageResource(R.drawable.icon_image);
        } else {
            photoUri = Uri.parse(uriString);
            petImageView.setImageURI(photoUri);
        }
    }

    private void setGenderSpinner(){
        List<String> enumGender = new ArrayList<>(
                Arrays.asList(getResources().getString(R.string.female),
                        getResources().getString(R.string.male),
                        getResources().getString(R.string.unknown)));

        genderSpinner = findViewById(R.id.spn_gender);
        ArrayAdapter<String>adapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, enumGender);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);

    }

    private void setTypeSpinner(){
        List<String> enumType = new ArrayList<>(
                Arrays.asList(getResources().getString(R.string.lost),
                        getResources().getString(R.string.found),
                        getResources().getString(R.string.give_away)));

        typeSpinner = findViewById(R.id.spn_type);
        ArrayAdapter<String>adapterType = new ArrayAdapter<>(this,
                R.layout.spinner_layout, enumType);
        adapterType.setDropDownViewResource(R.layout.spinner_layout);
        typeSpinner.setAdapter(adapterType);

    }

    private void setAnimalSpinner(){
        List<String> enumAnimal = new ArrayList<>(
                Arrays.asList(getResources().getString(R.string.dog),
                        getResources().getString(R.string.cat),
                        getResources().getString(R.string.rabbit),
                        getResources().getString(R.string.bird),
                        getResources().getString(R.string.other)));


        animalSpinner = findViewById(R.id.spn_animal);
        ArrayAdapter<String>adapterAnimal = new ArrayAdapter<>(this,
                R.layout.spinner_layout, enumAnimal);
        adapterAnimal.setDropDownViewResource(R.layout.spinner_layout);
        animalSpinner.setAdapter(adapterAnimal);
    }

    public void onClickAddImage(View view) {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            onPickPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                onPickPhoto();
                Toast.makeText(AddAnnouncementActivity.this, "Permission granted!",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(AddAnnouncementActivity.this, "Permission denied!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        //Get uri from image from gallery
                        assert result.getData() != null;
                        photoUri = result.getData().getData();

                        //Put the image in the ImageView on screen
                        petImageView.setImageURI(photoUri);

                    }}
            });

    // Trigger gallery selection for a photo
    @SuppressLint("QueryPermissionsNeeded")
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            activityResultLaunch.launch(intent);
        }
    }

    private boolean validatedFields(){

        boolean validated = true;
        if(UtilsValidators.isEmptyField(nameEditText.getText().toString()))
        {
            nameEditText.setError("You should enter a name");
            validated = false;
        }

        if(UtilsValidators.isEmptyField(descriptionEditText.getText().toString()))
        {
            descriptionEditText.setError("You should enter a description");
            validated = false;
        }

        if(UtilsValidators.isEmptyField(coordinatesTextView.getText().toString()))
        {
            coordinatesTextView.setError("You should enter a location");
            validated = false;
        }

        return validated;
    }

    public void addAnnouncement(){
        DAOAnnouncement daoAnnouncement = new DAOAnnouncement();
        DAOLocationPoint daoLocationPoint = new DAOLocationPoint();

        String userID = currentUser.getUid();
        String date = String.valueOf(new java.sql.Date(System.currentTimeMillis()));

        Announcement announcement = new Announcement(typeSpinner.getSelectedItem().toString(),
                userID,
                nameEditText.getText().toString(),
                genderSpinner.getSelectedItem().toString(),
                descriptionEditText.getText().toString(),
                breedEditText.getText().toString(),
                animalSpinner.getSelectedItem().toString(),
                date);


        daoAnnouncement.add(announcement).
                addOnSuccessListener(success -> Toast.makeText(getApplicationContext(),
                        "Announcement inserted",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(getApplicationContext(),
                        "Insertion failed",
                        Toast.LENGTH_SHORT).show());

        //add location point
        String announcementId = daoAnnouncement.getId();
        LocationPoint locationPoint = new LocationPoint(latitude, longitude, announcementId, userID);
        daoLocationPoint.add(locationPoint).
                addOnSuccessListener(success -> Toast.makeText(getApplicationContext(),
                        "Announcement inserted",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(getApplicationContext(),
                        "Insertion failed",
                        Toast.LENGTH_SHORT).show());


        if(photoUri!=null) {
            //Get the firebase storage reference
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("Announcements/").child(announcementId);

            // Save the image in Firebase Storage
            storageReference.putFile(photoUri);
        }
    }

    public void onAddAnnouncementButtonClick(View view) {
        if(validatedFields())
            addAnnouncement();
    }


    public void onAddLocationButtonClick(View view) {
        saveItemsToSharedPreferences();
        Intent intent = new Intent(AddAnnouncementActivity.this,
                AddFirstLocationActivity.class);

        //Ask for location permission
        if (!PermissionsManager.areLocationPermissionsGranted(AddAnnouncementActivity.this)){
            permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(AddAnnouncementActivity.this,
                            "You should grant this permission to see your current location!",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    startActivity(intent);
                }
            });
            permissionsManager.requestLocationPermissions(AddAnnouncementActivity.this);
        }
        else {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddAnnouncementActivity.this, MainPageActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}