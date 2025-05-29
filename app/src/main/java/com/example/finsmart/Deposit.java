package com.example.finsmart;

// TODO: 30.05.2025 сделать с депозитом как с остальными типами: отдельно сумма и вложенные средства
//  . сумма высчитывается изходя из прошедшего времени и ставки

// TODO: 30.05.2025 добавить в edit возможность задавать пролонгацию и капитализацию 
public class Deposit {
    private String bankName;
    private double amount;
    private double interestRate;
    private String startDate;
    private String duration;
    private boolean hasProlongation;
    private boolean hasCapitalization;

    public Deposit(String bankName, double amount, double interestRate, String startDate, String duration, boolean hasProlongation, boolean hasCapitalization) {
        this.bankName = bankName;
        this.amount = amount;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.duration = duration;
        this.hasProlongation = hasProlongation;
        this.hasCapitalization = hasCapitalization;
    }

    // Геттеры

    public String getBankName() {
        return bankName;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isHasProlongation() {
        return hasProlongation;
    }

    public boolean isHasCapitalization() {
        return hasCapitalization;
    }
}