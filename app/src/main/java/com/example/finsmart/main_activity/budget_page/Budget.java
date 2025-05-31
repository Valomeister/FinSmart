package com.example.finsmart.main_activity.budget_page;

import java.util.List;

public class Budget {
    private int id;
    private String month; // например "03/24"
    private List<IncomeEntry> incomeList;
    private List<ExpenseEntry> expenseList;

    public Budget(int id, String month) {
        this.id = id;
        this.month = month;
    }

    public Budget(String month) {
        this(-1, month); // -1 = ещё не сохранён
    }

    // --- Геттеры ---
    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }
    public String getMonth() {
        return month;
    }

    public List<IncomeEntry> getIncomeList() {
        return incomeList;
    }

    public List<ExpenseEntry> getExpenseList() {
        return expenseList;
    }

    // --- Методы добавления статей ---
    public void addIncome(String name, double amount) {
        incomeList.add(new IncomeEntry(name, amount));
    }

    public void addExpense(String name, double amount) {
        expenseList.add(new ExpenseEntry(name, amount));
    }

    // --- Расчеты ---
    public double getTotalIncome() {
        double total = 0;
        for (IncomeEntry entry : incomeList) {
            total += entry.getAmount();
        }
        return total;
    }

    public double getTotalExpenses() {
        double total = 0;
        for (ExpenseEntry entry : expenseList) {
            total += entry.getAmount();
        }
        return total;
    }

    public double getNetBudget() {
        return getTotalIncome() - getTotalExpenses();
    }

    // --- Для отладки ---
    @Override
    public String toString() {
        return "Budget{" +
                "month='" + month + '\'' +
                ", totalIncome=" + getTotalIncome() +
                ", totalExpenses=" + getTotalExpenses() +
                ", netBudget=" + getNetBudget() +
                '}';
    }
}