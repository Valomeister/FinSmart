package com.example.finsmart.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.finsmart.data.model.common.CategoryType;

@Entity(tableName = "category_table",
        foreignKeys = @ForeignKey(entity = Budget.class,
                parentColumns = "budget_id",
                childColumns = "budget_id",
                onDelete = ForeignKey.CASCADE))
public class Category {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "budget_id", index = true)
    private int budgetId;

    private String name;

    @ColumnInfo(name = "category_type")
    private CategoryType type;

    @ColumnInfo(name = "planned_limit")
    private int plannedLimit;

    private String emoji;

    public Category(int budgetId, String name, CategoryType type, int plannedLimit, String emoji) {
        this.budgetId = budgetId;
        this.name = name;
        this.type = type;
        this.plannedLimit = plannedLimit;
        this.emoji = emoji;
    }

    // Геттеры и сеттеры

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public int getPlannedLimit() {
        return plannedLimit;
    }

    public void setPlannedLimit(int plannedLimit) {
        this.plannedLimit = plannedLimit;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}