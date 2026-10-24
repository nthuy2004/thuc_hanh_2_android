package com.loader.thuc_hanh_2_android.ui.employee;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.session.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EmployeeListActivityTest {

    private SessionManager session;
    private AppDatabase db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        session = new SessionManager(context);
        db = AppDatabase.getInstance(context);
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
    }

    private int adapterCount(ActivityScenario<EmployeeListActivity> scenario) {
        final int[] count = {-1};
        scenario.onActivity(a -> {
            RecyclerView rv = a.findViewById(R.id.rv_employees);
            count[0] = rv.getAdapter().getItemCount();
        });
        return count[0];
    }

    @Test
    public void admin_seesSeededEmployees() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            Thread.sleep(800);
            assertEquals(2, adapterCount(s));
            onView(withText("Nguyen Van An")).check(matches(isDisplayed()));
            onView(withText("Mã: NV001")).check(matches(isDisplayed()));
            onView(withText("Tran Thi Binh")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void softDeletedEmployee_isNotListed() throws InterruptedException {
        Account acc = db.accountDao().getByUsername("nv2");
        Employee e = db.employeeDao().getByAccountId(acc.id);
        e.isActive = false;
        db.employeeDao().update(e);
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            Thread.sleep(800);
            assertEquals(1, adapterCount(s));
        } finally {
            e.isActive = true;
            db.employeeDao().update(e);
        }
    }

    @Test
    public void emptyList_showsEmptyState_withoutCrash() throws InterruptedException {
        List<Employee> originals = new java.util.ArrayList<>();
        for (String u : new String[]{"nv1", "nv2"}) {
            Employee e = db.employeeDao().getByAccountId(db.accountDao().getByUsername(u).id);
            originals.add(e);
            e.isActive = false;
            db.employeeDao().update(e);
        }
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            Thread.sleep(800);
            onView(withText(R.string.employee_list_empty)).check(matches(isDisplayed()));
        } finally {
            for (Employee e : originals) {
                e.isActive = true;
                db.employeeDao().update(e);
            }
        }
    }

    @Test
    public void employeeRole_isRedirected_andCannotSeeList() throws InterruptedException {
        session.saveSession(2, 1, "nv1", Account.ROLE_EMPLOYEE);
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            Thread.sleep(1500); // cho man hinh bi chan ket thuc va Employee Home hien len
            onView(withId(R.id.btn_check_in)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void adminDashboard_buttonOpensEmployeeList() throws InterruptedException {
        try (ActivityScenario<com.loader.thuc_hanh_2_android.AdminDashboardActivity> s =
                     ActivityScenario.launch(
                             com.loader.thuc_hanh_2_android.AdminDashboardActivity.class)) {
            onView(withTagValue(is((Object) "manage_employees"))).perform(click());
            Thread.sleep(800);
            onView(withText(containsString("Nguyen Van An"))).check(matches(isDisplayed()));
        }
    }
}
