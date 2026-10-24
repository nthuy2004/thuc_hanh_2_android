package com.loader.thuc_hanh_2_android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.loader.thuc_hanh_2_android.data.entity.WorkLog;

import java.util.List;

/**
 * Data Access Object cho bang WorkLog do P2 phu trach.
 */
@Dao
public interface WorkLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WorkLog workLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WorkLog> workLogs);

    @Update
    int update(WorkLog workLog);

    @Query("SELECT * FROM work_log WHERE id = :id LIMIT 1")
    WorkLog getById(long id);

    @Query("SELECT * FROM work_log WHERE employeeId = :employeeId AND workDate = :workDate LIMIT 1")
    WorkLog getTodayLog(long employeeId, String workDate);

    @Query("SELECT * FROM work_log WHERE employeeId = :employeeId ORDER BY workDate DESC")
    List<WorkLog> getAllByEmployee(long employeeId);

    @Query("SELECT * FROM work_log WHERE employeeId = :employeeId AND workDate LIKE :yearMonthPrefix || '%' ORDER BY workDate DESC")
    List<WorkLog> getLogsByMonth(long employeeId, String yearMonthPrefix);

    @Query("SELECT * FROM work_log WHERE employeeId = :employeeId AND status = 'COMPLETED' AND workDate LIKE :yearMonthPrefix || '%' ORDER BY workDate ASC")
    List<WorkLog> getCompletedLogsByMonth(long employeeId, String yearMonthPrefix);

    @Query("SELECT COUNT(*) FROM work_log")
    int countLogs();
}
