package com.example.lostmypet.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.lostmypet.R;
import com.example.lostmypet.activities.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {


    public static final String TAG_LOGIN_FRAGMENT = "TAG_LOGIN_FRAGMENT";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ProgressBar progressBar;

    private EditText emailEditText;
    private EditText passwordEditText;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        
    }

  @Override
    public void onStart() {
        super.onStart();
        mAuth.getCurrentUser();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        emailEditText.setText(preferences.getString("EMAIL", ""));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.pb_loading);
        emailEditText = view.findViewById(R.id.edt_email);
        passwordEditText = view.findViewById(R.id.edt_password);

        view.findViewById(R.id.btn_login).setOnClickListener(v ->
                loginFirebaseUser(emailEditText.getText().toString(),
                        passwordEditText.getText().toString()));
        view.findViewById(R.id.btn_forgot_password).setOnClickListener(v -> resetPassword());
    }


    private void resetPassword(){
        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Enter your registered email!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(success-> {
                    AlertDialog.Builder builder= new AlertDialog.Builder(requireContext());
                    builder.setTitle("Almost done")
                            .setMessage("We have sent an email with instructions to reset your password!")
                            .setIcon(R.drawable.ic_email)
                            .setPositiveButton("Ok", (dialog, which) -> {
                            });
                    builder.create().show();
                })
                .addOnFailureListener(failure ->
                        Toast.makeText(getContext(),
                                "Failed to send reset email!",
                                Toast.LENGTH_SHORT).show());
    }

    private void loginFirebaseUser(String email, String password) {
        if (getActivity() == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);

                        FirebaseUser user = mAuth.getCurrentUser();

                        assert user != null;
                        if(user.isEmailVerified()) {
                            Toast.makeText(getContext(), "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            goToSecondActivity();
                        }else {
                            Toast.makeText(getContext(), "Email is not verified.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void goToSecondActivity() {
        startActivity(new Intent(getActivity(), ProfileActivity.class));
        requireActivity().finish();
    }


    private void saveEmail(EditText text) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("EMAIL", text.getText().toString());
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();

        saveEmail(emailEditText);
    }

}

