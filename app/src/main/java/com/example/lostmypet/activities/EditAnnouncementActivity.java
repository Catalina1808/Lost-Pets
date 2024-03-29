package com.example.lostmypet.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lostmypet.DAO.DAOAnnouncement;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.Animal;
import com.example.lostmypet.helpers.Gender;
import com.example.lostmypet.helpers.Type;
import com.example.lostmypet.models.AnnouncementItemRV;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EditAnnouncementActivity extends AppCompatActivity {

    private Spinner genderSpinner;
    private Spinner typeSpinner;
    private Spinner animalSpinner;
    private ImageView petImageView;
    private EditText nameEditText;
    private EditText breedEditText;
    private EditText descriptionEditText;
    private TextView coordinatesTextView;
    private Button changeImageBtn;
    private Button changeLocation;
    private Button updateAnnouncementBtn;

    private AnnouncementItemRV announcementItemRV;

    private Uri photoUri;

    private FirebaseStorage firebaseStorage;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        //get firebase storage for photos
        firebaseStorage = FirebaseStorage.getInstance();

        //get the announcement from the MyAnnouncementsActivity
        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();
        announcementItemRV = (AnnouncementItemRV) bundle.getSerializable("announcement");

        //set UI elements
        getUIElements();
        setPetImageView();
        setUIElements();
        setAnimalSpinner();
        setGenderSpinner();
        setTypeSpinner();
    }


    public void getUIElements(){
        petImageView = findViewById(R.id.imv_pet);
        genderSpinner= findViewById(R.id.spn_gender);
        nameEditText = findViewById(R.id.edt_name);
        animalSpinner = findViewById(R.id.spn_animal);
        breedEditText = findViewById(R.id.edt_breed);
        typeSpinner = findViewById(R.id.spn_type);
        coordinatesTextView = findViewById(R.id.tv_location);
        descriptionEditText = findViewById(R.id.edt_description);
        changeImageBtn = findViewById(R.id.btn_add_image);
        changeLocation = findViewById(R.id.btn_add_location);
        updateAnnouncementBtn = findViewById(R.id.btn_add_announcement);
    }

    public void setUIElements(){
        nameEditText.setText (announcementItemRV.getName()) ;
        breedEditText.setText (announcementItemRV.getBreed());
        descriptionEditText.setText (announcementItemRV.getDescription());
        changeImageBtn.setText(R.string.change_image);
        changeLocation.setText(R.string.change_location);
        coordinatesTextView.setVisibility(View.GONE);
        updateAnnouncementBtn.setText(R.string.update);
    }

    private void setGenderSpinner(){
        List<String> genderList = new ArrayList<>(
                Arrays.asList(getResources().getString(R.string.female),
                        getResources().getString(R.string.male),
                        getResources().getString(R.string.unknown)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_layout, genderList);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(Gender.valueOf(announcementItemRV.getGender()).ordinal());
    }

    private void setTypeSpinner(){
        List<String> typeList = new ArrayList<>(
                Arrays.asList(getResources().getString(R.string.lost),
                        getResources().getString(R.string.found),
                        getResources().getString(R.string.give_away)));

        typeSpinner = findViewById(R.id.spn_type);
        ArrayAdapter<String>adapterType = new ArrayAdapter<>(this,
                R.layout.spinner_layout, typeList);
        adapterType.setDropDownViewResource(R.layout.spinner_layout);
        typeSpinner.setAdapter(adapterType);
        typeSpinner.setSelection(Type.valueOf(announcementItemRV.getType()).ordinal());

    }

    private void setAnimalSpinner(){
        List<String> animalList = new ArrayList<>(
                Arrays.asList(getResources().getString(R.string.dog),
                        getResources().getString(R.string.cat),
                        getResources().getString(R.string.rabbit),
                        getResources().getString(R.string.bird),
                        getResources().getString(R.string.other)));

        animalSpinner = findViewById(R.id.spn_animal);
        ArrayAdapter<String>adapterAnimal = new ArrayAdapter<>(this,
                R.layout.spinner_layout, animalList);
        adapterAnimal.setDropDownViewResource(R.layout.spinner_layout);
        animalSpinner.setAdapter(adapterAnimal);
        animalSpinner.setSelection(Animal.valueOf(announcementItemRV.getAnimal()).ordinal());
    }

    public void setPetImageView(){
        StorageReference storageReference = firebaseStorage.getReference("Announcements/")
                .child(announcementItemRV.getAnnouncementId());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(this).load(imageURL).into(petImageView);
        }).addOnFailureListener(exception -> {
            Toast.makeText(this, R.string.pet_image_missing,
                    Toast.LENGTH_SHORT).show();
            petImageView.setImageResource(R.drawable.icon_image);});
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

    public void onAddAnnouncementButtonClick(View view) {
        if(validatedFields())
            updateAnnouncement();
    }

    public void onAddLocationButtonClick(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        intent.putExtra("announcementID", announcementItemRV.getAnnouncementId());
        intent.putExtra("editable", true);
        intent.putExtra("isLost", announcementItemRV.getType().equals(Type.Lost.toString()));
        this.startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                onPickPhoto();
                Toast.makeText(EditAnnouncementActivity.this, R.string.permission_granted,
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EditAnnouncementActivity.this, R.string.permission_denied,
                        Toast.LENGTH_SHORT).show();
            }
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
                        }
                    }
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
        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            nameEditText.setError(getString(R.string.warning_enter_name));
            validated = false;
        }

        if(TextUtils.isEmpty(descriptionEditText.getText().toString()))
        {
            descriptionEditText.setError(getString(R.string.warning_enter_description));
            validated = false;
        }

        return validated;
    }

    public void updateAnnouncement(){
        DAOAnnouncement daoAnnouncement = new DAOAnnouncement();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", Type.values()[typeSpinner.getSelectedItemPosition()].toString());
        hashMap.put("userID", announcementItemRV.getUserId());
        hashMap.put("announcementID", announcementItemRV.getAnnouncementId());
        hashMap.put("breed", breedEditText.getText().toString());
        hashMap.put("name", nameEditText.getText().toString());
        hashMap.put("gender", Gender.values()[genderSpinner.getSelectedItemPosition()].toString());
        hashMap.put("description", descriptionEditText.getText().toString());
        hashMap.put("animal", Animal.values()[animalSpinner.getSelectedItemPosition()].toString());

        daoAnnouncement.update(announcementItemRV.getAnnouncementId(), hashMap).
                addOnSuccessListener(success -> Toast.makeText(getApplicationContext(),
                        R.string.announcement_updated,
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(err -> Toast.makeText(getApplicationContext(),
                        R.string.insertion_failed,
                        Toast.LENGTH_SHORT).show());

        if(photoUri!=null) {
            //Get the firebase storage reference
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("Announcements/")
                    .child(announcementItemRV.getAnnouncementId());

            // Save the image in Firebase Storage
            storageReference.putFile(photoUri);
        }
    }

}