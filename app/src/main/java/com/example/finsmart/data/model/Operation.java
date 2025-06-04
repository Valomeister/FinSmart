package com.example.finsmart.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.finsmart.data.model.common.FlowType;

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

    private String categoryName;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "category_type")
    private FlowType type;





    public Operation(double sum, Integer categoryId, String categoryName, String date, String title, FlowType type) {
        this.sum = sum;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.date = date;
        this.title = title;
        this.type = type;
    }

    // Геттеры и сеттеры
    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public String getCategoryName() {
        return categoryName;
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

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public FlowType getType() {
        return type;
    }
    public void setType(FlowType type) {
        this.type = type;
    }
}