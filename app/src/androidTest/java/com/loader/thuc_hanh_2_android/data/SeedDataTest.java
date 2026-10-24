package com.loader.thuc_hanh_2_android.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.utils.PasswordHasher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SeedDataTest {

    private AppDatabase db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("employee_salary.db");
        db = AppDatabase.getInstance(context);
    }

    private int count(String table) {
        try (Cursor c = db.getOpenHelper().getReadableDatabase()
                .query("SELECT COUNT(*) FROM " + table)) {
            c.moveToFirst();
            return c.getInt(0);
        }
    }

    @Test
    public void seed_createsThreeAccountsAndTwoEmployees() {
        assertEquals(3, count("account"));
        assertEquals(2, count("employee"));
        assertEquals(0, count("work_log"));
        assertEquals(0, count("salary"));
    }

    @Test
    public void seed_adminAccountIsValid() {
        Account admin = db.accountDao().getByUsername("admin");
        assertNotNull(admin);
        assertEquals(Account.ROLE_ADMIN, admin.role);
        assertTrue(PasswordHasher.matches("admin123", admin.password));
    }

    @Test
    public void seed_employeesLinkedToAccounts() {
        Account nv1 = db.accountDao().getByUsername("nv1");
        assertNotNull(nv1);
        assertEquals(Account.ROLE_EMPLOYEE, nv1.role);
        assertTrue(PasswordHasher.matches("123456", nv1.password));

        Employee e1 = db.employeeDao().getByAccountId(nv1.id);
        assertNotNull(e1);
        assertEquals("NV001", e1.code);
        assertTrue(e1.isActive);
        assertTrue(e1.hourlyRate > 0);

        Account nv2 = db.accountDao().getByUsername("nv2");
        Employee e2 = db.employeeDao().getByAccountId(nv2.id);
        assertNotNull(e2);
        assertEquals("NV002", e2.code);
    }
}
