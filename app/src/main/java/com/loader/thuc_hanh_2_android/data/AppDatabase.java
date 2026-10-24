package com.loader.thuc_hanh_2_android.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.loader.thuc_hanh_2_android.data.dao.AccountDao;
import com.loader.thuc_hanh_2_android.data.dao.EmployeeDao;
import com.loader.thuc_hanh_2_android.data.dao.SalaryDao;
import com.loader.thuc_hanh_2_android.data.dao.WorkLogDao;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;

import com.loader.thuc_hanh_2_android.utils.PasswordHasher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Account.class, Employee.class, WorkLog.class, Salary.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "employee_salary.db";

    /** Dung chung cho moi thao tac ghi/doc DB ngoai main thread. */
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    private static volatile AppDatabase instance;

    /** Chay dung 1 lan khi DB duoc tao lan dau: nap tai khoan admin va 2 nhan vien mau. */
    private static final RoomDatabase.Callback SEED_CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            long adminId = insertAccount(db, "admin", "admin123", Account.ROLE_ADMIN);
            long acc1 = insertAccount(db, "nv1", "123456", Account.ROLE_EMPLOYEE);
            long acc2 = insertAccount(db, "nv2", "123456", Account.ROLE_EMPLOYEE);
            insertEmployee(db, "NV001", "Nguyen Van An", "an.nguyen@example.com",
                    "0901000001", 50000, acc1);
            insertEmployee(db, "NV002", "Tran Thi Binh", "binh.tran@example.com",
                    "0901000002", 60000, acc2);
        }
    };

    private static long insertAccount(SupportSQLiteDatabase db, String username,
                                      String plainPassword, String role) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", PasswordHasher.sha256(plainPassword));
        values.put("role", role);
        return db.insert("account", SQLiteDatabase.CONFLICT_NONE, values);
    }

    private static void insertEmployee(SupportSQLiteDatabase db, String code, String fullname,
                                       String email, String phone, double hourlyRate,
                                       long accountId) {
        ContentValues values = new ContentValues();
        values.put("code", code);
        values.put("fullname", fullname);
        values.put("email", email);
        values.put("phone", phone);
        values.put("hourlyRate", hourlyRate);
        values.put("accountId", accountId);
        values.put("isActive", 1);
        db.insert("employee", SQLiteDatabase.CONFLICT_NONE, values);
    }

    public abstract AccountDao accountDao();

    public abstract EmployeeDao employeeDao();

    public abstract WorkLogDao workLogDao();

    public abstract SalaryDao salaryDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .addCallback(SEED_CALLBACK)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
