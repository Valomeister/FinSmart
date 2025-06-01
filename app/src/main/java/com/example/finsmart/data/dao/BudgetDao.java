package com.example.finsmart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finsmart.data.model.Budget;

import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    long insert(Budget budget);

    @Query("SELECT * FROM budget_table WHERE user_id = :userId")
    List<Budget> getAllBudgetsForUser(int userId);

    @Query("SELECT * FROM budget_table WHERE user_id = :userId AND month = :month LIMIT 1")
    Budget getBudgetByMonthForUser(int userId, String month);

    @Query("DELETE FROM budget_table")
    void clearAllTables();
}