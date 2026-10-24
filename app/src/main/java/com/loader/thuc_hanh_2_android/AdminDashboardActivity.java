package com.loader.thuc_hanh_2_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.loader.thuc_hanh_2_android.session.HomeRouter;
import com.loader.thuc_hanh_2_android.session.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        setContentView(R.layout.activity_admin_dashboard);

        SessionManager session = new SessionManager(this);


        CardView cvManageEmployees = findViewById(R.id.cv_manage_employees);
        CardView cvManageSalary = findViewById(R.id.cv_manage_salary);
        Button btnLogout = findViewById(R.id.btn_logout);


        cvManageEmployees.setOnClickListener(v -> {
            Toast.makeText(this, "Mở màn hình Quản lý nhân viên (P1 làm)", Toast.LENGTH_SHORT).show();
            // TODO: start Activity EmployeeListActivity
        });

        cvManageSalary.setOnClickListener(v -> {
            Toast.makeText(this, "Mở màn hình Quản lý lương (P3 làm)", Toast.LENGTH_SHORT).show();
            // TODO: start Activity AdminSalaryActivity
        });

        btnLogout.setOnClickListener(v -> {
            session.clear();
            startActivity(HomeRouter.loginIntent(this));
            finish();
        });
    }
}