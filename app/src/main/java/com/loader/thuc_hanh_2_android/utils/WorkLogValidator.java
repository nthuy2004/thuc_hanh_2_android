package com.loader.thuc_hanh_2_android.utils;

/**
 * Validates check-in/check-out rules and attendance integrity.
 */
public class WorkLogValidator {

    public static final double MAX_SHIFT_HOURS = 16.0;

    public static final String STATUS_WORKING = "WORKING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_INCOMPLETE = "INCOMPLETE";

    /**
     * Validate Check-in action.
     * Rule: Cannot check-in twice on the same day.
     * Rule: Cannot check-in for a future date.
     */
    public static ValidationResult validateCheckIn(String workDate, boolean hasExistingRecordToday) {
        if (workDate == null || workDate.trim().isEmpty()) {
            return ValidationResult.error("Ngày làm việc không hợp lệ.");
        }

        if (TimeUtils.isFutureDate(workDate)) {
            return ValidationResult.error("Không thể ghi nhận chấm công cho ngày trong tương lai.");
        }

        if (hasExistingRecordToday) {
            return ValidationResult.error("Nhân viên đã check-in cho ngày hôm nay rồi! Mỗi ngày chỉ được check-in 1 lần.");
        }

        return ValidationResult.success("Check-in hợp lệ.");
    }

    /**
     * Validate Check-out action.
     * Rule: Cannot check-out if haven't checked-in.
     * Rule: Cannot check-out if shift is already completed.
     * Rule: Check-out time must be strictly after check-in time.
     * Rule: Shift duration cannot exceed 16 hours.
     */
    public static ValidationResult validateCheckOut(String checkInTime, String checkOutTime, String currentStatus) {
        if (checkInTime == null || checkInTime.trim().isEmpty()) {
            return ValidationResult.error("Không thể check-out khi chưa thực hiện check-in.");
        }

        if (STATUS_COMPLETED.equalsIgnoreCase(currentStatus)) {
            return ValidationResult.error("Ca làm việc hôm nay đã được check-out hoàn tất trước đó.");
        }

        if (checkOutTime == null || checkOutTime.trim().isEmpty()) {
            return ValidationResult.error("Giờ check-out không được để trống.");
        }

        if (!TimeUtils.isCheckOutAfterCheckIn(checkInTime, checkOutTime)) {
            return ValidationResult.error("Giờ check-out phải sau giờ check-in.");
        }

        double totalHours = TimeUtils.calculateTotalHours(checkInTime, checkOutTime);
        if (totalHours < 0) {
            return ValidationResult.error("Không thể tính toán số giờ làm việc từ mốc thời gian cung cấp.");
        }

        if (totalHours > MAX_SHIFT_HOURS) {
            return ValidationResult.error("Ca làm việc không được vượt quá " + (int) MAX_SHIFT_HOURS + " giờ (tính được: " + totalHours + "h).");
        }

        return ValidationResult.success("Check-out hợp lệ.");
    }

    /**
     * Validate if a WorkLog record is valid and completed to be used for salary calculation.
     * Rule: Only COMPLETED records are eligible for salary calculation.
     */
    public static boolean isEligibleForSalary(String status, double totalHours) {
        if (!STATUS_COMPLETED.equalsIgnoreCase(status)) {
            return false;
        }
        return totalHours > 0 && totalHours <= MAX_SHIFT_HOURS;
    }
}
