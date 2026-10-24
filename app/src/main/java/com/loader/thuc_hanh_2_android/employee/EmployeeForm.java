package com.loader.thuc_hanh_2_android.employee;

/** Du lieu nhap tu form them/sua nhan vien (chua kiem tra). */
public class EmployeeForm {

    public final String code;
    public final String fullname;
    public final String email;
    public final String phone;
    public final String hourlyRateText;

    public EmployeeForm(String code, String fullname, String email, String phone,
                        String hourlyRateText) {
        this.code = trim(code);
        this.fullname = trim(fullname);
        this.email = trim(email);
        this.phone = trim(phone);
        this.hourlyRateText = trim(hourlyRateText);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
