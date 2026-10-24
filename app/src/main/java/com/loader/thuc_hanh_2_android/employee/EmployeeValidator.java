package com.loader.thuc_hanh_2_android.employee;

import java.util.regex.Pattern;

/** Kiem tra dinh dang form nhan vien (khong dung DB). Kiem tra trung nam o EmployeeRepository. */
public final class EmployeeValidator {

    private static final Pattern CODE = Pattern.compile("^[A-Za-z0-9_-]{2,20}$");
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?\\d{9,12}$");

    public static final double MAX_HOURLY_RATE = 1_000_000_000d;

    private EmployeeValidator() {
    }

    /** Tra ve null neu khong hop le; nguoc lai tra ve muc luong theo gio. */
    public static Double parseHourlyRate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            double v = Double.parseDouble(text.trim());
            if (Double.isNaN(v) || Double.isInfinite(v) || v <= 0 || v > MAX_HOURLY_RATE) {
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static FormErrors validate(EmployeeForm f) {
        FormErrors e = new FormErrors();

        if (f.code.isEmpty()) {
            e.code = "Vui lòng nhập mã nhân viên";
        } else if (!CODE.matcher(f.code).matches()) {
            e.code = "Mã gồm 2-20 ký tự chữ, số, gạch ngang hoặc gạch dưới";
        }

        if (f.fullname.isEmpty()) {
            e.fullname = "Vui lòng nhập họ và tên";
        } else if (f.fullname.length() < 2 || f.fullname.length() > 100) {
            e.fullname = "Họ tên phải từ 2 đến 100 ký tự";
        }

        if (!f.email.isEmpty() && !EMAIL.matcher(f.email).matches()) {
            e.email = "Email không đúng định dạng";
        }

        if (!f.phone.isEmpty() && !PHONE.matcher(f.phone).matches()) {
            e.phone = "Số điện thoại gồm 9-12 chữ số";
        }

        if (parseHourlyRate(f.hourlyRateText) == null) {
            e.hourlyRate = "Lương theo giờ phải là số lớn hơn 0";
        }
        return e;
    }
}
