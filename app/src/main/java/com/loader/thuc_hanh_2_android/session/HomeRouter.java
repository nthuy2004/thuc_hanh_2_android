package com.loader.thuc_hanh_2_android.session;

import android.content.Context;
import android.content.Intent;

import com.loader.thuc_hanh_2_android.AdminDashboardActivity;
import com.loader.thuc_hanh_2_android.EmployeeHomeActivity;
import com.loader.thuc_hanh_2_android.LoginActivity;
import com.loader.thuc_hanh_2_android.data.entity.Account;

/** Quyet dinh man hinh dich theo role. Noi duy nhat can sua khi P2/P4 co man hinh that. */
public final class HomeRouter {

    private HomeRouter() {
    }

    public static Intent homeFor(Context context, String role) {
        if (Account.ROLE_ADMIN.equals(role)) {
            return new Intent(context, AdminDashboardActivity.class);
        }
        if (Account.ROLE_EMPLOYEE.equals(role)) {
            return new Intent(context, EmployeeHomeActivity.class);
        }
        return loginIntent(context);
    }

    /** Ve man hinh dang nhap va xoa toan bo back stack. */
    public static Intent loginIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
