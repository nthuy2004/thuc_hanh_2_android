package com.loader.thuc_hanh_2_android.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "work_log",
        foreignKeys = @ForeignKey(
                entity = Employee.class,
                parentColumns = "id",
                childColumns = "employeeId"),
        indices = {@Index(value = {"employeeId", "workDate"}, unique = true)})
public class WorkLog {

    public static final String STATUS_WORKING = "WORKING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_INCOMPLETE = "INCOMPLETE";

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long employeeId;

    /** Dinh dang yyyy-MM-dd (theo TimeUtils). */
    @NonNull
    public String workDate;

    /** Dinh dang HH:mm:ss (theo TimeUtils). */
    public String checkInTime;

    /** Dinh dang HH:mm:ss; null khi chua check-out. */
    public String checkOutTime;

    public double totalHours;

    /** STATUS_WORKING / STATUS_COMPLETED / STATUS_INCOMPLETE. */
    @NonNull
    public String status = STATUS_WORKING;

    public WorkLog(long employeeId, @NonNull String workDate, String checkInTime,
                   String checkOutTime, double totalHours, @NonNull String status) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.totalHours = totalHours;
        this.status = status;
    }
}
