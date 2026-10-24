package com.loader.thuc_hanh_2_android.ui.salary;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.EmployeeHomeActivity;
import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.session.SessionManager;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.NumberFormat;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class SalaryUiTest {

    private Context context;
    private AppDatabase db;
    private SessionManager session;
    private Employee an;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        AppDatabase.resetForTesting(context);
        db = AppDatabase.getInstance(context);
        session = new SessionManager(context);
        an = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv1").id);
        db.workLogDao().insert(new WorkLog(an.id, "2026-09-01", "08:00:00", "17:00:00", 8,
                WorkLog.STATUS_COMPLETED));
        db.workLogDao().insert(new WorkLog(an.id, "2026-09-02", "08:00:00", "19:00:00", 10,
                WorkLog.STATUS_COMPLETED));
    }

    @After
    public void tearDown() {
        AppDatabase.resetForTesting(context);
        AppDatabase.getInstance(context);
    }

    private void loginAdmin() {
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
    }

    private void loginAn() {
        session.saveSession(an.accountId, an.id, "nv1", Account.ROLE_EMPLOYEE);
    }

    private static void waitUi() throws InterruptedException {
        Thread.sleep(1000);
    }

    private static Matcher<View> markPaidButtonOf(String name) {
        return allOf(withId(R.id.btn_mark_salary_paid),
                isDescendantOfA(allOf(withId(R.id.is), hasDescendant(withText(name)))));
    }

    private static String money(double v) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(v) + " VNĐ";
    }

    /** Dem so dong trong danh sach admin co TextView hien dung noi dung cho truoc. */
    private static int countTexts(ActivityScenario<AdminSalaryActivity> scenario, String text) {
        final int[] count = {0};
        scenario.onActivity(a -> {
            RecyclerView rv = a.findViewById(R.id.rv_admin_salary);
            for (int i = 0; i < rv.getChildCount(); i++) {
                TextView tv = rv.getChildAt(i).findViewById(R.id.tv_salary_month_year);
                if (tv != null && text.contentEquals(tv.getText())) {
                    count[0]++;
                }
            }
        });
        return count[0];
    }

    private void generate(String period) throws InterruptedException {
        onView(withId(R.id.edt_month_year)).perform(replaceText(period));
        closeSoftKeyboard();
        onView(withId(R.id.btn_generate_salary)).perform(click());
        waitUi();
    }

    // ----- Admin -----

    @Test
    public void admin_generate_showsEveryEmployeeWithComputedSalary() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<AdminSalaryActivity> s =
                     ActivityScenario.launch(AdminSalaryActivity.class)) {
            waitUi();
            onView(withText(R.string.salary_admin_empty)).check(matches(isDisplayed()));

            generate("09/2026");

            onView(withText("Nguyen Van An")).check(matches(isDisplayed()));
            onView(withText("Tran Thi Binh")).check(matches(isDisplayed()));
            onView(withText(money(950000))).check(matches(isDisplayed()));
            onView(withText(String.format(Locale.getDefault(), "Tăng ca: %.2f giờ", 2.0)))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void admin_invalidPeriod_showsError_andCreatesNothing() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<AdminSalaryActivity> s =
                     ActivityScenario.launch(AdminSalaryActivity.class)) {
            waitUi();
            onView(withId(R.id.edt_month_year)).perform(replaceText("13/2026"));
            closeSoftKeyboard();
            onView(withId(R.id.btn_generate_salary)).perform(click());
            waitUi();

            onView(withId(R.id.edt_month_year)).check(matches(
                    hasErrorText("Nhập tháng/năm đúng dạng MM/yyyy (VD: 10/2026)")));
            assertEquals(0, db.salaryDao().getByPeriod(13, 2026).size());
        }
    }

    @Test
    public void admin_markPaid_updatesStatusAndHidesButton() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<AdminSalaryActivity> s =
                     ActivityScenario.launch(AdminSalaryActivity.class)) {
            waitUi();
            generate("09/2026");

            onView(markPaidButtonOf("Nguyen Van An")).perform(click());
            waitUi();

            onView(withText("ĐÃ THANH TOÁN")).check(matches(isDisplayed()));
            onView(markPaidButtonOf("Nguyen Van An")).check(matches(not(isDisplayed())));
            assertEquals(Salary.STATUS_PAID,
                    db.salaryDao().getByEmployeeAndPeriod(an.id, 9, 2026).status);
        }
    }

    @Test
    public void admin_filterByMonth_showsOnlyThatMonth() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<AdminSalaryActivity> s =
                     ActivityScenario.launch(AdminSalaryActivity.class)) {
            waitUi();
            generate("09/2026");
            assertEquals(2, countTexts(s, "Bảng lương 09/2026"));

            generate("08/2026");
            assertEquals(2, countTexts(s, "Bảng lương 08/2026"));
            assertEquals(0, countTexts(s, "Bảng lương 09/2026"));
        }
    }

    @Test
    public void employee_cannotOpenAdminSalary_isRedirected() throws InterruptedException {
        loginAn();
        try (ActivityScenario<AdminSalaryActivity> s =
                     ActivityScenario.launch(AdminSalaryActivity.class)) {
            Thread.sleep(1500);
            onView(withId(R.id.btn_check_in)).check(matches(isDisplayed()));
        }
    }

    // ----- Employee -----

    @Test
    public void employee_seesOnlyOwnSalary() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<AdminSalaryActivity> s =
                     ActivityScenario.launch(AdminSalaryActivity.class)) {
            waitUi();
            generate("09/2026");
        }
        loginAn();
        try (ActivityScenario<EmployeeSalaryActivity> s =
                     ActivityScenario.launch(EmployeeSalaryActivity.class)) {
            waitUi();
            onView(withText("Bảng lương 09/2026")).check(matches(isDisplayed()));
            onView(withText(money(950000))).check(matches(isDisplayed()));
            // Khong hien ten ai khac va khong co nut thanh toan.
            onView(withText("Tran Thi Binh")).check(doesNotExist());
            onView(withId(R.id.btn_mark_salary_paid)).check(matches(
                    androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(
                            androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE)));
        }
    }

    @Test
    public void employee_withoutSalary_showsEmptyMessage() throws InterruptedException {
        loginAn();
        try (ActivityScenario<EmployeeSalaryActivity> s =
                     ActivityScenario.launch(EmployeeSalaryActivity.class)) {
            waitUi();
            onView(withText(R.string.salary_employee_empty)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void admin_cannotOpenEmployeeSalary_isRedirected() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<EmployeeSalaryActivity> s =
                     ActivityScenario.launch(EmployeeSalaryActivity.class)) {
            Thread.sleep(1500);
            onView(withId(R.id.cv_manage_salary)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void employeeHome_salaryButton_opensSalaryScreen() throws InterruptedException {
        loginAn();
        try (ActivityScenario<EmployeeHomeActivity> s =
                     ActivityScenario.launch(EmployeeHomeActivity.class)) {
            waitUi();
            onView(withId(R.id.btn_view_salary)).perform(click());
            waitUi();
            onView(withId(R.id.toolbar_employee_salary)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void adminDashboard_salaryCard_opensAdminSalary() throws InterruptedException {
        loginAdmin();
        try (ActivityScenario<com.loader.thuc_hanh_2_android.AdminDashboardActivity> s =
                     ActivityScenario.launch(
                             com.loader.thuc_hanh_2_android.AdminDashboardActivity.class)) {
            onView(withId(R.id.cv_manage_salary)).perform(click());
            waitUi();
            onView(withId(R.id.btn_generate_salary)).check(matches(isDisplayed()));
        }
    }
}
