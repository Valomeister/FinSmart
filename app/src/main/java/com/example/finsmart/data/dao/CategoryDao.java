package com.example.finsmart.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.common.FlowType;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);
    @Insert
    void insertAll(List<Category> categories);

    @Query("SELECT * FROM category_table WHERE budget_id = :budgetId")
    List<Category> getCategoriesByBudget(int budgetId);

    @Query("SELECT * FROM category_table WHERE budget_id = :budgetId AND category_type = :type")
    LiveData<List<Category>> getCategoriesByBudgetAndType(int budgetId, FlowType type);

    @Query("DELETE FROM category_table")
    void clearAllTables();
}