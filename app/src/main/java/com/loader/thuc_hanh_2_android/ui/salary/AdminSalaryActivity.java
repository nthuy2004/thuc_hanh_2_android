package com.loader.thuc_hanh_2_android.ui.salary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.adapter.SalaryAdapter;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.repository.SalaryRepository;
import com.loader.thuc_hanh_2_android.databinding.ActivityAdminSalaryBinding;
import com.loader.thuc_hanh_2_android.session.AccessGuard;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Admin: tao bang luong thang, loc theo thang/nam, danh dau da thanh toan. */
public class AdminSalaryActivity extends AppCompatActivity
        implements SalaryAdapter.OnMarkPaidListener {

    private static final Pattern PERIOD = Pattern.compile("^\\s*(\\d{1,2})\\s*/\\s*(\\d{4})\\s*$");

    private ActivityAdminSalaryBinding binding;
    private SalaryRepository repository;
    private SalaryAdapter adapter;
    private View emptyState;
    private TextView emptyMessage;

    /** Ky luong dang xem; null khi o nhap chua hop le. */
    private int month;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AccessGuard.requireRole(this, Account.ROLE_ADMIN)) {
            return;
        }
        binding = ActivityAdminSalaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new SalaryRepository(this);
        adapter = new SalaryAdapter(AppDatabase.getInstance(this).employeeDao(), true, this);
        binding.rvAdminSalary.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminSalary.setAdapter(adapter);
        addEmptyState();

        if (savedInstanceState == null) {
            Calendar now = Calendar.getInstance();
            binding.edtMonthYear.setText(String.format(Locale.US, "%02d/%d",
                    now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR)));
        }
        binding.btnGenerateSalary.setOnClickListener(v -> generate());
        loadExisting();
    }

    /** Dung lai layout empty_state.xml cua P4, dat cuoi man hinh. */
    private void addEmptyState() {
        emptyState = LayoutInflater.from(this)
                .inflate(R.layout.empty_state, binding.as, false);
        emptyMessage = emptyState.findViewById(R.id.tv_empty_message);
        emptyMessage.setText(R.string.salary_admin_empty);
        emptyState.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyState.setVisibility(View.GONE);
        binding.as.addView(emptyState);
    }

    /** Doc o thang/nam; tra ve false va bao loi neu khong hop le. */
    private boolean readPeriod() {
        binding.edtMonthYear.setError(null);
        Matcher m = PERIOD.matcher(binding.edtMonthYear.getText().toString());
        if (m.matches()) {
            int mo = Integer.parseInt(m.group(1));
            int yr = Integer.parseInt(m.group(2));
            if (mo >= 1 && mo <= 12 && yr >= 2000 && yr <= 2100) {
                month = mo;
                year = yr;
                return true;
            }
        }
        binding.edtMonthYear.setError(getString(R.string.salary_error_period));
        return false;
    }

    /** Xem bang luong da co cua ky nhap (khong tao moi). */
    private void loadExisting() {
        if (!readPeriod()) {
            return;
        }
        final int mo = month;
        final int yr = year;
        AppDatabase.databaseExecutor.execute(() -> {
            List<Salary> list = repository.getSalaries(mo, yr);
            runOnUiThread(() -> show(list));
        });
    }

    private void generate() {
        if (!readPeriod()) {
            return;
        }
        final int mo = month;
        final int yr = year;
        binding.btnGenerateSalary.setEnabled(false);
        AppDatabase.databaseExecutor.execute(() -> {
            int count = repository.generateForMonth(mo, yr);
            List<Salary> list = repository.getSalaries(mo, yr);
            runOnUiThread(() -> {
                binding.btnGenerateSalary.setEnabled(true);
                show(list);
                Toast.makeText(this, getString(R.string.salary_generated, mo, yr, count),
                        Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void show(List<Salary> list) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        adapter.setSalaries(list);
        boolean empty = list == null || list.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvAdminSalary.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onMarkPaid(Salary salary) {
        final int mo = salary.month;
        final int yr = salary.year;
        AppDatabase.databaseExecutor.execute(() -> {
            repository.markPaid(salary.id);
            List<Salary> list = repository.getSalaries(mo, yr);
            runOnUiThread(() -> {
                show(list);
                Toast.makeText(this, R.string.salary_marked_paid, Toast.LENGTH_SHORT).show();
            });
        });
    }
}
