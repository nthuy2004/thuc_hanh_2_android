package com.loader.thuc_hanh_2_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmployeeProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        Button btnViewAttendance = findViewById(R.id.btn_view_attendance);
        Button btnViewSalary = findViewById(R.id.btn_view_salary);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnViewAttendance.setOnClickListener(v -> {
            Toast.makeText(this, "Mở Lịch sử chấm công (P2 làm)", Toast.LENGTH_SHORT).show();
            // TODO: start Activity AttendanceHistoryActivity
        });

        btnViewSalary.setOnClickListener(v -> {
            Toast.makeText(this, "Mở Bảng lương cá nhân (P3 làm)", Toast.LENGTH_SHORT).show();
            // TODO: start Activity EmployeeSalaryActivity
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}