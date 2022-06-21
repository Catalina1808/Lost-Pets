package com.example.lostmypet.activities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostmypet.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditAnnouncementActivityTest {

    @Rule
    public ActivityScenarioRule<MyAnnouncementsActivity> myAnnouncementsActivity=
            new ActivityScenarioRule<>(MyAnnouncementsActivity.class);

    @Test
    public void isLayoutDisplayed() {
        onView(withId(R.id.layout_add_announcement)).check(matches(isDisplayed()));
    }


    @Test
    public void areButtonsDisplayed() {
        onView(withId(R.id.btn_add_image)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_add_location)).perform(scrollTo()).check(matches(isDisplayed()));
    }


    @Test
    public void areEditTextsDisplayed() {
        onView(withId(R.id.edt_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edt_description)).check(matches(isDisplayed()));
    }

    @Test
    public void areSpinnersDisplayed() {
        onView(withId(R.id.spn_type)).check(matches(isDisplayed()));
        onView(withId(R.id.spn_animal)).check(matches(isDisplayed()));
        onView(withId(R.id.spn_gender)).check(matches(isDisplayed()));
    }

    @Before
    public void openFromMyAnnouncementsActivity() throws InterruptedException {
        Thread.sleep(3000);
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
                                        View button = view.findViewById(R.id.btn_edit);
                                        if(button!=null)
                                            button.performClick();
                                    }
                                })
                );
        closeSoftKeyboard();
    }

}