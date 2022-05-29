package com.example.lostmypet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lostmypet.R;
import com.example.lostmypet.helpers.UtilsValidators;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UpdatePasswordFragment extends Fragment {
    public static final String TAG_FRAGMENT_UPDATE_PASSWORD="TAG_FRAGMENT_UPDATE_PASSWORD";

    private ProgressBar progressBar;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private Button saveChangesButton;
    private FirebaseUser firebaseUser;

    public static UpdatePasswordFragment newInstance() {
        UpdatePasswordFragment fragment = new UpdatePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        firebaseUser=mAuth.getCurrentUser();

        getUIElements(view);

        saveChangesButton.setOnClickListener(v -> {
            if(validateFields()){
                updateFireBasePassword();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_password, container, false);
    }

    private void getUIElements(View view){
        progressBar = view.findViewById(R.id.pb_loading);
        oldPasswordEditText = view.findViewById(R.id.edt_old_password);
        newPasswordEditText = view.findViewById(R.id.edt_new_password);
        saveChangesButton = view.findViewById(R.id.btn_save_password);
    }


    private boolean validateFields(){
        boolean isValidated = true;

        if(newPasswordEditText.getText().toString().equals(oldPasswordEditText.getText().toString())){
            newPasswordEditText.setError("The new password should be different!");
            isValidated=false;
        } else if(!UtilsValidators.isValidPassword(newPasswordEditText.getText().toString())) {
            newPasswordEditText
                    .setError("Your passwords should have at least 6 characters and a mix of letters and numbers");
            isValidated = false;
        }
        return isValidated;
    }

    private void updateFireBasePassword(){

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(firebaseUser.getEmail()),
                        oldPasswordEditText.getText().toString());
        firebaseUser.reauthenticate(credential).addOnSuccessListener(success ->
                firebaseUser.updatePassword(newPasswordEditText.getText().toString())
                .addOnSuccessListener(task ->
                        Toast.makeText(getContext(), "Password updated!",
                                Toast.LENGTH_SHORT).show())).addOnFailureListener(failure->
            Toast.makeText(getContext(), "Wrong password!",
                    Toast.LENGTH_SHORT).show());

        progressBar.setVisibility(View.GONE);
    }

}