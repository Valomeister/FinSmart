package com.example.finsmart.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "operation_table",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Operation {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "operation_id")
    private int operationId;

    private double sum; // <-- изменено на double

    @ColumnInfo(name = "category_id", index = true)
    private Integer categoryId;

    public Operation(double sum, Integer categoryId) {
        this.sum = sum;
        this.categoryId = categoryId;
    }

    // Геттеры и сеттеры
    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}