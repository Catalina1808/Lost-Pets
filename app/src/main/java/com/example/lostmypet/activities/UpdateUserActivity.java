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
        builder.setTitle(R.string.password_needed);
        builder.setMessage(R.string.warning_password_update);

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
                Toast.makeText(this, R.string.warning_enter_password,
                        Toast.LENGTH_SHORT).show();
            } else {
                updateFireBaseUser();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(this, R.string.changes_not_saved,
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
            emailEditText.setError(getString(R.string.invalid_email));
            isValidated = false;
        }

        if (!UtilsValidators.isValidPhone(phoneEditText.getText().toString())) {
            phoneEditText.setError(getString(R.string.invalid_phone));
            isValidated = false;
        }

        if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
            usernameEditText.setError(getString(R.string.warning_enter_username));
            isValidated = false;
        }

        return isValidated;
    }

    private boolean validatePasswordFields(){
        if(oldPasswordEditText.getText().toString().isEmpty()){
            oldPasswordEditText.setError(getString(R.string.warning_enter_old_password));
            return false;
        }
        if(newPasswordEditText.getText().toString().equals(oldPasswordEditText.getText().toString())){
            newPasswordEditText.setError(getString(R.string.warning_same_passwords));
            return false;
        }
        if(!UtilsValidators.isValidPassword(newPasswordEditText.getText().toString())) {
            newPasswordEditText
                    .setError(getString(R.string.password_conditions));
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
                    Toast.makeText(this, R.string.profile_updated,
                            Toast.LENGTH_SHORT).show());

            if(!emailEditText.getText().toString().equals(firebaseUser.getEmail())) {
                firebaseUser.updateEmail(emailEditText.getText().toString())
                        .addOnCompleteListener(success -> {
                            Timber.w(getString(R.string.email_updated));
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            AlertDialog.Builder builder = new AlertDialog
                                                    .Builder(this);
                                            builder.setTitle(R.string.almost_done)
                                                    .setMessage(R.string.we_send_update_email)
                                                    .setIcon(R.drawable.ic_email)
                                                    .setPositiveButton("Ok", (dialog, which) -> {
                                                    });
                                            builder.create().show();
                                            Toast.makeText(this, R.string.email_sent,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }).addOnFailureListener(failure ->
                                Timber.w(getString(R.string.email_could_not_update)));
            }

        }).addOnFailureListener(failure->
                Toast.makeText(this, R.string.wrong_password,
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
                                Toast.makeText(this, R.string.password_updated,
                                        Toast.LENGTH_SHORT).show())).addOnFailureListener(failure->
                Toast.makeText(this, R.string.wrong_password,
                        Toast.LENGTH_SHORT).show());

        progressBar.setVisibility(View.GONE);
    }
}