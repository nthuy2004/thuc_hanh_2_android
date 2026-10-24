package com.loader.thuc_hanh_2_android.ui.employee;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.session.SessionManager;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Test giao dien: them, sua, xoa mem, tim kiem nhan vien (chuc nang C-G). */
@RunWith(AndroidJUnit4.class)
public class EmployeeManageUiTest {

    private Context context;
    private AppDatabase db;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        AppDatabase.resetForTesting(context);
        db = AppDatabase.getInstance(context);
        new SessionManager(context)
                .saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
    }

    @After
    public void tearDown() {
        AppDatabase.resetForTesting(context);
        AppDatabase.getInstance(context);
    }

    private static void waitUi() throws InterruptedException {
        Thread.sleep(900);
    }

    /** Nut con (sua/xoa) nam trong the cua nhan vien co ten cho truoc. */
    private static Matcher<android.view.View> buttonInCardOf(int buttonId, String name) {
        return allOf(withId(buttonId),
                isDescendantOfA(allOf(withId(R.id.ie), hasDescendant(withText(name)))));
    }

    private void fillForm(String code, String name, String email, String phone, String rate) {
        onView(withId(R.id.edt_emp_code)).perform(replaceText(code));
        onView(withId(R.id.edt_emp_name)).perform(replaceText(name));
        onView(withId(R.id.edt_emp_email)).perform(replaceText(email));
        onView(withId(R.id.edt_emp_phone)).perform(replaceText(phone));
        onView(withId(R.id.edt_emp_rate)).perform(replaceText(rate));
        closeSoftKeyboard();
    }

    @Test
    public void add_newEmployee_appearsInList() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            onView(withId(R.id.fab_add_employee)).perform(click());
            fillForm("EMP10", "Hoang Van Moi", "moi@example.com", "0911222333", "65000");
            onView(withId(R.id.btn_save)).perform(click());
            waitUi();

            onView(withText("Hoang Van Moi")).check(matches(isDisplayed()));
            onView(withText("Mã: EMP10")).check(matches(isDisplayed()));
            Account acc = db.accountDao().getByUsername("emp10");
            assertNotNull(acc);
            assertEquals(65000d, db.employeeDao().getByAccountId(acc.id).hourlyRate, 0.001);
        }
    }

    @Test
    public void add_invalidRate_showsError_andKeepsDialogOpen() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            onView(withId(R.id.fab_add_employee)).perform(click());
            fillForm("EMP11", "Luong Am", "", "", "-5");
            onView(withId(R.id.btn_save)).perform(click());
            waitUi();

            onView(withId(R.id.edt_emp_rate)).check(matches(
                    hasErrorText("Lương theo giờ phải là số lớn hơn 0")));
            onView(withId(R.id.btn_save)).check(matches(isDisplayed()));
            assertNull(db.accountDao().getByUsername("emp11"));
        }
    }

    @Test
    public void add_emptyForm_showsErrorsOnRequiredFields() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            onView(withId(R.id.fab_add_employee)).perform(click());
            onView(withId(R.id.btn_save)).perform(click());
            waitUi();

            onView(withId(R.id.edt_emp_code)).check(matches(
                    hasErrorText("Vui lòng nhập mã nhân viên")));
            onView(withId(R.id.edt_emp_name)).check(matches(
                    hasErrorText("Vui lòng nhập họ và tên")));
        }
    }

    @Test
    public void add_duplicateCode_showsError() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            onView(withId(R.id.fab_add_employee)).perform(click());
            fillForm("NV001", "Trung Ma", "", "", "10000");
            onView(withId(R.id.btn_save)).perform(click());
            waitUi();

            onView(withId(R.id.edt_emp_code)).check(matches(
                    hasErrorText("Mã nhân viên đã tồn tại")));
        }
    }

    @Test
    public void edit_changesRate_andCodeIsLocked() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            waitUi();
            onView(buttonInCardOf(R.id.btn_edit_employee, "Nguyen Van An")).perform(click());

            onView(withId(R.id.edt_emp_code)).check(matches(not(isEnabled())));
            onView(withId(R.id.edt_emp_code)).check(matches(withText("NV001")));
            onView(withId(R.id.edt_emp_rate)).perform(replaceText("77000"));
            closeSoftKeyboard();
            onView(withId(R.id.btn_save)).perform(click());
            waitUi();

            Employee e = db.employeeDao()
                    .getByAccountId(db.accountDao().getByUsername("nv1").id);
            assertEquals(77000d, e.hourlyRate, 0.001);
        }
    }

    @Test
    public void delete_softDeletesAfterConfirm_andKeepsRow() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            waitUi();
            onView(buttonInCardOf(R.id.btn_delete_employee, "Tran Thi Binh")).perform(click());
            onView(withId(android.R.id.button1)).perform(click());
            waitUi();

            onView(withText("Tran Thi Binh")).check(doesNotExist());
            onView(withText("Nguyen Van An")).check(matches(isDisplayed()));
            Employee e = db.employeeDao()
                    .getByAccountId(db.accountDao().getByUsername("nv2").id);
            assertNotNull(e);
            assertFalse(e.isActive);
        }
    }

    @Test
    public void delete_cancel_keepsEmployee() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            waitUi();
            onView(buttonInCardOf(R.id.btn_delete_employee, "Tran Thi Binh")).perform(click());
            onView(withId(android.R.id.button2)).perform(click());
            waitUi();

            onView(withText("Tran Thi Binh")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void search_filtersByNameOrCode_andShowsEmptyResult() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            waitUi();

            onView(withId(R.id.edt_search)).perform(replaceText("Binh"));
            waitUi();
            onView(withText("Tran Thi Binh")).check(matches(isDisplayed()));
            onView(withText("Nguyen Van An")).check(doesNotExist());

            onView(withId(R.id.edt_search)).perform(replaceText("nv001"));
            waitUi();
            onView(withText("Nguyen Van An")).check(matches(isDisplayed()));
            onView(withText("Tran Thi Binh")).check(doesNotExist());

            onView(withId(R.id.edt_search)).perform(replaceText("zzz%_"));
            waitUi();
            onView(withText(R.string.employee_list_no_result)).check(matches(isDisplayed()));

            onView(withId(R.id.edt_search)).perform(replaceText(""));
            waitUi();
            onView(withText("Nguyen Van An")).check(matches(isDisplayed()));
            onView(withText("Tran Thi Binh")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void search_percentSign_isTreatedAsLiteral() throws InterruptedException {
        try (ActivityScenario<EmployeeListActivity> s =
                     ActivityScenario.launch(EmployeeListActivity.class)) {
            waitUi();
            onView(withId(R.id.edt_search)).perform(replaceText("%"));
            waitUi();
            onView(withText(R.string.employee_list_no_result)).check(matches(isDisplayed()));
        }
    }
}
