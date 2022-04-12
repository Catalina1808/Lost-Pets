package com.example.lostmypet.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lostmypet.R;
import com.example.lostmypet.activities.ProfileActivity;
import com.example.lostmypet.helpers.UtilsValidators;
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

        view.findViewById(R.id.btn_login).setOnClickListener(v -> validateEmailAndPassword());

        progressBar = view.findViewById(R.id.pb_loading);

        emailEditText = view.findViewById(R.id.edt_email);
        passwordEditText = view.findViewById(R.id.edt_password);
    }



    private void validateEmailAndPassword() {
        if(getView() == null) {
            return;
        }

        emailEditText = getView().findViewById(R.id.edt_email);
        passwordEditText = getView().findViewById(R.id.edt_password);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!UtilsValidators.isValidEmail(email)) {
            emailEditText.setError("Invalid Email");
            return;
        } else {
            emailEditText.setError(null);
        }

        if(!UtilsValidators.isValidPassword(password)) {
            passwordEditText.setError("Invalid Password");
            return;
        } else {
            passwordEditText.setError(null);
        }

        loginFirebaseUser(email, password);

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


    void saveEmail(EditText text) {
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

