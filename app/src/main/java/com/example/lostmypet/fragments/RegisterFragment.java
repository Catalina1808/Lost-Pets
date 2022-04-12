package com.example.lostmypet.fragments;

import android.os.Bundle;
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
import com.example.lostmypet.helpers.UtilsValidators;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {
    public static final String TAG_REGISTER_FRAGMENT ="TAG_REGISTER_FRAGMENT";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressBar progressBar;

    public static RegisterFragment newInstance() {

        Bundle args = new Bundle();

        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.pb_loading);

        view.findViewById(R.id.btn_register).setOnClickListener(v -> validateEmailAndPassword());
    }

    private void validateEmailAndPassword(){
        if(getView()==null)
        {
            return;
        }
        EditText emailEditText=getView().findViewById(R.id.edt_email);
        EditText passwordEditText=getView().findViewById(R.id.edt_password);
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();

        if(!UtilsValidators.isValidEmail(email))
        {
            emailEditText.setError("Invalid Email");
            return;
        }
        else
        {
            emailEditText.setError(null);
        }

        if(!UtilsValidators.isValidPassword(password))
        {
            passwordEditText.setError("Invalid Password");
            return;
        }
        else
        {
            passwordEditText.setError(null);
        }

        createFireBaseUser(email, password);
    }

    private void createFireBaseUser(String email, String password){
        if(getActivity()==null)
        {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task->{
            progressBar.setVisibility(View.GONE);
            if(task.isSuccessful())
            {
                FirebaseUser user=mAuth.getCurrentUser();

                Toast.makeText(getContext(), "Authentification success.", Toast.LENGTH_SHORT).show();
                assert user != null;
                user.sendEmailVerification()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                EditText usernameEditText= requireView().findViewById(R.id.edt_username);
                                String username=usernameEditText.getText().toString();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                user.updateProfile(profileUpdates);

                                AlertDialog.Builder builder= new AlertDialog.Builder(requireContext());
                                builder.setTitle("Almost done")
                                        .setMessage("We have sent an email with a confirmation link to your email address. Open it up to activate your account.")
                                        .setIcon(R.drawable.ic_email)
                                        .setPositiveButton("Ok", (dialog, which) -> {
                                        });
                                builder.create().show();
                                Toast.makeText(getContext(), "Email sent!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
            {
                Toast.makeText(getContext(), "Authentification failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
