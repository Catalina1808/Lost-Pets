package com.example.lostmypet.activities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostmypet.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

public class AllAnnouncementsActivityTest {
    @Rule
    public ActivityScenarioRule<AllAnnouncementsActivity> allAnnouncementsPageActivity =
            new ActivityScenarioRule<>(AllAnnouncementsActivity.class);

    @Test
    public void isLayoutDisplayed() {
        onView(withId(R.id.layout_all_announcements)).check(matches(isDisplayed()));
    }

    @Test
    public void isTypeFilterDisplayed() {
        onView(withId(R.id.rg_type)).check(matches(isDisplayed()));
        onView(withId(R.id.rb_lost)).check(matches(isDisplayed()));
        onView(withId(R.id.rb_found)).check(matches(isDisplayed()));
        onView(withId(R.id.rb_give_away)).check(matches(isDisplayed()));
    }

    @Test
    public void isTypeCorrectDisplayed() {
        onView(withId(R.id.rb_lost)).check(matches(withText(R.string.lost)));
        onView(withId(R.id.rb_found)).check(matches(withText(R.string.found)));
        onView(withId(R.id.rb_give_away)).check(matches(withText(R.string.give_away)));
    }

    @Test
    public void isCityFilterDisplayed() {
        onView(withId(R.id.edt_city)).check(matches(isDisplayed()));
    }

    @Test
    public void areAnimalTextViewsCorrectDisplayed() {
        onView(withId(R.id.tv_dog)).check(matches(withText(R.string.dog)));
        onView(withId(R.id.tv_cat)).check(matches(withText(R.string.cat)));
        onView(withId(R.id.tv_bird)).check(matches(withText(R.string.bird)));
        onView(withId(R.id.tv_rabbit)).check(matches(withText(R.string.rabbit)));
        onView(withId(R.id.tv_other)).check(matches(withText(R.string.other)));
    }

    @Test
    public void navToActivity(){
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
        onView(withId(R.id.layout_view_announcement)).check(matches(isDisplayed()));
    }

}
