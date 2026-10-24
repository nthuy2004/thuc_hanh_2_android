package com.loader.thuc_hanh_2_android.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "employee",
        foreignKeys = @ForeignKey(
                entity = Account.class,
                parentColumns = "id",
                childColumns = "accountId"),
        indices = {
                @Index(value = "code", unique = true),
                @Index(value = "accountId", unique = true)
        })
public class Employee {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String code;

    @NonNull
    public String fullname;

    public String email;

    public String phone;

    /** Luong theo gio (VND), phai > 0. */
    public double hourlyRate;

    public long accountId;

    /** false = da xoa mem, van giu lich su cong va luong. */
    public boolean isActive = true;

    public Employee(@NonNull String code, @NonNull String fullname, String email, String phone,
                    double hourlyRate, long accountId) {
        this.code = code;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.hourlyRate = hourlyRate;
        this.accountId = accountId;
    }
}
