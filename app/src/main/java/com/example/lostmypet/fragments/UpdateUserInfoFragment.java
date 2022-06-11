package com.example.lostmypet.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lostmypet.DAO.DAOUser;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.UtilsValidators;
import com.example.lostmypet.interfaces.OnFragmentActivityCommunication;
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


public class UpdateUserInfoFragment extends Fragment {

    public static final String TAG_FRAGMENT_UPDATE_USER_INFO = "TAG_FRAGMENT_UPDATE_USER_INFO";
    private OnFragmentActivityCommunication activityCommunication;

    private ProgressBar progressBar;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText usernameEditText;
    private Button saveChangesButton;
    private FirebaseUser firebaseUser;
    private String password;
    private User user;

    public static UpdateUserInfoFragment newInstance() {
        UpdateUserInfoFragment fragment = new UpdateUserInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUser();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentActivityCommunication) {
            activityCommunication = (OnFragmentActivityCommunication) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        firebaseUser = mAuth.getCurrentUser();

        getUIElements(view);
        getUser();

        saveChangesButton.setOnClickListener(v -> {
            if (validateFields()) {
                showAlertDialog();
            }
        });

        emailEditText.setText(firebaseUser.getEmail());
        view.findViewById(R.id.btn_change_password).setOnClickListener(v -> goToUpdatePassword());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_user_info, container, false);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Password needed!");
        builder.setMessage("You have to enter your password to apply changes!");

        // Set up the input
        EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpToPx(15), 0, dpToPx(15), 0);
        input.setLayoutParams(lp);
        FrameLayout container = new FrameLayout(getContext());
        container.addView(input);

        builder.setView(container);
        builder.setIcon(R.drawable.ic_paw);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            password = input.getText().toString();
            if(password.isEmpty()){
                Toast.makeText(getContext(), "No password entered!",
                        Toast.LENGTH_SHORT).show();
            } else {
                updateFireBaseUser();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(getContext(), "Your changes are not saved!",
                    Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void getUIElements(View view) {
        progressBar = view.findViewById(R.id.pb_loading);
        emailEditText = view.findViewById(R.id.edt_email);
        phoneEditText = view.findViewById(R.id.edt_phone);
        usernameEditText = view.findViewById(R.id.edt_username);
        saveChangesButton = view.findViewById(R.id.btn_save);
    }


    private void getUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://lostmypet-32687-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference databaseReferenceLocations = database
                .getReference(User.class.getSimpleName());

        databaseReferenceLocations.addValueEventListener(new ValueEventListener() {
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

    private boolean validateFields() {
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
                    Toast.makeText(getContext(), "Profile updated!",
                            Toast.LENGTH_SHORT).show());

            if(!emailEditText.getText().toString().equals(firebaseUser.getEmail())) {
                firebaseUser.updateEmail(emailEditText.getText().toString())
                        .addOnCompleteListener(success -> {
                            Timber.w("Email updated!");
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            AlertDialog.Builder builder = new AlertDialog
                                                    .Builder(requireContext());
                                            builder.setTitle("Almost done")
                                                    .setMessage("We have sent an email with a confirmation link to your email address. Open it up to update your account.")
                                                    .setIcon(R.drawable.ic_email)
                                                    .setPositiveButton("Ok", (dialog, which) -> {
                                                    });
                                            builder.create().show();
                                            Toast.makeText(getContext(), "Email sent!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }).addOnFailureListener(failure ->
                                Timber.w("Email could not be updated!"));
            }

        }).addOnFailureListener(failure->
                Toast.makeText(getContext(), "Wrong password!",
                        Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.GONE);
    }

    private void goToUpdatePassword() {
        if (activityCommunication != null) {
            activityCommunication.onAddFragment(UpdatePasswordFragment.TAG_FRAGMENT_UPDATE_PASSWORD);
        }
    }

}