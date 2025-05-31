package com.example.finsmart.main_activity.home_page.fund_page;

public class Fund {
    private String fundName;
    private double amount;         // Сумма (в рублях)
    private double investedSum;    // Вложено средств
    private double dynamics;       // Динамика с момента покупки (%)
    private String startDate;      // Дата вложения

    public Fund(String fundName, double amount, double investedSum, String startDate) {
        this.fundName = fundName;
        this.amount = amount;
        this.investedSum = investedSum;
        this.startDate = startDate;
    }

    // Геттеры
    public String getFundName() {
        return fundName;
    }

    public double getAmount() {
        return amount;
    }

    public double getInvestedSum() {
        return investedSum;
    }

    public double getDynamics() {
        return amount / investedSum;
    }

    public String getStartDate() {
        return startDate;
    }
}