package com.example.finsmart;

public class Fund {
    private String fundName;
    private double amount;
    private double dynamics; // Динамика с момента покупки
    private String startDate;

    public Fund(String fundName, double amount, double dynamics, String startDate) {
        this.fundName = fundName;
        this.amount = amount;
        this.dynamics = dynamics;
        this.startDate = startDate;
    }

    // Геттеры
    public String getFundName() {
        return fundName;
    }

    public double getAmount() {
        return amount;
    }

    public double getDynamics() {
        return dynamics;
    }

    public String getStartDate() {
        return startDate;
    }

}
