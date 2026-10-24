package com.loader.thuc_hanh_2_android.data.model;

/**
 * Tong ket cham cong theo thang: so ngay cong, tong gio lam, tong gio OT.
 */
public class MonthlyAttendanceSummary {
    private final int totalDays;
    private final double totalHours;
    private final double totalOvertimeHours;

    public MonthlyAttendanceSummary(int totalDays, double totalHours, double totalOvertimeHours) {
        this.totalDays = totalDays;
        this.totalHours = totalHours;
        this.totalOvertimeHours = totalOvertimeHours;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getTotalOvertimeHours() {
        return totalOvertimeHours;
    }
}
