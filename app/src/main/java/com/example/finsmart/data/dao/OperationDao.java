package com.example.finsmart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finsmart.data.model.Operation;

import java.util.List;

@Dao
public interface OperationDao {
    @Insert
    long insert(Operation operation);

    // Получить все операции по ID категории
    @Query("SELECT * FROM operation_table WHERE category_id = :categoryId")
    List<Operation> getOperationsByCategory(int categoryId);

    @Query("DELETE FROM operation_table")
    void clearAllTables();
}