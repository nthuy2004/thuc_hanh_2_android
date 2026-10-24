package com.loader.thuc_hanh_2_android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.session.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityRoutingTest {

    private SessionManager session;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        session = new SessionManager(context);
        session.clear();
    }

    @Test
    public void noSession_goesToLogin() {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void adminSession_goesToAdminHome() {
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.cv_manage_employees)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void employeeSession_goesToEmployeeHome() {
        session.saveSession(2, 1, "nv1", Account.ROLE_EMPLOYEE);
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.btn_check_in)).check(matches(isDisplayed()));
        }
    }
}
