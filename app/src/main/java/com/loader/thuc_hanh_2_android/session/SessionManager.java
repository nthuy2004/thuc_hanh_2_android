package com.loader.thuc_hanh_2_android.session;

import android.content.Context;
import android.content.SharedPreferences;

/** Luu phien dang nhap bang SharedPreferences. */
public class SessionManager {

    public static final long NO_ID = -1;

    private static final String PREF_NAME = "session";
    private static final String KEY_ACCOUNT_ID = "accountId";
    private static final String KEY_EMPLOYEE_ID = "employeeId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** employeeId dung NO_ID neu la ADMIN (admin khong co ban ghi Employee). */
    public void saveSession(long accountId, long employeeId, String username, String role) {
        prefs.edit()
                .putLong(KEY_ACCOUNT_ID, accountId)
                .putLong(KEY_EMPLOYEE_ID, employeeId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getLong(KEY_ACCOUNT_ID, NO_ID) != NO_ID && getRole() != null;
    }

    public long getAccountId() {
        return prefs.getLong(KEY_ACCOUNT_ID, NO_ID);
    }

    public long getEmployeeId() {
        return prefs.getLong(KEY_EMPLOYEE_ID, NO_ID);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
