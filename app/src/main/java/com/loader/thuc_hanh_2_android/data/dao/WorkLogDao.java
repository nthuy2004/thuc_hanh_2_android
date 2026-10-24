package com.loader.thuc_hanh_2_android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.loader.thuc_hanh_2_android.data.entity.WorkLog;

/** Skeleton do P1 tao; P2 bo sung cac query check-in/out va tong hop thang. */
@Dao
public interface WorkLogDao {

    @Insert
    long insert(WorkLog workLog);

    @Query("SELECT * FROM work_log WHERE id = :id LIMIT 1")
    WorkLog getById(long id);
}
