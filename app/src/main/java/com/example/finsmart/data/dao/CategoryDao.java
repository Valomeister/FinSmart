package com.example.finsmart.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.common.CategoryType;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Query("SELECT * FROM category_table WHERE budget_id = :budgetId")
    List<Category> getCategoriesByBudget(int budgetId);

    @Query("SELECT * FROM category_table WHERE budget_id = :budgetId AND category_type = :type")
    List<Category> getCategoriesByBudgetAndType(int budgetId, CategoryType type);

    @Query("DELETE FROM category_table")
    void clearAllTables();
}