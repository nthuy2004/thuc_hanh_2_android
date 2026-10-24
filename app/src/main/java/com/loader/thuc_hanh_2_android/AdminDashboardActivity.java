package com.loader.thuc_hanh_2_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Ánh xạ các View từ XML sang Java bằng ID
        CardView cvManageEmployees = findViewById(R.id.cv_manage_employees);
        CardView cvManageSalary = findViewById(R.id.cv_manage_salary);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Bắt sự kiện click
        cvManageEmployees.setOnClickListener(v -> {
            Toast.makeText(this, "Mở màn hình Quản lý nhân viên (P1 làm)", Toast.LENGTH_SHORT).show();
            // TODO: start Activity EmployeeListActivity
        });

        cvManageSalary.setOnClickListener(v -> {
            Toast.makeText(this, "Mở màn hình Quản lý lương (P3 làm)", Toast.LENGTH_SHORT).show();
            // TODO: start Activity AdminSalaryActivity
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình này, quay về Login
        });
    }
}