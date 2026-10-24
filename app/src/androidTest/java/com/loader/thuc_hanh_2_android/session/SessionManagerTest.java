package com.loader.thuc_hanh_2_android.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.data.entity.Account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SessionManagerTest {

    private Context context;
    private SessionManager session;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        session = new SessionManager(context);
        session.clear();
    }

    @Test
    public void emptySession_isNotLoggedIn() {
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());
        assertNull(session.getUsername());
        assertEquals(SessionManager.NO_ID, session.getAccountId());
        assertEquals(SessionManager.NO_ID, session.getEmployeeId());
    }

    @Test
    public void saveSession_thenReadBack() {
        session.saveSession(5, 2, "nv1", Account.ROLE_EMPLOYEE);
        assertTrue(session.isLoggedIn());
        assertEquals(5, session.getAccountId());
        assertEquals(2, session.getEmployeeId());
        assertEquals("nv1", session.getUsername());
        assertEquals(Account.ROLE_EMPLOYEE, session.getRole());
    }

    @Test
    public void adminSession_hasNoEmployeeId() {
        session.saveSession(1, SessionManager.NO_ID, "admin", Account.ROLE_ADMIN);
        assertTrue(session.isLoggedIn());
        assertEquals(SessionManager.NO_ID, session.getEmployeeId());
        assertEquals(Account.ROLE_ADMIN, session.getRole());
    }

    @Test
    public void sessionSurvivesNewInstance_likeRestartingApp() {
        session.saveSession(5, 2, "nv1", Account.ROLE_EMPLOYEE);
        SessionManager another = new SessionManager(context);
        assertTrue(another.isLoggedIn());
        assertEquals("nv1", another.getUsername());
    }

    @Test
    public void clear_logsOut() {
        session.saveSession(5, 2, "nv1", Account.ROLE_EMPLOYEE);
        session.clear();
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());
    }
}
