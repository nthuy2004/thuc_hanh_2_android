package com.loader.thuc_hanh_2_android.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class SalaryCalculator {

    private static final double STANDARD_HOURS = 8.0;
    private static final double MAX_SHIFT_HOURS = 16.0;
    private static final double OVERTIME_RATE = 1.5;

    private SalaryCalculator() {
    }

    public static Result calculate(List<DailyWorkLog> workLogs, double hourlyRate) {
        if (hourlyRate <= 0 || Double.isNaN(hourlyRate)
                || Double.isInfinite(hourlyRate)) {
            throw new IllegalArgumentException("hourlyRate phải lớn hơn 0.");
        }

        if (workLogs == null) {
            workLogs = Collections.emptyList();
        }

        double totalHours = 0.0;
        double overtimeHours = 0.0;
        int excludedLogs = 0;
        List<String> warnings = new ArrayList<>();

        for (DailyWorkLog log : workLogs) {
            if (log == null) {
                excludedLogs++;
                warnings.add("Có bản ghi chấm công rỗng.");
                continue;
            }

            if (!"COMPLETED".equalsIgnoreCase(log.getStatus())) {
                excludedLogs++;
                warnings.add("Bỏ qua log " + log.getWorkDate()
                        + " vì trạng thái chưa COMPLETED.");
                continue;
            }

            double hours = log.getTotalHours();

            if (Double.isNaN(hours) || Double.isInfinite(hours)
                    || hours <= 0 || hours > MAX_SHIFT_HOURS) {
                excludedLogs++;
                warnings.add("Bỏ qua log " + log.getWorkDate()
                        + " vì tổng giờ không hợp lệ.");
                continue;
            }

            hours = roundHours(hours);
            double dailyOvertime = Math.max(0.0, hours - STANDARD_HOURS);

            totalHours += hours;
            overtimeHours += dailyOvertime;
        }

        totalHours = roundHours(totalHours);
        overtimeHours = roundHours(overtimeHours);

        double regularHours = roundHours(totalHours - overtimeHours);

        BigDecimal rate = BigDecimal.valueOf(hourlyRate);
        BigDecimal regularPay = rate.multiply(BigDecimal.valueOf(regularHours));
        BigDecimal overtimePay = rate
                .multiply(BigDecimal.valueOf(overtimeHours))
                .multiply(BigDecimal.valueOf(OVERTIME_RATE));

        double totalSalary = regularPay.add(overtimePay)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return new Result(
                totalHours,
                overtimeHours,
                totalSalary,
                excludedLogs,
                warnings
        );
    }

    private static double roundHours(double hours) {
        return BigDecimal.valueOf(hours)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static final class DailyWorkLog {
        private final String workDate;
        private final double totalHours;
        private final String status;

        public DailyWorkLog(String workDate, double totalHours, String status) {
            this.workDate = workDate;
            this.totalHours = totalHours;
            this.status = status;
        }

        public String getWorkDate() {
            return workDate;
        }

        public double getTotalHours() {
            return totalHours;
        }

        public String getStatus() {
            return status;
        }
    }

    public static final class Result {
        private final double totalHours;
        private final double overtimeHours;
        private final double totalSalary;
        private final int excludedLogs;
        private final List<String> warnings;

        private Result(double totalHours, double overtimeHours,
                       double totalSalary, int excludedLogs,
                       List<String> warnings) {
            this.totalHours = totalHours;
            this.overtimeHours = overtimeHours;
            this.totalSalary = totalSalary;
            this.excludedLogs = excludedLogs;
            this.warnings = Collections.unmodifiableList(
                    new ArrayList<>(warnings)
            );
        }

        public double getTotalHours() {
            return totalHours;
        }

        public double getOvertimeHours() {
            return overtimeHours;
        }

        public double getTotalSalary() {
            return totalSalary;
        }

        public int getExcludedLogs() {
            return excludedLogs;
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }
}