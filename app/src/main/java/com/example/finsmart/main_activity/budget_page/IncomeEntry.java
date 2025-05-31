package com.example.finsmart.main_activity.budget_page;

public class IncomeEntry {
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
}