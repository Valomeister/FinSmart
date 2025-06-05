package com.example.finsmart.main_activity.home_page;

public class CryptoDataPoint {
    private final String date;
    private final double price;

    public CryptoDataPoint(String date, double price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }
}