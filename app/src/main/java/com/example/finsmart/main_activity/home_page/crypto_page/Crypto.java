package com.example.finsmart.main_activity.home_page.crypto_page;

public class Crypto {
    private String name;
    private String symbol; // например, BTC, ETH
    private double quantity;
    private double buyInPrice; // цена покупки в fiat (например, USD)
    private double currentPrice; // текущая цена в fiat
    private String purchaseDate;

    public Crypto(String name, String symbol, double quantity,
                  double buyInPrice, double currentPrice, String purchaseDate) {
        this.name = name;
        this.symbol = symbol;
        this.quantity = quantity;
        this.buyInPrice = buyInPrice;
        this.currentPrice = currentPrice;
        this.purchaseDate = purchaseDate;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getBuyInPrice() {
        return buyInPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    // Расчёт рыночной стоимости
    public double getMarketValue() {
        return quantity * currentPrice;
    }

    // Расчёт доходности в процентах
    public double getReturnPercentage() {
        if (buyInPrice == 0) {
            return 0.0; // избегаем деления на ноль
        }
        return ((currentPrice - buyInPrice) / buyInPrice) * 100;
    }

    // Расчёт прибыли/убытка
    public double getProfitLoss() {
        return (currentPrice - buyInPrice) * quantity;
    }
}