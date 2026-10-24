package com.loader.thuc_hanh_2_android.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.loader.thuc_hanh_2_android.data.entity.Account;

@Dao
public interface AccountDao {

    @Insert
    long insert(Account account);

    @Query("SELECT * FROM account WHERE username = :username LIMIT 1")
    Account getByUsername(String username);

    @Query("SELECT * FROM account WHERE id = :id LIMIT 1")
    Account getById(long id);
}
