package com.loader.thuc_hanh_2_android.auth;

import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;

public class LoginResult {

    public enum Status {
        SUCCESS,
        INVALID_CREDENTIALS,
        ACCOUNT_INACTIVE
    }

    public final Status status;
    public final Account account;
    /** null neu la ADMIN hoac dang nhap that bai. */
    public final Employee employee;

    private LoginResult(Status status, Account account, Employee employee) {
        this.status = status;
        this.account = account;
        this.employee = employee;
    }

    public static LoginResult success(Account account, Employee employee) {
        return new LoginResult(Status.SUCCESS, account, employee);
    }

    public static LoginResult invalidCredentials() {
        return new LoginResult(Status.INVALID_CREDENTIALS, null, null);
    }

    public static LoginResult accountInactive() {
        return new LoginResult(Status.ACCOUNT_INACTIVE, null, null);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
