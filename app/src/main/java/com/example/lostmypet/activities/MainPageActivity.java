package com.example.lostmypet.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lostmypet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainPageActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private ImageView userImageView;
    private FirebaseAuth mAuth;
    private TextView usernameTextView;

    @Override
    protected void onStart() {
        super.onStart();
        //reset username on screen because it might have been changed in "UpdateUserActivity"
        String userDisplayName = currentUser.getDisplayName();
        usernameTextView.setText(userDisplayName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //Get the firebase user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;

        //Get the image and username from UI
        usernameTextView = findViewById(R.id.tv_username);
        userImageView = findViewById(R.id.civ_profile_image);

        //Set username on screen
        String message = currentUser.getDisplayName();
        usernameTextView.setText(message);

        //Get the firebase storage reference and verify if the user has a profile pic to get
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+currentUser.getUid());

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageURL = uri.toString();
            Glide.with(getApplicationContext()).load(imageURL).into(userImageView);
        }).addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), "The user does not have a profile image or it could not be loaded.",
                 Toast.LENGTH_SHORT).show());

    }

    public void onClickLogOut(View view){
        mAuth.signOut();
        Intent intent = new Intent(MainPageActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }


    public void onClickChangePicture(View view){

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
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                onPickPhoto();
                Toast.makeText(MainPageActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MainPageActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
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
                        Uri photoUri = result.getData().getData();

                        //Put the image in the ImageView on screen
                        userImageView.setImageURI(photoUri);

                        //Save the image in Firebase Storage
                        storageReference = FirebaseStorage.getInstance().getReference("Users/"+currentUser.getUid());
                        storageReference.putFile(photoUri).addOnSuccessListener(taskSnapshot -> Toast.makeText(getApplicationContext(), "User Profile updated",
                                Toast.LENGTH_SHORT).show());

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


    public void onAddAnnouncementBtnClick(View view) {
        Intent intent = new Intent(MainPageActivity.this, AddAnnouncementActivity.class);
        startActivity(intent);
    }

    public void onAllAnnouncementsBtnClick(View view) {
        Intent intent = new Intent(MainPageActivity.this, AllAnnouncementsActivity.class);
        startActivity(intent);
    }

    public void onFavoritesBtnClick(View view) {
        Intent intent = new Intent(MainPageActivity.this, FavoritesActivity.class);
        startActivity(intent);
    }

    public void onMyAnnouncementsBtnClick(View view) {
        Intent intent = new Intent(MainPageActivity.this, MyAnnouncementsActivity.class);
        startActivity(intent);
    }

    public void onAccountSettingsBtnClick(View view) {
        Intent intent = new Intent(MainPageActivity.this, UpdateUserActivity.class);
        startActivity(intent);
    }
}