package com.loader.thuc_hanh_2_android.ui.salary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.adapter.SalaryAdapter;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.repository.SalaryRepository;
import com.loader.thuc_hanh_2_android.databinding.ActivityEmployeeSalaryBinding;
import com.loader.thuc_hanh_2_android.session.AccessGuard;
import com.loader.thuc_hanh_2_android.session.SessionManager;

import java.util.List;

/** Employee: chi xem bang luong cua chinh minh (lay employeeId tu phien dang nhap). */
public class EmployeeSalaryActivity extends AppCompatActivity {

    private ActivityEmployeeSalaryBinding binding;
    private SalaryAdapter adapter;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AccessGuard.requireRole(this, Account.ROLE_EMPLOYEE)) {
            return;
        }
        binding = ActivityEmployeeSalaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new SalaryAdapter(AppDatabase.getInstance(this).employeeDao(), false, null);
        binding.rvEmployeeSalary.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEmployeeSalary.setAdapter(adapter);
        addEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding == null) {
            return;
        }
        long employeeId = new SessionManager(this).getEmployeeId();
        SalaryRepository repository = new SalaryRepository(this);
        AppDatabase.databaseExecutor.execute(() -> {
            List<Salary> list = employeeId == SessionManager.NO_ID
                    ? null : repository.getEmployeeSalaries(employeeId);
            runOnUiThread(() -> show(list));
        });
    }

    private void addEmptyState() {
        emptyState = LayoutInflater.from(this)
                .inflate(R.layout.empty_state, binding.es, false);
        ((TextView) emptyState.findViewById(R.id.tv_empty_message))
                .setText(R.string.salary_employee_empty);
        emptyState.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyState.setVisibility(View.GONE);
        binding.es.addView(emptyState);
    }

    private void show(List<Salary> list) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        adapter.setSalaries(list);
        boolean empty = list == null || list.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvEmployeeSalary.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
