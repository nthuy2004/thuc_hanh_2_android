package com.loader.thuc_hanh_2_android.data.repository;

import android.content.Context;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.dao.WorkLogDao;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.data.model.MonthlyAttendanceSummary;
import com.loader.thuc_hanh_2_android.utils.TimeUtils;
import com.loader.thuc_hanh_2_android.utils.ValidationResult;
import com.loader.thuc_hanh_2_android.utils.WorkLogValidator;

import java.util.List;
import java.util.Locale;

/**
 * Repository xu ly cac nghiep vu cham cong, gio lam, va lich su theo thang.
 */
public class WorkLogRepository {

    private final WorkLogDao workLogDao;

    public WorkLogRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.workLogDao = db.workLogDao();
    }

    public WorkLogRepository(WorkLogDao workLogDao) {
        this.workLogDao = workLogDao;
    }

    public WorkLog getTodayLog(long employeeId) {
        String today = TimeUtils.getCurrentDate();
        return workLogDao.getTodayLog(employeeId, today);
    }

    public ValidationResult checkIn(long employeeId) {
        String today = TimeUtils.getCurrentDate();
        WorkLog existingLog = workLogDao.getTodayLog(employeeId, today);

        ValidationResult validation = WorkLogValidator.validateCheckIn(today, existingLog != null);
        if (!validation.isValid()) {
            return validation;
        }

        String currentTime = TimeUtils.getCurrentTime();
        WorkLog newLog = new WorkLog(
                employeeId,
                today,
                currentTime,
                null,
                0.0,
                WorkLog.STATUS_WORKING
        );

        workLogDao.insert(newLog);
        return ValidationResult.success("Check-in thành công lúc " + currentTime);
    }

    public ValidationResult checkOut(long employeeId) {
        String today = TimeUtils.getCurrentDate();
        WorkLog todayLog = workLogDao.getTodayLog(employeeId, today);

        if (todayLog == null) {
            return ValidationResult.error("Bạn chưa thực hiện Check-in cho ngày hôm nay.");
        }

        String currentTime = TimeUtils.getCurrentTime();
        ValidationResult validation = WorkLogValidator.validateCheckOut(
                todayLog.checkInTime,
                currentTime,
                todayLog.status
        );

        if (!validation.isValid()) {
            return validation;
        }

        double totalHours = TimeUtils.calculateTotalHours(todayLog.checkInTime, currentTime);
        todayLog.checkOutTime = currentTime;
        todayLog.totalHours = totalHours;
        todayLog.status = WorkLog.STATUS_COMPLETED;

        workLogDao.update(todayLog);
        return ValidationResult.success(
                String.format(Locale.US, "Check-out thành công lúc %s (Tổng giờ: %.2fh)", currentTime, totalHours)
        );
    }

    public List<WorkLog> getMonthlyLogs(long employeeId, int year, int month) {
        String yearMonthPrefix = String.format(Locale.US, "%04d-%02d", year, month);
        return workLogDao.getLogsByMonth(employeeId, yearMonthPrefix);
    }

    public MonthlyAttendanceSummary getMonthlySummary(long employeeId, int year, int month) {
        String yearMonthPrefix = String.format(Locale.US, "%04d-%02d", year, month);
        List<WorkLog> completedLogs = workLogDao.getCompletedLogsByMonth(employeeId, yearMonthPrefix);

        int totalDays = completedLogs.size();
        double totalHours = 0.0;
        double totalOt = 0.0;

        for (WorkLog log : completedLogs) {
            if (WorkLogValidator.isEligibleForSalary(log.status, log.totalHours)) {
                totalHours += log.totalHours;
                totalOt += TimeUtils.calculateDailyOvertime(log.totalHours);
            }
        }

        return new MonthlyAttendanceSummary(
                totalDays,
                TimeUtils.roundToTwoDecimals(totalHours),
                TimeUtils.roundToTwoDecimals(totalOt)
        );
    }

    /**
     * Tao 3-5 ban ghi WorkLog mau trong thang hien tai cho P3 tinh luong va ca nhom demo.
     */
    public void seedSampleLogsIfEmpty(long employeeId) {
        if (workLogDao.countLogs() > 0) {
            return;
        }

        String currentMonthPrefix = TimeUtils.getCurrentDate().substring(0, 7); // yyyy-MM
        workLogDao.insert(new WorkLog(employeeId, currentMonthPrefix + "-01", "08:00:00", "16:00:00", 8.0, WorkLog.STATUS_COMPLETED));
        workLogDao.insert(new WorkLog(employeeId, currentMonthPrefix + "-02", "08:00:00", "18:00:00", 10.0, WorkLog.STATUS_COMPLETED));
        workLogDao.insert(new WorkLog(employeeId, currentMonthPrefix + "-03", "08:00:00", "17:30:00", 9.5, WorkLog.STATUS_COMPLETED));
        workLogDao.insert(new WorkLog(employeeId, currentMonthPrefix + "-04", "08:30:00", "16:30:00", 8.0, WorkLog.STATUS_COMPLETED));
    }
}
