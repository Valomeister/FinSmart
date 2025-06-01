package com.example.finsmart.main_activity.budget_page;

import android.os.Parcel;
import android.os.Parcelable;

public class IncomeEntry implements Parcelable {
    private int id;
    private int budgetId;
    private String name;
    private double amount;

    public IncomeEntry(int id, int budgetId, String name, double amount) {
        this.id = id;
        this.budgetId = budgetId;
        this.name = name;
        this.amount = amount;
    }

    public IncomeEntry(String name, double amount) {
        this(-1, -1, name, amount);
    }

    // --- Parcelable ---
    protected IncomeEntry(Parcel in) {
        id = in.readInt();
        budgetId = in.readInt();
        name = in.readString();
        amount = in.readDouble();
    }

    public static final Creator<IncomeEntry> CREATOR = new Creator<IncomeEntry>() {
        @Override
        public IncomeEntry createFromParcel(Parcel in) {
            return new IncomeEntry(in);
        }

        @Override
        public IncomeEntry[] newArray(int size) {
            return new IncomeEntry[size];
        }
    };

    // --- Геттеры и сеттеры ---
    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // --- Parcelable методы ---
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(budgetId);
        dest.writeString(name);
        dest.writeDouble(amount);
    }
}