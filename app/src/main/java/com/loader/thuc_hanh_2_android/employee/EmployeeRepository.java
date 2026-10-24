package com.loader.thuc_hanh_2_android.employee;

import android.database.sqlite.SQLiteConstraintException;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.utils.PasswordHasher;

import java.util.Locale;

/**
 * Nghiep vu them / sua / xoa mem nhan vien. Cac ham truy van DB dong bo:
 * phai goi tren thread nen (AppDatabase.databaseExecutor).
 */
public class EmployeeRepository {

    /** Mat khau mac dinh cua tai khoan nhan vien moi. */
    public static final String DEFAULT_PASSWORD = "123456";

    private final AppDatabase db;

    public EmployeeRepository(AppDatabase db) {
        this.db = db;
    }

    /** Ten dang nhap cua nhan vien moi = ma nhan vien viet thuong. */
    public static String usernameFor(String code) {
        return code.toLowerCase(Locale.ROOT);
    }

    public FormErrors add(EmployeeForm form) {
        FormErrors errors = EmployeeValidator.validate(form);
        String code = form.code.toUpperCase(Locale.ROOT);
        String username = usernameFor(code);

        if (errors.code == null) {
            if (db.employeeDao().countByCode(code, 0) > 0) {
                errors.code = "Mã nhân viên đã tồn tại";
            } else if (db.accountDao().getByUsername(username) != null) {
                errors.code = "Mã trùng với tên đăng nhập đã có, hãy chọn mã khác";
            }
        }
        if (errors.email == null && !form.email.isEmpty()
                && db.employeeDao().countByEmail(form.email, 0) > 0) {
            errors.email = "Email đã được dùng cho nhân viên khác";
        }
        if (errors.hasErrors()) {
            return errors;
        }

        double rate = EmployeeValidator.parseHourlyRate(form.hourlyRateText);
        try {
            db.runInTransaction(() -> {
                long accountId = db.accountDao().insert(new Account(
                        username, PasswordHasher.sha256(DEFAULT_PASSWORD), Account.ROLE_EMPLOYEE));
                db.employeeDao().insert(new Employee(
                        code, form.fullname, emptyToNull(form.email), emptyToNull(form.phone),
                        rate, accountId));
            });
        } catch (SQLiteConstraintException e) {
            errors.code = "Mã nhân viên hoặc tên đăng nhập đã tồn tại";
        }
        return errors;
    }

    public FormErrors update(long employeeId, EmployeeForm form) {
        FormErrors errors = EmployeeValidator.validate(form);
        // Ma nhan vien khong duoc doi khi sua, bo qua loi cua o nay.
        errors.code = null;

        Employee employee = db.employeeDao().getById(employeeId);
        if (employee == null || !employee.isActive) {
            errors.general = "Nhân viên không còn tồn tại";
            return errors;
        }
        if (errors.email == null && !form.email.isEmpty()
                && db.employeeDao().countByEmail(form.email, employeeId) > 0) {
            errors.email = "Email đã được dùng cho nhân viên khác";
        }
        if (errors.hasErrors()) {
            return errors;
        }

        employee.fullname = form.fullname;
        employee.email = emptyToNull(form.email);
        employee.phone = emptyToNull(form.phone);
        employee.hourlyRate = EmployeeValidator.parseHourlyRate(form.hourlyRateText);
        db.employeeDao().update(employee);
        return errors;
    }

    /** Xoa mem: giu nguyen ban ghi de bao toan lich su cong va luong. */
    public void softDelete(long employeeId) {
        db.employeeDao().softDelete(employeeId);
    }

    private static String emptyToNull(String s) {
        return s == null || s.isEmpty() ? null : s;
    }
}
