package com.loader.thuc_hanh_2_android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.loader.thuc_hanh_2_android.data.entity.Employee;

@Dao
public interface EmployeeDao {

    @Insert
    long insert(Employee employee);

    @Update
    void update(Employee employee);

    @Query("SELECT * FROM employee WHERE id = :id LIMIT 1")
    Employee getById(long id);

    @Query("SELECT * FROM employee WHERE accountId = :accountId LIMIT 1")
    Employee getByAccountId(long accountId);
    @Query("SELECT * FROM employee WHERE isActive = 1 ORDER BY fullname ASC")
    List<Employee> getAllActive();
}
