package com.loader.thuc_hanh_2_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.databinding.ActivityAdminDashboardBinding;
import com.loader.thuc_hanh_2_android.session.AccessGuard;
import com.loader.thuc_hanh_2_android.ui.employee.EmployeeListActivity;
import com.loader.thuc_hanh_2_android.ui.salary.AdminSalaryActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AccessGuard.requireRole(this, Account.ROLE_ADMIN)) {
            return;
        }
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cvManageEmployees.setOnClickListener(v ->
                startActivity(new Intent(this, EmployeeListActivity.class)));

        binding.cvManageSalary.setOnClickListener(v ->
                startActivity(new Intent(this, AdminSalaryActivity.class)));

        binding.btnLogout.setOnClickListener(v -> AccessGuard.logout(this));
    }
}
