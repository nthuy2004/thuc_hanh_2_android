package com.loader.thuc_hanh_2_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loader.thuc_hanh_2_android.session.HomeRouter;
import com.loader.thuc_hanh_2_android.session.SessionManager;
import com.loader.thuc_hanh_2_android.ui.attendance.AttendanceHistoryActivity;

public class EmployeeProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        SessionManager session = new SessionManager(this);

        Button btnViewAttendance = findViewById(R.id.btn_view_attendance);
        Button btnViewSalary = findViewById(R.id.btn_view_salary);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnViewAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendanceHistoryActivity.class);
            intent.putExtra(EmployeeHomeActivity.EXTRA_EMPLOYEE_ID, session.getEmployeeId());
            startActivity(intent);
        });

        btnViewSalary.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng xem bảng lương đang được hoàn thiện", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            session.clear();
            startActivity(HomeRouter.loginIntent(this));
            finish();
        });
    }
}
