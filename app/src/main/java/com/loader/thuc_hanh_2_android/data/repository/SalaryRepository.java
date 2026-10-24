package com.loader.thuc_hanh_2_android.data.repository;

import android.content.Context;

import androidx.room.RoomDatabase;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.dao.EmployeeDao;
import com.loader.thuc_hanh_2_android.data.dao.SalaryDao;
import com.loader.thuc_hanh_2_android.data.dao.WorkLogDao;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.salary.SalaryCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalaryRepository {

    private final AppDatabase db;
    private final SalaryDao salaryDao;
    private final EmployeeDao employeeDao;
    private final WorkLogDao workLogDao;

    public SalaryRepository(Context context) {
        db = AppDatabase.getInstance(context);
        salaryDao = db.salaryDao();
        employeeDao = db.employeeDao();
        workLogDao = db.workLogDao();
    }

    public List<Salary> getSalaries(int month, int year) {
        validatePeriod(month, year);
        return salaryDao.getByPeriod(month, year);
    }

    public List<Salary> getEmployeeSalaries(long employeeId) {
        return salaryDao.getByEmployee(employeeId);
    }

    public int generateForMonth(int month, int year) {
        validatePeriod(month, year);

        final int[] generated = {0};

        db.runInTransaction(() -> {
            List<Employee> employees = employeeDao.getAllActive();

            for (Employee employee : employees) {
                if (employee.hourlyRate <= 0
                        || Double.isNaN(employee.hourlyRate)
                        || Double.isInfinite(employee.hourlyRate)) {
                    continue;
                }

                String prefix = String.format(
                        Locale.US, "%04d-%02d", year, month
                );

                List<WorkLog> logs =
                        workLogDao.getCompletedLogsByMonth(employee.id, prefix);

                List<SalaryCalculator.DailyWorkLog> dailyLogs =
                        new ArrayList<>();

                for (WorkLog log : logs) {
                    dailyLogs.add(new SalaryCalculator.DailyWorkLog(
                            log.workDate,
                            log.totalHours,
                            log.status
                    ));
                }

                SalaryCalculator.Result result =
                        SalaryCalculator.calculate(dailyLogs, employee.hourlyRate);

                Salary existing = salaryDao.getByEmployeeAndPeriod(
                        employee.id, month, year
                );

                if (existing == null) {
                    Salary salary = new Salary(
                            employee.id,
                            month,
                            year,
                            result.getTotalHours(),
                            result.getOvertimeHours(),
                            result.getTotalSalary(),
                            Salary.STATUS_UNPAID
                    );
                    salaryDao.insert(salary);
                } else {
                    existing.totalHours = result.getTotalHours();
                    existing.overtimeHours = result.getOvertimeHours();
                    existing.totalSalary = result.getTotalSalary();

                    // Giữ nguyên trạng thái PAID nếu đã thanh toán.
                    salaryDao.update(existing);
                }

                generated[0]++;
            }
        });

        return generated[0];
    }

    public boolean markPaid(long salaryId) {
        return salaryDao.updateStatus(
                salaryId, Salary.STATUS_PAID
        ) > 0;
    }

    private void validatePeriod(int month, int year) {
        if (month < 1 || month > 12 || year < 2000) {
            throw new IllegalArgumentException("Tháng hoặc năm không hợp lệ.");
        }
    }
}