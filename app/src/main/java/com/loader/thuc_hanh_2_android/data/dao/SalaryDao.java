package com.loader.thuc_hanh_2_android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.loader.thuc_hanh_2_android.data.entity.Salary;

/** Skeleton do P1 tao; P3 bo sung generateForMonth va cac query luong. */
@Dao
public interface SalaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Salary salary);

    @Query("SELECT * FROM salary WHERE id = :id LIMIT 1")
    Salary getById(long id);
}
