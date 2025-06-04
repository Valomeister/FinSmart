package com.example.finsmart.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finsmart.data.model.Operation;

import java.util.List;

@Dao
public interface OperationDao {
    @Insert
    long insert(Operation operation);

    @Query("SELECT * FROM operation_table WHERE category_id IN " +
            "(SELECT category_id FROM category_table WHERE budget_id = :budgetId)")
    LiveData<List<Operation>> getOperationsByBudget(int budgetId);

    @Query("SELECT * FROM operation_table WHERE category_id = :categoryId")
    LiveData<List<Operation>> getOperationsByCategory(int categoryId);

    @Query("SELECT * FROM operation_table WHERE category_id = :categoryId")
    List<Operation> getOperationsByCategoryNonLineData(int categoryId);

    @Query("SELECT * FROM operation_table ORDER BY date DESC")
    LiveData<List<Operation>> getAllOperations();

    @Query("DELETE FROM operation_table")
    void clearAllTables();
}