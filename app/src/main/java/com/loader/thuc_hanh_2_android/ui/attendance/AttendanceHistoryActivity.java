package com.loader.thuc_hanh_2_android.ui.attendance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.loader.thuc_hanh_2_android.adapter.WorkLogAdapter;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.data.model.MonthlyAttendanceSummary;
import com.loader.thuc_hanh_2_android.data.repository.WorkLogRepository;
import com.loader.thuc_hanh_2_android.databinding.ActivityAttendanceHistoryBinding;
import com.loader.thuc_hanh_2_android.utils.TimeUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workLogRepository = new WorkLogRepository(this);
        currentEmployeeId = getIntent().getLongExtra(EmployeeHomeActivity.EXTRA_EMPLOYEE_ID, 1L);

        setupToolbar();
        setupRecyclerView();
        loadAttendanceData();
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

    private void loadAttendanceData() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        binding.tvSummaryMonth.setText(String.format(Locale.US, "Tổng hợp tháng %02d/%d", currentMonth, currentYear));

        // 1. Cap nhat the tong hop
        MonthlyAttendanceSummary summary = workLogRepository.getMonthlySummary(currentEmployeeId, currentYear, currentMonth);
        binding.tvSummaryDays.setText("Số ngày công: " + summary.getTotalDays());
        binding.tvSummaryHours.setText(String.format(Locale.US, "Tổng giờ làm: %.2fh", summary.getTotalHours()));
        binding.tvSummaryOt.setText(String.format(Locale.US, "Tổng giờ OT: %.2fh", summary.getTotalOvertimeHours()));

        // 2. Cap nhat danh sach WorkLog
        List<WorkLog> logs = workLogRepository.getMonthlyLogs(currentEmployeeId, currentYear, currentMonth);
        adapter.setWorkLogs(logs);
    }
}
