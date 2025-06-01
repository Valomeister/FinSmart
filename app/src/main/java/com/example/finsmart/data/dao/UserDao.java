package com.example.finsmart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finsmart.data.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM user_table")
    List<User> getAllUsers();

    @Query("DELETE FROM user_table")
    void clearAllTables();
}