package com.example.lostmypet.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
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

public class UpdatePasswordFragment extends Fragment {
    public static final String TAG_FRAGMENT_UPDATE_PASSWORD="TAG_FRAGMENT_UPDATE_PASSWORD";

    private ProgressBar progressBar;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private Button saveChangesButton;
    private FirebaseUser firebaseUser;
    private User user;

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


        if(!UtilsValidators.isValidPassword(newPasswordEditText.getText().toString()))
        {
            newPasswordEditText.setError("Invalid Password");
            isValidated=false;
        }
        return isValidated;
    }

    private void updateFireBaseUser(){

        progressBar.setVisibility(View.VISIBLE);

        firebaseUser.updatePassword(newPasswordEditText.getText().toString());

        progressBar.setVisibility(View.GONE);
    }

}