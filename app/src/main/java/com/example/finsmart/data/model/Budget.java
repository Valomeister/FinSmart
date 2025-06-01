package com.example.finsmart.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_table",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE))
public class Budget {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budget_id")
    private int budgetId;

    @ColumnInfo(name = "user_id", index = true)
    private int userId;

    private String month;

    public Budget(int userId, String month) {
        this.userId = userId;
        this.month = month;
    }

    // Геттеры и сеттеры
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
}