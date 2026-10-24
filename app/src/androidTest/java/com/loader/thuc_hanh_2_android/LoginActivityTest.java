package com.loader.thuc_hanh_2_android;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class LoginActivityTest {

    private SessionManager session;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        session = new SessionManager(context);
        session.clear();
    }

    private void login(String user, String pass) {
        onView(withId(R.id.edt_username)).perform(replaceText(user));
        onView(withId(R.id.edt_password)).perform(replaceText(pass));
        closeSoftKeyboard();
        onView(withId(R.id.btn_login)).perform(click());
    }

    private void waitUntilLoggedIn() throws InterruptedException {
        for (int i = 0; i < 50 && !session.isLoggedIn(); i++) {
            Thread.sleep(100);
        }
    }

    @Test
    public void emptyUsername_showsError() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            login("", "abc");
            onView(withId(R.id.edt_username)).check(matches(
                    hasErrorText("Vui lòng nhập tên đăng nhập")));
            assertFalse(session.isLoggedIn());
        }
    }

    @Test
    public void emptyPassword_showsError() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            login("admin", "");
            onView(withId(R.id.edt_password)).check(matches(
                    hasErrorText("Vui lòng nhập mật khẩu")));
            assertFalse(session.isLoggedIn());
        }
    }

    @Test
    public void wrongPassword_staysOnLogin_noSession() throws InterruptedException {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            login("admin", "saimatkhau");
            Thread.sleep(1500);
            assertFalse(session.isLoggedIn());
            onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void adminLogin_savesSession_andOpensDashboard() throws InterruptedException {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            login("admin", "admin123");
            waitUntilLoggedIn();
            assertTrue(session.isLoggedIn());
            assertEquals(Account.ROLE_ADMIN, session.getRole());
            assertEquals("admin", session.getUsername());
            assertEquals(SessionManager.NO_ID, session.getEmployeeId());
            onView(withId(R.id.cv_manage_employees)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void employeeLogin_savesEmployeeId_andOpensHome() throws InterruptedException {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            login("nv1", "123456");
            waitUntilLoggedIn();
            assertTrue(session.isLoggedIn());
            assertEquals(Account.ROLE_EMPLOYEE, session.getRole());
            assertTrue(session.getEmployeeId() > 0);
            onView(withId(R.id.btn_check_in)).check(matches(isDisplayed()));
        }
    }
}
