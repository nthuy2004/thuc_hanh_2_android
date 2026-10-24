package com.loader.thuc_hanh_2_android.ui.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import com.loader.thuc_hanh_2_android.ui.salary.EmployeeSalaryActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.data.repository.WorkLogRepository;
import com.loader.thuc_hanh_2_android.databinding.ActivityEmployeeHomeBinding;
import com.loader.thuc_hanh_2_android.utils.TimeUtils;
import com.loader.thuc_hanh_2_android.utils.ValidationResult;

import java.util.Locale;

/**
 * Man hinh chinh cua Employee: Check-in, Check-out, trang thai ca lam viec hom nay.
 */
public class EmployeeHomeActivity extends AppCompatActivity {

    public static final String EXTRA_EMPLOYEE_ID = "employeeId";
    private static final String PREF_NAME = "session_pref";
    private static final String KEY_EMPLOYEE_ID = "employeeId";

    private ActivityEmployeeHomeBinding binding;
    private WorkLogRepository workLogRepository;
    private long currentEmployeeId = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workLogRepository = new WorkLogRepository(this);
        resolveEmployeeId();

        // Khoi tao du lieu mau neu chua co de co the demo ngay
        workLogRepository.seedSampleLogsIfEmpty(currentEmployeeId);

        setupToolbar();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshShiftStatus();
    }

    private void resolveEmployeeId() {
        if (getIntent().hasExtra(EXTRA_EMPLOYEE_ID)) {
            currentEmployeeId = getIntent().getLongExtra(EXTRA_EMPLOYEE_ID, 1L);
        } else {
            SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            currentEmployeeId = pref.getLong(KEY_EMPLOYEE_ID, 1L);
        }
    }

    private void setupToolbar() {
        binding.toolbarEmployeeHome.setTitle("Trang chủ nhân viên");
    }

    private void refreshShiftStatus() {
        binding.tvCurrentDate.setText("Hôm nay: " + TimeUtils.getCurrentDate());

        WorkLog todayLog = workLogRepository.getTodayLog(currentEmployeeId);

        if (todayLog == null) {
            binding.tvStatus.setText("Trạng thái: CHƯA CHECK-IN");
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
            binding.btnCheckIn.setEnabled(true);
            binding.btnCheckOut.setEnabled(false);
        } else if (WorkLog.STATUS_WORKING.equalsIgnoreCase(todayLog.status)) {
            String inTime = todayLog.checkInTime != null ? todayLog.checkInTime : "--:--:--";
            binding.tvStatus.setText("Trạng thái: ĐANG LÀM VIỆC (Vào: " + inTime + ")");
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.primaryColor));
            binding.btnCheckIn.setEnabled(false);
            binding.btnCheckOut.setEnabled(true);
        } else if (WorkLog.STATUS_COMPLETED.equalsIgnoreCase(todayLog.status)) {
            binding.tvStatus.setText(String.format(Locale.US, "Trạng thái: ĐÃ HOÀN THÀNH (%.2fh)", todayLog.totalHours));
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.secondaryColor));
            binding.btnCheckIn.setEnabled(false);
            binding.btnCheckOut.setEnabled(false);
        } else {
            binding.tvStatus.setText("Trạng thái: CHƯA HOÀN TẤT");
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
            binding.btnCheckIn.setEnabled(false);
            binding.btnCheckOut.setEnabled(true);
        }
    }

    private void setupListeners() {
        binding.btnCheckIn.setOnClickListener(v -> {
            ValidationResult result = workLogRepository.checkIn(currentEmployeeId);
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            if (result.isValid()) {
                refreshShiftStatus();
            }
        });

        binding.btnCheckOut.setOnClickListener(v -> {
            ValidationResult result = workLogRepository.checkOut(currentEmployeeId);
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
            if (result.isValid()) {
                refreshShiftStatus();
            }
        });

        binding.btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeHomeActivity.this, AttendanceHistoryActivity.class);
            intent.putExtra(EXTRA_EMPLOYEE_ID, currentEmployeeId);
            startActivity(intent);
        });
        binding.btnViewSalary.setOnClickListener(v -> {
            Intent intent = new Intent(
                    EmployeeHomeActivity.this,
                    EmployeeSalaryActivity.class
            );
            intent.putExtra(EXTRA_EMPLOYEE_ID, currentEmployeeId);
            startActivity(intent);
        });
    }
}
