package com.loader.thuc_hanh_2_android.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.loader.thuc_hanh_2_android.data.dao.AccountDao;
import com.loader.thuc_hanh_2_android.data.dao.EmployeeDao;
import com.loader.thuc_hanh_2_android.data.dao.SalaryDao;
import com.loader.thuc_hanh_2_android.data.dao.WorkLogDao;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;

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
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
