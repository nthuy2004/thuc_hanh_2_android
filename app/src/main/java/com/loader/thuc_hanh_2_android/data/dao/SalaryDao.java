package com.loader.thuc_hanh_2_android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.loader.thuc_hanh_2_android.data.entity.Salary;

import java.util.List;

@Dao
public interface SalaryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Salary salary);

    @Update
    int update(Salary salary);

    @Query("SELECT * FROM salary WHERE id = :id LIMIT 1")
    Salary getById(long id);

    @Query("SELECT * FROM salary WHERE employeeId = :employeeId AND month = :month AND year = :year LIMIT 1")
    Salary getByEmployeeAndPeriod(long employeeId, int month, int year);

    @Query("SELECT * FROM salary WHERE month = :month AND year = :year ORDER BY employeeId ASC")
    List<Salary> getByPeriod(int month, int year);

    @Query("SELECT * FROM salary WHERE employeeId = :employeeId ORDER BY year DESC, month DESC")
    List<Salary> getByEmployee(long employeeId);

    @Query("UPDATE salary SET status = :status WHERE id = :salaryId")
    int updateStatus(long salaryId, String status);
}