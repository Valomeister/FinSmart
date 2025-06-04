package com.example.finsmart.main_activity.operations_page;

import androidx.annotation.NonNull;

import com.example.finsmart.data.model.Operation;

import java.util.List;
import java.util.stream.Collectors;

public class OperationWithDate {
    private final String date;
    private final Operation operation;
    boolean isFirst = false;

    public OperationWithDate(String date, Operation operation) {
        this.date = date;
        this.operation = operation;
    }

    public boolean isHeader() {
        return operation == null;
    }

    public String getDate() {
        return date;
    }

    public Operation getOperation() {
        return operation;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }
}