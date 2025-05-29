package com.example.finsmart;

public class Currency {
    private String name;
    private String code;
    private char symbol;
    private double quantity;
    private double purchaseExchangeRate;
    private double currentExchangeRate;
    private String purchaseDate;

    public Currency(String name, String code, char symbol, double quantity, double purchaseExchangeRate,
                    double currentExchangeRate, String purchaseDate) {
        this.name = name;
        this.code = code;
        this.symbol = symbol;
        this.quantity = quantity;
        this.purchaseExchangeRate = purchaseExchangeRate;
        this.currentExchangeRate = currentExchangeRate;
        this.purchaseDate = purchaseDate;
    }

    // Геттеры

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPurchaseExchangeRate() {
        return purchaseExchangeRate;
    }

    public double getCurrentExchangeRate() {
        return currentExchangeRate;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    // Расчёт рыночной стоимости
    public double getMarketValue() {
        return quantity * currentExchangeRate;
    }

    // Расчёт доходности в процентах
    public double getReturnPercentage() {
        if (purchaseExchangeRate == 0) {
            return 0.0; // избегаем деления на ноль
        }
        return ((currentExchangeRate - purchaseExchangeRate) / purchaseExchangeRate) * 100;
    }

    // Расчёт прибыли/убытка
    public double getProfitLoss() {
        return (currentExchangeRate - purchaseExchangeRate) * quantity;
    }

    public char getSymbol() {
        return symbol;
    }
}