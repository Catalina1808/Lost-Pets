package com.example.lostmypet.activities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import android.view.View;
import android.widget.Button;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostmypet.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ViewAnnouncementActivityTest {
    @Rule
    public ActivityScenarioRule<AllAnnouncementsActivity> allAnnouncementsActivity=
            new ActivityScenarioRule<>(AllAnnouncementsActivity.class);

    @Test
    public void isLayoutDisplayed() {
        onView(withId(R.id.layout_view_announcement)).check(matches(isDisplayed()));
    }

    @Test
    public void areImagesDisplayed() {
        onView(withId(R.id.imv_pet)).check(matches(isDisplayed()));
        onView(withId(R.id.imv_user)).check(matches(isDisplayed()));
    }

    @Test
    public void areButtonsDisplayed() {
        onView(withId(R.id.btn_comments)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_add_location)).check(matches(isDisplayed()));
    }


    @Test
    public void areTextViewsDisplayed() {
        onView(withId(R.id.tv_name)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_type)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_description)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_city)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_username)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_phone)).check(matches(isDisplayed()));
    }

    @Before
    public void openFromAllAnnouncementsActivity(){
        closeSoftKeyboard();
        onView(withId(R.id.rv_announcements))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                new ViewAction() {
                                    @Override
                                    public Matcher<View> getConstraints() {
                                        return null;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "Click on specific button";
                                    }

                                    @Override
                                    public void perform(UiController uiController, View view) {
                                        View button = view.findViewById(R.id.btn_more);
                                        if(button!=null)
                                            button.performClick();
                                    }
                                })
                );
    }

}