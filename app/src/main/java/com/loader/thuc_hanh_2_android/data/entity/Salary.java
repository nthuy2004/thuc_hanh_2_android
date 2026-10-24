package com.loader.thuc_hanh_2_android.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "salary",
        foreignKeys = @ForeignKey(
                entity = Employee.class,
                parentColumns = "id",
                childColumns = "employeeId"),
        indices = {@Index(value = {"employeeId", "month", "year"}, unique = true)})
public class Salary {

    public static final String STATUS_UNPAID = "UNPAID";
    public static final String STATUS_PAID = "PAID";

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long employeeId;

    public int month;

    public int year;

    public double totalHours;

    public double overtimeHours;

    public double totalSalary;

    /** STATUS_UNPAID / STATUS_PAID. */
    @NonNull
    public String status = STATUS_UNPAID;

    public Salary(long employeeId, int month, int year, double totalHours,
                  double overtimeHours, double totalSalary, @NonNull String status) {
        this.employeeId = employeeId;
        this.month = month;
        this.year = year;
        this.totalHours = totalHours;
        this.overtimeHours = overtimeHours;
        this.totalSalary = totalSalary;
        this.status = status;
    }
}
