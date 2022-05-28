package com.example.lostmypet.activities;

import static com.example.lostmypet.fragments.LoginFragment.TAG_LOGIN_FRAGMENT;
import static com.example.lostmypet.fragments.RegisterFragment.TAG_REGISTER_FRAGMENT;
import static com.example.lostmypet.fragments.UpdatePasswordFragment.TAG_FRAGMENT_UPDATE_PASSWORD;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lostmypet.R;
import com.example.lostmypet.fragments.LoginFragment;
import com.example.lostmypet.fragments.RegisterFragment;
import com.example.lostmypet.fragments.UpdatePasswordFragment;
import com.example.lostmypet.fragments.UpdateUserInfoFragment;
import com.example.lostmypet.interfaces.OnFragmentActivityCommunication;

public class UpdateUserActivity extends AppCompatActivity implements OnFragmentActivityCommunication {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        onAddUpdateInfoFragment();
    }

    private void onAddUpdateInfoFragment(){
        FragmentManager fragmentManager= getSupportFragmentManager();

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fly_container, UpdateUserInfoFragment.newInstance(),
                UpdateUserInfoFragment.TAG_FRAGMENT_UPDATE_USER_INFO);
        fragmentTransaction.commit();

    }

    @Override
    public void onAddFragment(String TAG){
        FragmentManager fragmentManager= getSupportFragmentManager();

        Fragment fragment;

        if (TAG_FRAGMENT_UPDATE_PASSWORD.equals(TAG)) {
            fragment = UpdatePasswordFragment.newInstance();
        } else {
            return;
        }

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fly_container,fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }
}