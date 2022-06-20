package com.example.lostmypet.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lostmypet.DAO.DAOUser;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.UtilsValidators;
import com.example.lostmypet.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class UpdateUserActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText usernameEditText;
    private Button saveInfoButton;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private Button savePasswordButton;

    private FirebaseUser firebaseUser;
    private String password;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        firebaseUser = mAuth.getCurrentUser();

        getUIElements();
        getUser();

        saveInfoButton.setOnClickListener(v -> {
            if (validateInfoFields()) {
                showAlertDialog();
            }
        });

        savePasswordButton.setOnClickListener(v -> {
            if(validatePasswordFields()){
                updateFireBasePassword();
            }
        });

        emailEditText.setText(firebaseUser.getEmail());

    }

    private void getUIElements() {
        progressBar = findViewById(R.id.pb_loading);
        emailEditText = findViewById(R.id.edt_email);
        phoneEditText = findViewById(R.id.edt_phone);
        usernameEditText = findViewById(R.id.edt_username);
        saveInfoButton = findViewById(R.id.btn_update_info);
        oldPasswordEditText = findViewById(R.id.edt_old_password);
        newPasswordEditText = findViewById(R.id.edt_new_password);
        savePasswordButton = findViewById(R.id.btn_update_password);
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password needed!");
        builder.setMessage("You have to enter your password to apply changes!");

        // Set up the input
        EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int dpToPx = (int) (15 * Resources.getSystem().getDisplayMetrics().density);
        lp.setMargins(dpToPx, 0, dpToPx, 0);
        input.setLayoutParams(lp);
        FrameLayout container = new FrameLayout(this);
        container.addView(input);

        builder.setView(container);
        builder.setIcon(R.drawable.ic_paw);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            password = input.getText().toString();
            if(password.isEmpty()){
                Toast.makeText(this, "No password entered!",
                        Toast.LENGTH_SHORT).show();
            } else {
                updateFireBaseUser();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(this, "Your changes are not saved!",
                    Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void getUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference databaseReferenceLocations = database
                .getReference(User.class.getSimpleName());

        databaseReferenceLocations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot.getKey(), firebaseUser.getUid())) {
                        user = dataSnapshot.getValue(User.class);
                        phoneEditText.setText(Objects.requireNonNull(user).getPhone());
                        usernameEditText.setText(user.getUsername());
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean validateInfoFields() {
        boolean isValidated = true;

        if (!UtilsValidators.isValidEmail(emailEditText.getText().toString())) {
            emailEditText.setError("Invalid Email");
            isValidated = false;
        }

        if (!UtilsValidators.isValidPhone(phoneEditText.getText().toString())) {
            phoneEditText.setError("Invalid Phone");
            isValidated = false;
        }

        if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
            usernameEditText.setError("No username");
            isValidated = false;
        }

        return isValidated;
    }

    private boolean validatePasswordFields(){
        if(oldPasswordEditText.getText().toString().isEmpty()){
            oldPasswordEditText.setError("You should enter the old password!");
            return false;
        }
        if(newPasswordEditText.getText().toString().equals(oldPasswordEditText.getText().toString())){
            newPasswordEditText.setError("The new password should be different!");
            return false;
        }
        if(!UtilsValidators.isValidPassword(newPasswordEditText.getText().toString())) {
            newPasswordEditText
                    .setError("Your passwords should have at least 6 characters and a mix of letters and numbers");
            return false;
        }
        return true;
    }

    private void updateFireBaseUser() {

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(firebaseUser.getEmail()), password);
        firebaseUser.reauthenticate(credential).addOnSuccessListener(task -> {

            DAOUser daoUser = new DAOUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(usernameEditText.getText().toString())
                    .build();
            firebaseUser.updateProfile(profileUpdates);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("username", usernameEditText.getText().toString());
            hashMap.put("phone", phoneEditText.getText().toString());
            daoUser.update(firebaseUser.getUid(), hashMap).addOnSuccessListener(success ->
                    Toast.makeText(this, "Profile updated!",
                            Toast.LENGTH_SHORT).show());

            if(!emailEditText.getText().toString().equals(firebaseUser.getEmail())) {
                firebaseUser.updateEmail(emailEditText.getText().toString())
                        .addOnCompleteListener(success -> {
                            Timber.w("Email updated!");
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            AlertDialog.Builder builder = new AlertDialog
                                                    .Builder(this);
                                            builder.setTitle("Almost done")
                                                    .setMessage("We have sent an email with a confirmation link to your email address. Open it up to update your account.")
                                                    .setIcon(R.drawable.ic_email)
                                                    .setPositiveButton("Ok", (dialog, which) -> {
                                                    });
                                            builder.create().show();
                                            Toast.makeText(this, "Email sent!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }).addOnFailureListener(failure ->
                                Timber.w("Email could not be updated!"));
            }

        }).addOnFailureListener(failure->
                Toast.makeText(this, "Wrong password!",
                        Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.GONE);
    }

    private void updateFireBasePassword(){
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(firebaseUser.getEmail()),
                        oldPasswordEditText.getText().toString());
        firebaseUser.reauthenticate(credential).addOnSuccessListener(success ->
                firebaseUser.updatePassword(newPasswordEditText.getText().toString())
                        .addOnSuccessListener(task ->
                                Toast.makeText(this, "Password updated!",
                                        Toast.LENGTH_SHORT).show())).addOnFailureListener(failure->
                Toast.makeText(this, "Wrong password!",
                        Toast.LENGTH_SHORT).show());

        progressBar.setVisibility(View.GONE);
    }
}