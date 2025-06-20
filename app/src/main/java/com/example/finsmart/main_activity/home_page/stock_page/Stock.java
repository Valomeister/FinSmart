package com.example.finsmart.main_activity.home_page.stock_page;

public class Stock {
    private String name;
    private String symbol; // <-- новое поле
    private int quantity;
    private double purchasePrice;
    private double currentPrice;
    private String purchaseDate;

    // Конструктор
    public Stock(String name, String symbol, int quantity, double purchasePrice, double currentPrice, String purchaseDate) {
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.currentPrice = currentPrice;
        this.purchaseDate = purchaseDate;
    }

    // Геттеры
    public String getStockName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    // Вычисляем рыночную стоимость (количество * текущая цена)
    public double getMarketValue() {
        return quantity * currentPrice;
    }

    // Вычисляем доходность в процентах с момента покупки
    public double getReturnPercentage() {
        if (purchasePrice == 0) {
            return 0.0; // избегаем деления на ноль
        }
        return ((currentPrice - purchasePrice) / purchasePrice) * 100;
    }

    // Вычисляем прибыль/убыток в абсолютных значениях
    public double getProfitLoss() {
        return (currentPrice - purchasePrice) * quantity;
    }
}