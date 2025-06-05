package com.example.finsmart.main_activity.home_page;

public class StockDataPoint {
    private final String date;
    private final double closePrice;

    public StockDataPoint(String date, double closePrice) {
        this.date = date;
        this.closePrice = closePrice;
    }

    public String getDate() { return date; }
    public double getClosePrice() { return closePrice; }
}