package com.loader.thuc_hanh_2_android.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AuthServiceTest {

    private AppDatabase db;
    private AuthService auth;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = AppDatabase.getInstance(context);
        auth = new AuthService(db);
    }

    @Test
    public void admin_loginSuccess() {
        LoginResult r = auth.login("admin", "admin123");
        assertTrue(r.isSuccess());
        assertEquals(Account.ROLE_ADMIN, r.account.role);
        assertNull(r.employee);
    }

    @Test
    public void employee_loginSuccess_returnsEmployee() {
        LoginResult r = auth.login("nv1", "123456");
        assertTrue(r.isSuccess());
        assertEquals(Account.ROLE_EMPLOYEE, r.account.role);
        assertNotNull(r.employee);
        assertEquals("NV001", r.employee.code);
    }

    @Test
    public void usernameIsTrimmed() {
        assertTrue(auth.login("  admin  ", "admin123").isSuccess());
    }

    @Test
    public void wrongPassword_isInvalid() {
        assertEquals(LoginResult.Status.INVALID_CREDENTIALS,
                auth.login("admin", "sai").status);
    }

    @Test
    public void unknownUser_isInvalid() {
        assertEquals(LoginResult.Status.INVALID_CREDENTIALS,
                auth.login("khongco", "123456").status);
    }

    @Test
    public void nullInput_isInvalid() {
        assertEquals(LoginResult.Status.INVALID_CREDENTIALS, auth.login(null, null).status);
    }

    @Test
    public void softDeletedEmployee_cannotLogin() {
        Account acc = db.accountDao().getByUsername("nv2");
        Employee e = db.employeeDao().getByAccountId(acc.id);
        e.isActive = false;
        db.employeeDao().update(e);
        try {
            assertEquals(LoginResult.Status.ACCOUNT_INACTIVE,
                    auth.login("nv2", "123456").status);
        } finally {
            e.isActive = true;
            db.employeeDao().update(e);
        }
        assertTrue(auth.login("nv2", "123456").isSuccess());
    }
}
