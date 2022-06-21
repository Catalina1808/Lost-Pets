package com.example.lostmypet.fragments;

import android.os.Bundle;
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

import com.example.lostmypet.DAO.DAOUser;
import com.example.lostmypet.R;
import com.example.lostmypet.helpers.UtilsValidators;
import com.example.lostmypet.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends Fragment {
    public static final String TAG_REGISTER_FRAGMENT ="TAG_REGISTER_FRAGMENT";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressBar progressBar;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneEditText;
    private EditText usernameEditText;

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

        emailEditText = view.findViewById(R.id.edt_email);
        passwordEditText = view.findViewById(R.id.edt_password);
        phoneEditText = view.findViewById(R.id.edt_phone);
        usernameEditText = view.findViewById(R.id.edt_username);

        view.findViewById(R.id.btn_register).setOnClickListener(view1 -> {
            if(validateFields()){
                createFireBaseUser();
            }
        });
    }

    private boolean validateFields(){
        boolean isValidated = getView() != null;

        if(!UtilsValidators.isValidEmail(emailEditText.getText().toString()))
        {
            emailEditText.setError(getString(R.string.invalid_email));
            isValidated=false;
        }
        if(!UtilsValidators.isValidPassword(passwordEditText.getText().toString()))
        {
            passwordEditText.
                    setError(getString(R.string.password_conditions));
            isValidated=false;
        }
        if(!UtilsValidators.isValidPhone(phoneEditText.getText().toString()))
        {
            phoneEditText.setError(getString(R.string.invalid_phone));
            isValidated=false;
        }
        if(TextUtils.isEmpty(usernameEditText.getText().toString()))
        {
            usernameEditText.setError(getString(R.string.warning_enter_username));
            isValidated=false;
        }

        return isValidated;
    }

    private void createFireBaseUser(){
        if(getActivity()==null)
        {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(getActivity(), task->{
            progressBar.setVisibility(View.GONE);
            if(task.isSuccessful())
            {
                FirebaseUser user=mAuth.getCurrentUser();

                Toast.makeText(getContext(), R.string.auth_success, Toast.LENGTH_SHORT).show();
                assert user != null;
                user.sendEmailVerification()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                String username=usernameEditText.getText().toString();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                                user.updateProfile(profileUpdates);

                                DAOUser daoUser = new DAOUser();
                                User userModel = new User(username, phoneEditText.getText().toString());
                                daoUser.add(userModel, user.getUid());

                                AlertDialog.Builder builder= new AlertDialog.Builder(requireContext());
                                builder.setTitle(R.string.almost_done)
                                        .setMessage(R.string.we_sent_confirmation_email)
                                        .setIcon(R.drawable.ic_email)
                                        .setPositiveButton("Ok", (dialog, which) -> {
                                        });
                                builder.create().show();
                                Toast.makeText(getContext(), R.string.email_sent,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
            {
                Toast.makeText(getContext(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
