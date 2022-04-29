package com.example.lostmypet.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lostmypet.R;
import com.example.lostmypet.interfaces.OnFragmentActivityCommunication;


public class WelcomeFragment extends Fragment {
    public static final String TAG_FRAGMENT_WELCOME="TAG_FRAGMENT_WELCOME";
    private OnFragmentActivityCommunication activityCommunication;

    public static WelcomeFragment newInstance() {

        Bundle args = new Bundle();

        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnFragmentActivityCommunication){
            activityCommunication= (OnFragmentActivityCommunication) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.findViewById(R.id.btn_register).setOnClickListener(v -> goToRegister());

        view.findViewById(R.id.btn_login).setOnClickListener(v -> goToLogin());

    }

    private  void goToRegister(){
        if(activityCommunication !=null){
            activityCommunication.onAddFragment(RegisterFragment.TAG_REGISTER_FRAGMENT);
        }
    }

        private void goToLogin () {
            if (activityCommunication != null) {
                activityCommunication.onAddFragment(LoginFragment.TAG_LOGIN_FRAGMENT);
            }
        }

}
