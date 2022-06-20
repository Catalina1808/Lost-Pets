package com.example.lostmypet.activities;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.lostmypet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;

public class MainPageActivityTest {

    @Rule
    public ActivityScenarioRule<MainPageActivity> mainPageActivity =
            new ActivityScenarioRule<>(MainPageActivity.class);

    public void navToActivity(int buttonId, int layoutId) {
        onView(withId(buttonId)).perform(click());
        onView(withId(layoutId)).check(matches(isDisplayed()));
    }

    public void backPressFromActivity(int buttonId, int layoutId) {
        onView(withId(buttonId)).perform(click());
        onView(withId(layoutId)).check(matches(isDisplayed()));
        closeSoftKeyboard();
        pressBack();
        onView(withId(R.id.layout_main_page)).check(matches(isDisplayed()));
    }

    @Test
    public void isLayoutDisplayed() {
        onView(withId(R.id.layout_main_page)).check(matches(isDisplayed()));
    }

    @Test
    public void isMessageDisplayed() {
        onView(withId(R.id.tv_hello)).check(matches(withText(R.string.hello)));
    }

    @Test
    public void areButtonsDisplayed() {
        //isDisplayed() not working for this button because is overlayed
        onView(withId(R.id.btn_change_picture)).check(matches(withEffectiveVisibility
                (ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.btn_account_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_add_announcement)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_my_announcements)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_all_announcements)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_favorites)).check(matches(isDisplayed()));
    }

    @Test
    public void navToAddAnnouncementActivity() {
       navToActivity(R.id.btn_add_announcement, R.id.layout_add_announcement);
    }
    @Test
    public void navToAllAnnouncementsActivity() {
        navToActivity(R.id.btn_all_announcements, R.id.layout_all_announcements);
    }
    @Test
    public void navToMyAnnouncementsActivity() {
        navToActivity(R.id.btn_my_announcements, R.id.layout_announcements_list);
    }
    @Test
    public void navToFavoritesActivity() {
        navToActivity(R.id.btn_favorites, R.id.layout_announcements_list);
    }

    @Test
    public void backPressFromAddAnnouncementActivity() {
        backPressFromActivity(R.id.btn_add_announcement, R.id.layout_add_announcement);
    }
    @Test
    public void backPressFromAllAnnouncementsActivity() {
        backPressFromActivity(R.id.btn_all_announcements, R.id.layout_all_announcements);
    }
    @Test
    public void backPressFromMyAnnouncementsActivity() {
        backPressFromActivity(R.id.btn_my_announcements, R.id.layout_announcements_list);
    }
    @Test
    public void backPressFromFavoritesActivity() {
        backPressFromActivity(R.id.btn_favorites, R.id.layout_announcements_list);
    }

    @Test
    public void isCorrectUsername() {
       // ActivityScenario<MainPageActivity> mainPageActivity = ActivityScenario.launch(MainPageActivity.class);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        onView(withId(R.id.tv_username))
                .check(matches(withText(Objects.requireNonNull(currentUser).getDisplayName())));
    }
}