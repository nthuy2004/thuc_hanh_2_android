package com.loader.thuc_hanh_2_android.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.loader.thuc_hanh_2_android.data.entity.Employee;

import java.util.List;

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

    /** Danh sach nhan vien chua bi xoa mem, sap xep theo ten. */
    @Query("SELECT * FROM employee WHERE isActive = 1 ORDER BY fullname COLLATE NOCASE")
    LiveData<List<Employee>> observeAllActive();

    /** Tim theo ten hoac ma (keyword da escape % _ \ ; keyword rong = tat ca). */
    @Query("SELECT * FROM employee WHERE isActive = 1 AND ("
            + "fullname LIKE '%' || :keyword || '%' ESCAPE '\\' "
            + "OR code LIKE '%' || :keyword || '%' ESCAPE '\\') "
            + "ORDER BY fullname COLLATE NOCASE")
    LiveData<List<Employee>> search(String keyword);

    /** Dem nhan vien (ke ca da xoa mem) co cung ma, bo qua id excludeId. */
    @Query("SELECT COUNT(*) FROM employee WHERE code = :code COLLATE NOCASE AND id != :excludeId")
    int countByCode(String code, long excludeId);

    @Query("SELECT COUNT(*) FROM employee WHERE LOWER(email) = LOWER(:email) AND id != :excludeId")
    int countByEmail(String email, long excludeId);

    @Query("UPDATE employee SET isActive = 0 WHERE id = :id")
    void softDelete(long id);

    @Query("SELECT * FROM employee WHERE isActive = 1 ORDER BY fullname ASC")
    List<Employee> getAllActive();
}
