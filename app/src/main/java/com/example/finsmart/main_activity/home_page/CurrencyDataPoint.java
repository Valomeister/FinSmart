package com.example.finsmart.main_activity.home_page;

public class CurrencyDataPoint {
    private final String date;
    private final double rate;

    public CurrencyDataPoint(String date, double rate) {
        this.date = date;
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public double getRate() {
        return rate;
    }
}