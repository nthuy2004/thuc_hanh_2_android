package com.loader.thuc_hanh_2_android.auth;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.utils.PasswordHasher;

/** Xu ly nghiep vu dang nhap. Truy van DB dong bo: phai goi tren thread nen. */
public class AuthService {

    private final AppDatabase db;

    public AuthService(AppDatabase db) {
        this.db = db;
    }

    public LoginResult login(String username, String password) {
        if (username == null || password == null) {
            return LoginResult.invalidCredentials();
        }
        Account account = db.accountDao().getByUsername(username.trim());
        if (account == null || !PasswordHasher.matches(password, account.password)) {
            return LoginResult.invalidCredentials();
        }
        if (Account.ROLE_ADMIN.equals(account.role)) {
            return LoginResult.success(account, null);
        }
        Employee employee = db.employeeDao().getByAccountId(account.id);
        if (employee == null || !employee.isActive) {
            return LoginResult.accountInactive();
        }
        return LoginResult.success(account, employee);
    }
}
