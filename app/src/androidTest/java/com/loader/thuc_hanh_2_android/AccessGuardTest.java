package com.loader.thuc_hanh_2_android;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

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
public class AccessGuardTest {

    private SessionManager session;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        session = new SessionManager(context);
        session.clear();
    }

    @Test
    public void noSession_openingAdminScreen_goesToLogin() {
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void employee_openingAdminScreen_isRedirectedToEmployeeHome() {
        session.saveSession(2, 1, "nv1", Account.ROLE_EMPLOYEE);
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            onView(withTagValue(is((Object) "hello")))
                    .check(matches(withText(containsString("EMPLOYEE"))));
        }
    }

    @Test
    public void admin_openingEmployeeScreen_isRedirectedToAdminHome() {
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
        try (ActivityScenario<EmployeeHomeActivity> s =
                     ActivityScenario.launch(EmployeeHomeActivity.class)) {
            onView(withTagValue(is((Object) "hello")))
                    .check(matches(withText(containsString("ADMIN"))));
        }
    }

    @Test
    public void admin_openingAdminScreen_isAllowed() {
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            onView(withTagValue(is((Object) "hello")))
                    .check(matches(withText(containsString("admin"))));
        }
    }

    @Test
    public void logout_clearsSession_andShowsLogin() {
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            onView(withTagValue(is((Object) "logout"))).perform(click());
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
            assertFalse(session.isLoggedIn());
        }
    }
}
