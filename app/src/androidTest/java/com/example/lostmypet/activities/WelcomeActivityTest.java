package com.example.lostmypet.activities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostmypet.R;

import org.junit.Rule;
import org.junit.Test;

public class WelcomeActivityTest {
    @Rule
    public ActivityScenarioRule<WelcomeActivity> welcomeActivity =
            new ActivityScenarioRule<>(WelcomeActivity.class);

    public void navToFragment(int buttonId, int layoutId) {
        onView(withId(buttonId)).perform(click());
        onView(withId(layoutId)).check(matches(isDisplayed()));
    }

    public void backPressFromFragment(int buttonId, int layoutId) {
        onView(withId(buttonId)).perform(click());
        onView(withId(layoutId)).check(matches(isDisplayed()));
        closeSoftKeyboard();
        pressBack();
        onView(withId(R.id.layout_welcome)).check(matches(isDisplayed()));
    }

    @Test
    public void navToLoginFragment(){
        navToFragment(R.id.btn_login, R.id.layout_login);
    }

    @Test
    public void backPressFromLoginFragment(){
        backPressFromFragment(R.id.btn_login, R.id.layout_login);
    }

    @Test
    public void navToRegisterFragment(){
        navToFragment(R.id.btn_register, R.id.layout_register);
    }

    @Test
    public void backPressFromRegisterFragment(){
        backPressFromFragment(R.id.btn_register, R.id.layout_register);
    }

    @Test
    public void isLayoutDisplayed() {
        onView(withId(R.id.layout_welcome)).check(matches(isDisplayed()));
    }

    @Test
    public void isLoginButtonDisplayed() {
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
    }

    @Test
    public void isRegisterButtonDisplayed() {
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
    }

}