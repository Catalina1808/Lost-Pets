package com.example.lostmypet.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lostmypet.R;
import com.example.lostmypet.activities.WelcomeActivity;
import com.example.lostmypet.interfaces.OnFragmentActivityCommunication;

import java.util.Locale;


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

        view.findViewById(R.id.btn_romanian).setOnClickListener(view1 -> setLocale("ro"));
        view.findViewById(R.id.btn_english).setOnClickListener(view12 -> setLocale("en"));
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        startActivity(new Intent(getActivity(), WelcomeActivity.class));
        requireActivity().finish();
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
