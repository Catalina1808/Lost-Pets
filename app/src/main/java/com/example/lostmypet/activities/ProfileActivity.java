package com.example.lostmypet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.example.lostmypet.interfaces.OnFragmentActivityCommunication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    public final static int PICK_PHOTO_CODE = 1046;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private ImageView userImage;
    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Get the firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;

        //Get the image and username from UI
        usernameTextView = (TextView) findViewById(R.id.tv_username);
        userImage = (ImageView) findViewById(R.id.civ_profile_image);

        //Set username on screen
        String message = currentUser.getDisplayName();
        usernameTextView.setText(message);

        //Get the firebase storage reference and verify if the user has a profile pic to get
        storageReference = FirebaseStorage.getInstance().getReference("Users/"+currentUser.getUid());

        if(storageReference!=null) {
           storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString();
                    Glide.with(getApplicationContext()).load(imageURL).into(userImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "The user does not have a profile image or it could not be loaded.",
                            Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public void onClickLogOut(View view){
        Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
        startActivity(intent);
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
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                onPickPhoto();
                Toast.makeText(ProfileActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(ProfileActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
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
                        Uri photoUri = result.getData().getData();

                        //Put the image in the ImageView on screen
                        userImage.setImageURI(photoUri);

                        //Save the image in Firebase Storage
                        storageReference = FirebaseStorage.getInstance().getReference("Users/"+currentUser.getUid());
                        storageReference.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "User Profile updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }}
            });

    // Trigger gallery selection for a photo
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


}