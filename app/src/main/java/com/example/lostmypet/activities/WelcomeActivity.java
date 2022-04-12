package com.example.lostmypet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import com.example.lostmypet.R;
import com.example.lostmypet.fragments.LoginFragment;
import com.example.lostmypet.fragments.RegisterFragment;
import com.example.lostmypet.fragments.WelcomeFragment;
import com.example.lostmypet.interfaces.OnFragmentActivityCommunication;

import static com.example.lostmypet.fragments.LoginFragment.TAG_LOGIN_FRAGMENT;
import static com.example.lostmypet.fragments.RegisterFragment.TAG_REGISTER_FRAGMENT;

public class WelcomeActivity extends AppCompatActivity implements OnFragmentActivityCommunication {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        onAddWelcomeFragment();

    }

    private void onAddWelcomeFragment(){
        FragmentManager fragmentManager= getSupportFragmentManager();

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fly_container, WelcomeFragment.newInstance(), WelcomeFragment.TAG_FRAGMENT_WELCOME);
        fragmentTransaction.commit();
    }

    @Override
    public void onAddFragment(String TAG){
        FragmentManager fragmentManager= getSupportFragmentManager();

        Fragment fragment;

        switch (TAG){
            case TAG_REGISTER_FRAGMENT:{
                fragment= RegisterFragment.newInstance();
                break;
            }

            case TAG_LOGIN_FRAGMENT: {
                fragment = LoginFragment.newInstance();
                break;
            }
            default: return;
        }

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fly_container,fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

}