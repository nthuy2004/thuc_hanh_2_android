package com.loader.thuc_hanh_2_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.loader.thuc_hanh_2_android.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Không dùng setContentView ở đây vì MainActivity chỉ làm nhiệm vụ điều hướng

        SessionManager sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            // Đã đăng nhập, kiểm tra Role để chuyển đúng màn hình
            String role = sessionManager.getRole();
            Intent intent;
            if ("ADMIN".equals(role)) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                // EMPLOYEE
                intent = new Intent(this, com.loader.thuc_hanh_2_android.ui.attendance.EmployeeHomeActivity.class);
            }
            startActivity(intent);
        } else {
            // Chưa đăng nhập, chuyển sang Login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Đóng MainActivity để user không back lại được màn hình trắng này
        finish();
    }
}