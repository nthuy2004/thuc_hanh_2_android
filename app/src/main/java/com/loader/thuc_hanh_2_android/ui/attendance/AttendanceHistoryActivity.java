package com.loader.thuc_hanh_2_android.ui.attendance;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loader.thuc_hanh_2_android.EmployeeHomeActivity;
import com.loader.thuc_hanh_2_android.adapter.WorkLogAdapter;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.data.model.MonthlyAttendanceSummary;
import com.loader.thuc_hanh_2_android.data.repository.WorkLogRepository;
import com.loader.thuc_hanh_2_android.databinding.ActivityAttendanceHistoryBinding;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Man hinh xem lich su cham cong va tong hop gio cong, gio OT theo thang.
 */
public class AttendanceHistoryActivity extends AppCompatActivity {

    private ActivityAttendanceHistoryBinding binding;
    private WorkLogRepository workLogRepository;
    private WorkLogAdapter adapter;
    private long currentEmployeeId = 1L;

    private int selectedYear;
    private int selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workLogRepository = new WorkLogRepository(this);
        currentEmployeeId = getIntent().getLongExtra(EmployeeHomeActivity.EXTRA_EMPLOYEE_ID, 1L);

        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH) + 1;
        selectedYear = cal.get(Calendar.YEAR);

        setupToolbar();
        setupRecyclerView();
        setupMonthPicker();

        loadAttendanceData(selectedYear, selectedMonth);
    }

    private void setupToolbar() {
        binding.toolbarAttendanceHistory.setTitle("Lịch sử chấm công");
        binding.toolbarAttendanceHistory.setNavigationIcon(android.R.drawable.ic_menu_revert);
        binding.toolbarAttendanceHistory.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new WorkLogAdapter();
        binding.rvAttendanceHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAttendanceHistory.setAdapter(adapter);
    }

    private void setupMonthPicker() {
        binding.tvSummaryMonth.setOnClickListener(v -> showMonthPicker());
    }

    private void showMonthPicker() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = String.format(Locale.US, "Tháng %02d/%d", i + 1, selectedYear);
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn tháng để xem lịch sử")
                .setItems(months, (dialog, which) -> {
                    selectedMonth = which + 1;
                    loadAttendanceData(selectedYear, selectedMonth);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadAttendanceData(int year, int month) {
        binding.tvSummaryMonth.setText(String.format(Locale.US, "Tổng hợp tháng %02d/%d  ▾", month, year));

        // 1. Cap nhat the tong hop
        MonthlyAttendanceSummary summary = workLogRepository.getMonthlySummary(currentEmployeeId, year, month);
        binding.tvSummaryDays.setText("Số ngày công: " + summary.getTotalDays());
        binding.tvSummaryHours.setText(String.format(Locale.US, "Tổng giờ làm: %.2fh", summary.getTotalHours()));
        binding.tvSummaryOt.setText(String.format(Locale.US, "Tổng giờ OT: %.2fh", summary.getTotalOvertimeHours()));

        // 2. Cap nhat danh sach WorkLog
        List<WorkLog> logs = workLogRepository.getMonthlyLogs(currentEmployeeId, year, month);
        adapter.setWorkLogs(logs);

        if (logs.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu chấm công cho tháng " + month + "/" + year, Toast.LENGTH_SHORT).show();
        }
    }
}
