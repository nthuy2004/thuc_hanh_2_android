package com.loader.thuc_hanh_2_android.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "account", indices = {@Index(value = "username", unique = true)})
public class Account {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String username;

    /** Mat khau da ma hoa SHA-256 (hex). */
    @NonNull
    public String password;

    /** ROLE_ADMIN hoac ROLE_EMPLOYEE. */
    @NonNull
    public String role;

    public Account(@NonNull String username, @NonNull String password, @NonNull String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
