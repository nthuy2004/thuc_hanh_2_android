package com.loader.thuc_hanh_2_android.employee;

/** Loi theo tung o cua form nhan vien; truong nao null la hop le. */
public class FormErrors {

    public String code;
    public String fullname;
    public String email;
    public String phone;
    public String hourlyRate;
    /** Loi chung khong gan voi o nao (vd: nhan vien khong ton tai). */
    public String general;

    public boolean hasErrors() {
        return code != null || fullname != null || email != null || phone != null
                || hourlyRate != null || general != null;
    }
}
